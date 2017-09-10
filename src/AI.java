import java.util.ArrayList;
import java.util.Collections;

public class AI
{
	//Board properties multiplied by polynomialCoefficients values in that order to calculate favourability of a board to a player
	//{RedPieces,-BlackPieces,RedKingPieces,-BlackKingPieces}
	static ArrayList<Float> polynomialCoefficients;
	CheckersData data;
	int depth = 9;
	ArrayList<ArrayList<CheckersMoveScore>> branchMoves;
	CheckersMoveScore prevMove;
	//Max time allowed in IncreaseDepth
	int timePerIncreaseDepth = 25000;
	//Max size of list in branchMoves
	float maxBranchSize = 350000;
	
	public AI(CheckersData data)
	{		
		this.data = data;
		polynomialCoefficients = new ArrayList<Float>();
		polynomialCoefficients.add(.1f);
		polynomialCoefficients.add(.1f);
		polynomialCoefficients.add(.13f);
		polynomialCoefficients.add(.13f);
	}
	
	
	//Favourability board position for player red
	//Returns float from low(bad for red) to high(good for red)
	static float ScoreBoard(CheckersData dataLocal)
	{
		int RedPieces=0; int BlackPieces = 0;
		int RedKingPieces = 0; int BlackKingPieces = 0;
		
		//Calculate above variables
		for(int i = 0;i < 8;i+=2)
		{
			for(int j = 1;j < 8;j+=2)
			{
				if(dataLocal.board.Get(i, j) == CheckersData.RED)
				{
					RedPieces++;
				}
				else if(dataLocal.board.Get(i, j) == CheckersData.BLACK)
				{
					BlackPieces++;
				}
				else if(dataLocal.board.Get(i, j) == CheckersData.RED_KING)
				{
					RedKingPieces++;
				}
				else if(dataLocal.board.Get(i, j) == CheckersData.BLACK_KING)
				{
					BlackKingPieces++;		
				}
			}
		}
		for(int i = 1;i < 8;i+=2)
		{
			for(int j = 0;j < 8;j+=2)
			{
				if(dataLocal.board.Get(i, j) == CheckersData.RED)
				{
					RedPieces++;
				}
				else if(dataLocal.board.Get(i, j) == CheckersData.BLACK)
				{
					BlackPieces++;
				}
				else if(dataLocal.board.Get(i, j) == CheckersData.RED_KING)
				{
					RedKingPieces++;
				}
				else if(dataLocal.board.Get(i, j) == CheckersData.BLACK_KING)
				{
					BlackKingPieces++;		
				}
			}
		}
		//Return score of favourability of board relative to red player
		float score = RedPieces*polynomialCoefficients.get(0) - BlackPieces*polynomialCoefficients.get(1)
				 	+ RedKingPieces*polynomialCoefficients.get(2) - BlackKingPieces*polynomialCoefficients.get(3);
		return score;
	}
	
