package checkersAIPackage;

import java.util.ArrayList;
import java.util.Collections;

public class AI
{
	Polynomial poly;
	CheckersData data;
	byte player, otherPlayer;
	int depth = 7;
	ArrayList<ArrayList<CheckersMoveScore>> branchMoves;
	CheckersMoveScore prevMove;
	//Max time allowed in IncreaseDepth
	int timePerIncreaseDepth = 25000;
	//Max size of list in branchMoves
	float maxBranchSize = 300000;
	
	public AI(CheckersData data,byte newPlayer,byte newOtherPlayer)
	{		
		this.data = data;
		player = newPlayer;
		otherPlayer = newOtherPlayer;
		//Board properties multiplied by polynomialCoefficients values in that order to calculate favourability of a board to a player
		//{RedPieces,-BlackPieces,RedKingPieces,-BlackKingPieces,RedAdjacent,-BlackAdjacent,
		//RedCentre,-BlackCentre,RedToKing,-BlackToKing}
		ArrayList<Float>  polynomialCoefficients = new ArrayList<Float>();
		polynomialCoefficients.add(.1f);
		polynomialCoefficients.add(.13f);
		polynomialCoefficients.add(.01f);
		polynomialCoefficients.add(.03f);
		polynomialCoefficients.add(.005f);
		poly = new Polynomial(polynomialCoefficients);
	}	
	
	public AI(CheckersData data,byte newPlayer,byte newOtherPlayer,Polynomial newPoly)
	{
		this.data = data;
		player = newPlayer;
		otherPlayer = newOtherPlayer;
		poly = newPoly;
	}
	
	public void updatePolynomial(Polynomial newPoly)
	{
		poly = new Polynomial(newPoly);
	}
	
	//Calculates score of board as far as depth moves ahead
	//Makes move based on scores updated back from score of boards of furthest depth
	public void makeAIMove()
	{	
		CheckersMove[] moves = data.getLegalMoves(player);
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
				scores[i] = poly.scoreBoard(localData);
				if (moves[i].isJump())
				{
					CheckersMove[] localCheckersMove = localData.getLegalJumpsFrom(localData.currentPlayer,moves[i].toRow,moves[i].toCol);
			         if (localCheckersMove == null) 
			         {
			        	 if (localData.currentPlayer == player)
		        		 	  playerMoveNext = otherPlayer;
			              else
			            	  playerMoveNext = player;
			         }
				}
		         else if (localData.currentPlayer == player)
	        	 		  playerMoveNext = otherPlayer;
		              else
		            	  playerMoveNext = player;
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
				makeAIMove();
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
							makeAIMove();
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
				makeAIMove();
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
					if(!localMove.finalState && localMove.moves != null)
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
			if(!increaseDepth())
			{
				break;
			}
			//Remove poor moves from branch
			//Sorted by score from low to high
			//Removes low scores for Red moves, high scores for Black moves
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
				if(!localMove.finalState)
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
	boolean increaseDepth()
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
			if(!branchMoves.get(branchMoves.size()-1).get(i).finalState)
			{
				for(int j = 0;j < moves.length;j++)
				{
					localData = new CheckersData(localDataTemp);
					byte playerMove = localData.currentPlayer;
					byte playerMoveNext = localData.currentPlayer;
					localData.makeMove(moves[j]);
					float score = poly.scoreBoard(localData);
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
				if(localMove.moves != null && !localMove.finalState)
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