	//Calculates score of board as far as depth moves ahead
	//Makes move based on scores updated back from score of boards of furthest depth
	public void MakeAIMove()
	{	
		CheckersMove[] moves = data.getLegalMoves(CheckersData.RED);
		float[] scores = new float[moves.length];
		ArrayList<CheckersMoveScore> moveScores = new ArrayList<CheckersMoveScore>();
		
		//branchMoves null on first MakeAIMove call or if failure in finding board in previous branchMoves
		//Calculate first list of moves for brancMoves
		if(branchMoves == null)
		{
			for(int i = 0;i < moves.length;i++)
			{
				CheckersData localData = new CheckersData(data);
				byte playerMove = localData.currentPlayer;
				byte playerMoveNext = localData.currentPlayer;
				
				localData.makeMove(moves[i]);
				scores[i] = ScoreBoard(localData);
				if (moves[i].isJump())
				{
					CheckersMove[] localCheckersMove = localData.getLegalJumpsFrom(localData.currentPlayer,moves[i].toRow,moves[i].toCol);
			         if (localCheckersMove == null) 
			         {
			        	 if (localData.currentPlayer == CheckersData.RED)
		        		 	  playerMoveNext = CheckersData.BLACK;
			              else
			            	  playerMoveNext = CheckersData.RED;
			         }
				}
		         else if (localData.currentPlayer == CheckersData.RED)
	        	 		  playerMoveNext = CheckersData.BLACK;
		              else
		            	  playerMoveNext = CheckersData.RED;
				localData.currentPlayer = playerMoveNext;
				moveScores.add(new CheckersMoveScore(scores[i],moves[i],localData,playerMove,playerMoveNext));
			}
			branchMoves = new ArrayList<ArrayList<CheckersMoveScore>>();
			branchMoves.add(moveScores);			
		}
		//Find current board in branchMoves
		//Make new branchMoves starting from this point in old branchMoves
		else
		{
			ArrayList<ArrayList<CheckersMoveScore>> localBranchMoves = new ArrayList<ArrayList<CheckersMoveScore>>();
			ArrayList<ArrayList<CheckersMoveScore>> oldMoves = new ArrayList<ArrayList<CheckersMoveScore>>();
			ArrayList<CheckersMoveScore> localDepthMoves1 = new ArrayList<CheckersMoveScore>();
			ArrayList<CheckersMoveScore> localDepthMoves3 = new ArrayList<CheckersMoveScore>();
			if(prevMove.moves == null)
			{
				System.out.println("prevMove moves is null");
				branchMoves = null;
				MakeAIMove();
				return;
			}
			oldMoves.add(prevMove.moves);
			boolean boardFound = false;
			int depthBoardFind = 5;
			//Find current board in oldMoves
			for(int j = 0; j < depthBoardFind;j++)
			{
				localDepthMoves3 = new ArrayList<CheckersMoveScore>();
				for(int i = 0; i < oldMoves.get(j).size();i++)
				{
					if(data.equals(oldMoves.get(j).get(i).board))
					{
						//Found current board in oldMoves
						localDepthMoves1 = oldMoves.get(j).get(i).moves;
						if(localDepthMoves1 == null)
						{
							branchMoves = null;
							System.out.println("moves of prevMove null");
							MakeAIMove();
							return;
						}
						if(localDepthMoves1.size() == 1)
						{
							//Make only move
							data.canvas.doMakeMove(localDepthMoves1.get(0).move);
							//Store move made to be referred to in next move
							prevMove = localDepthMoves1.get(0);
							return;
						}
						boardFound = true;
						break;
					}
					if(oldMoves.get(j).get(i).moves != null)
						localDepthMoves3.addAll(oldMoves.get(j).get(i).moves);
				}
				if(boardFound || localDepthMoves3 == null || localDepthMoves3.size() == 0)
					break;
				oldMoves.add(localDepthMoves3);
			}
			if(localDepthMoves1.size() == 0)
			{
				branchMoves = null;
				System.out.println("Board not found on prev moves");
				MakeAIMove();
				return;
			}
			moveScores = localDepthMoves1;
			//Add to localBranchMoves, each branch of moves in localDepthMoves1 and its moves and moves.... 
			localBranchMoves.add(localDepthMoves1);
			while(localDepthMoves1 != null && localDepthMoves1.size() > 0)
			{
				ArrayList<CheckersMoveScore> localDepthMoves2 = new ArrayList<CheckersMoveScore>();
				for(int j = 0;j < localDepthMoves1.size();j++)
				{
					CheckersMoveScore localMove = localDepthMoves1.get(j);
					if(!localMove.goalState && localMove.moves != null)
					{
						for(int k = 0;k < localMove.moves.size();k++)
						{
							localDepthMoves2.add(localMove.moves.get(k));
						}
					}
				}
				if(localDepthMoves2 != null && localDepthMoves2.size() > 0)
					localBranchMoves.add(localDepthMoves2);
				localDepthMoves1 = localDepthMoves2;
			}
			//Finished new branchMoves
			branchMoves = new ArrayList<ArrayList<CheckersMoveScore>>();
			branchMoves = localBranchMoves;
		}
		//Try adding as many branches to depthMoves so depthMoves is size deoth
		int branchSize = branchMoves.size();
		int localDepth = depth - branchSize;
		for(int i = 0;i < localDepth;i++)
		{
			//If time limit reached stop adding branches to brancMoves
			if(!IncreaseDepth(branchMoves))
			{
				break;
			}
			//Remove poor moves from branch
			//Sorted by score from low to high
			Collections.sort(branchMoves.get(branchMoves.size()-1));
			while(branchMoves.get(branchMoves.size()-1).size() > maxBranchSize)
			{
				if(branchMoves.get(branchMoves.size()-1).get(0).playerMove == CheckersData.RED)
					branchMoves.get(branchMoves.size()-1).remove(0);
				else if(branchMoves.get(branchMoves.size()-1).get(branchMoves.get(branchMoves.size()-1).size()-1).playerMove == CheckersData.BLACK)
					branchMoves.get(branchMoves.size()-1).remove(branchMoves.get(branchMoves.size()-1).size()-1);
				else
					break;
			}
			//Add moves added by IncreaseDepth to depthMoves
			ArrayList<CheckersMoveScore> localDepthMoves = new ArrayList<CheckersMoveScore>();
			for(int j = 0;j < branchMoves.get(branchMoves.size()-1).size();j++)
			{
				CheckersMoveScore localMove = branchMoves.get(branchMoves.size()-1).get(j);
				if(!localMove.goalState)
				{
					for(int k = 0;k < localMove.moves.size();k++)
					{
						localDepthMoves.add(localMove.moves.get(k));
					}
				}
			}
			if(localDepthMoves != null && localDepthMoves.size() >  0)
				branchMoves.add(localDepthMoves);
		}
		System.out.println("Branch Depth: " + branchMoves.size());
		
		//Choose index of best scoring move
		int bestMoveIndex = 0;
		for(int i = 0;i < moveScores.size();i++)
		{
			if(moveScores.get(i).score > moveScores.get(bestMoveIndex).score)
			{
				bestMoveIndex = i;
			}
		}
		//Make best move
		data.canvas.doMakeMove(moveScores.get(bestMoveIndex).move);
		//Store move made to be referred to in next move
		prevMove = moveScores.get(bestMoveIndex);
	}
	
	//Calculate moves for last list of branchMoves' last list
	//Updates scores back through branchMoves
	//Return true if time limit timePerIncreaseDepth not reached, false otherwise
	private boolean IncreaseDepth(ArrayList<ArrayList<CheckersMoveScore>> branchMoves)
	{		
		//Calculate scores for each move in new depth
		//Thread[] depthIncThreads = new Thread[branchMoves.get(maxDepth).size()];
		long startTime = System.currentTimeMillis();
		for(int i = 0;i < branchMoves.get(branchMoves.size()-1).size();i++)
		{
			CheckersData localData = new CheckersData(branchMoves.get(branchMoves.size()-1).get(i).board);
			CheckersData localDataTemp = new CheckersData(branchMoves.get(branchMoves.size()-1).get(i).board);
			ArrayList<CheckersMoveScore> localMoveScores = new ArrayList<CheckersMoveScore>();
			CheckersMove[] moves = localData.getLegalMoves(localData.currentPlayer);
			if(!branchMoves.get(branchMoves.size()-1).get(i).goalState)
			{
				for(int j = 0;j < moves.length;j++)
				{
					localData = new CheckersData(localDataTemp);
					byte playerMove = localData.currentPlayer;
					byte playerMoveNext = localData.currentPlayer;
					localData.makeMove(moves[j]);
					float score = ScoreBoard(localData);
					if (moves[j].isJump()) 
					{
						CheckersMove[] localCheckersMove = localData.getLegalJumpsFrom(localData.currentPlayer,moves[j].toRow,moves[j].toCol);
						if (localCheckersMove == null) 
						{
							if (localData.currentPlayer == CheckersData.RED)
								playerMoveNext = CheckersData.BLACK;
			              	else
			              		playerMoveNext = CheckersData.RED;
						}
					}
			         else if (localData.currentPlayer == CheckersData.RED)
			        	 	  playerMoveNext = CheckersData.BLACK;
			              else
			            	  playerMoveNext = CheckersData.RED;
					localData.currentPlayer = playerMoveNext;
					localMoveScores.add(new CheckersMoveScore(score,moves[j],localData,playerMove,playerMoveNext));
				}	
				branchMoves.get(branchMoves.size()-1).get(i).IncreaseDepth(localMoveScores);

			}
			long endTime = System.currentTimeMillis();
			if(endTime - startTime > timePerIncreaseDepth)
			{
				return false;
			}
		}

		//Update scores back through each branchMove
		for(int i = branchMoves.size()-1;i >= 0;i--)
		{
			for(int k = 0;k < branchMoves.get(i).size();k++)
			{
				CheckersMoveScore localMove = branchMoves.get(i).get(k);
				int scoreIndex = 0;
				if(localMove.moves != null && !localMove.goalState)
				{
					for(int j = 0;j < localMove.moves.size();j++)
					{
						if(localMove.playerMove == CheckersData.RED)
						{
							if(localMove.moves.get(j).score > localMove.moves.get(scoreIndex).score)
							{
								scoreIndex = j;
							}
						}
						else
						{
							if(localMove.moves.get(j).score < localMove.moves.get(scoreIndex).score)
							{
								scoreIndex = j;
							}
						}
					}
					localMove.UpdateScore(branchMoves.get(i).get(k).moves.get(scoreIndex).score);
				}
			}
		}
		return true;
	}
}
