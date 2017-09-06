import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;

public class AI
{
	//Coefficients range from 0 to 1
	//Entries multiplied by below values in that order to calculate favourability of a board to a player
	//RedPieces,-BlackPieces,RedKingPieces,-BlackKingPieces,RedHoppingOpportunities,-BlackHoppingOpportunities,RedCenters,-BlackCenters
	static ArrayList<Float> polynomialCoefficients;
	CheckersData data;
	int depth = 7;
	ArrayList<ArrayList<CheckersMoveScore>> branchMoves;
	CheckersMoveScore prevMove;
	int branchSizeAvg = 0;
	int branches = 0;
	
	//@SuppressWarnings("unchecked")
	public AI(CheckersData data)
	{		
		this.data = data;
		/*try
		{
			FileInputStream fileStream = new FileInputStream("coeff.dat");
		
			ObjectInputStream os = new ObjectInputStream(fileStream);
			
			polynomialCoefficients = (ArrayList<Float>)os.readObject();

			os.close();
		
		}
		//Catch Input errors
		catch(Exception e)
		{*/
			//e.printStackTrace();
			polynomialCoefficients = new ArrayList<Float>();
			polynomialCoefficients.add(.1f);
			polynomialCoefficients.add(.1f);
			polynomialCoefficients.add(.13f);
			polynomialCoefficients.add(.13f);
			polynomialCoefficients.add(.0f);
			polynomialCoefficients.add(.0f);
			polynomialCoefficients.add(.0f);
			polynomialCoefficients.add(.0f);
			//WriteCoeff();
		//}
	}
	//Favourability board position for player red
	//Returns float from low(bad for red) to high(good for red)
	static float ScoreBoard(CheckersData dataLocal)
	{
		int RedPieces=0; int BlackPieces = 0;
		int RedKingPieces = 0; int BlackKingPieces = 0;
		int RedHoppingOpportunities = 0; int BlackHoppingOpportunities = 0;
		int RedCenters = 0; int BlackCenters = 0;
		
		//Calculate above variables
		for(int i = 0;i < 8;i++)
		{
			for(int j = 0;j < 8;j++)
			{
				if(dataLocal.board.Get(i, j) == CheckersData.RED)
				{
					RedPieces++;
					/*if(( i == 2 || i == 4) && j == 4)
					{
						RedCenters++;
					}
					if(dataLocal.canJump(CheckersData.RED, i, j, i-1, j-1, i-2, j-2))
					{
						RedHoppingOpportunities++;
					}
					if(dataLocal.canJump(CheckersData.RED, i, j, i+1, j-1, i+2, j-2))
					{
						RedHoppingOpportunities++;
					}*/
				}
				else if(dataLocal.board.Get(i, j) == CheckersData.BLACK)
				{
					BlackPieces++;
					/*if(( i == 3 || i == 5) && j == 3)
					{
						BlackCenters++;
					}
					if(dataLocal.canJump(CheckersData.BLACK, i, j, i-1, j+1, i-2, j+2))
					{
						BlackHoppingOpportunities++;
					}
					if(dataLocal.canJump(CheckersData.BLACK, i, j, i+1, j+1, i+2, j+2))
					{
						BlackHoppingOpportunities++;
					}*/
				}
				else if(dataLocal.board.Get(i, j) == CheckersData.RED_KING)
				{
					RedKingPieces++;
					/*if(( i == 2 || i == 4) && j == 4)
					{
						RedCenters++;
					}
					if(dataLocal.canJump(CheckersData.RED, i, j, i-1, j-1, i-2, j-2))
					{
						RedHoppingOpportunities++;
					}
					if(dataLocal.canJump(CheckersData.RED, i, j, i+1, j-1, i+2, j-2))
					{
						RedHoppingOpportunities++;
					}
					if(dataLocal.canJump(CheckersData.RED, i, j, i-1, j+1, i-2, j+2))
					{
						RedHoppingOpportunities++;
					}
					if(dataLocal.canJump(CheckersData.RED, i, j, i+1, j+1, i+2, j+2))
					{
						RedHoppingOpportunities++;
					}*/
				}
				else if(dataLocal.board.Get(i, j) == CheckersData.BLACK_KING)
				{
					BlackKingPieces++;
					/*if(( i == 3 || i == 5) && j == 3)
					{
						BlackCenters++;
					}
					if(dataLocal.canJump(CheckersData.BLACK, i, j, i-1, j+1, i-2, j+2))
					{
						BlackHoppingOpportunities++;
					}
					if(dataLocal.canJump(CheckersData.BLACK, i, j, i+1, j+1, i+2, j+2))
					{
						BlackHoppingOpportunities++;
					}
					if(dataLocal.canJump(CheckersData.BLACK, i, j, i-1, j+1, i-2, j+2))
					{
						BlackHoppingOpportunities++;
					}
					if(dataLocal.canJump(CheckersData.BLACK, i, j, i+1, j+1, i+2, j+2))
					{
						BlackHoppingOpportunities++;
					}	*/				
				}
			}
		}
		float score = RedPieces*polynomialCoefficients.get(0) - BlackPieces*polynomialCoefficients.get(1)
				 	+ RedKingPieces*polynomialCoefficients.get(2) - BlackKingPieces*polynomialCoefficients.get(3)
				 	+ RedHoppingOpportunities*polynomialCoefficients.get(4) - BlackHoppingOpportunities*polynomialCoefficients.get(5)
				 	+ RedCenters*polynomialCoefficients.get(6) - BlackCenters*polynomialCoefficients.get(7);
		return score;
	}
	
	public void MakeAIMove()
	{		
		CheckersMove[] moves = data.getLegalMoves(CheckersData.RED);
		float[] scores = new float[moves.length];
		ArrayList<CheckersMoveScore> moveScores = new ArrayList<CheckersMoveScore>();

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
		else
		{
			ArrayList<ArrayList<CheckersMoveScore>> localBranchMoves = new ArrayList<ArrayList<CheckersMoveScore>>();
			ArrayList<CheckersMoveScore> localDepthMoves1 = new ArrayList<CheckersMoveScore>();
			ArrayList<CheckersMoveScore> localDepthMoves3 = new ArrayList<CheckersMoveScore>();
			ArrayList<ArrayList<CheckersMoveScore>> oldMoves = new ArrayList<ArrayList<CheckersMoveScore>>();
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
			for(int j = 0; j < depthBoardFind;j++)
			{
				localDepthMoves3 = new ArrayList<CheckersMoveScore>();
				for(int i = 0; i < oldMoves.get(j).size();i++)
				{
					if(data.equals(oldMoves.get(j).get(i).board))
					{
						localDepthMoves1 = oldMoves.get(j).get(i).moves;
						if(localDepthMoves1.size() == 1)
						{
							data.canvas.doMakeMove(localDepthMoves1.get(0).move);
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
			branchMoves = new ArrayList<ArrayList<CheckersMoveScore>>();
			branchMoves = localBranchMoves;
		}
		int branchSize = branchMoves.size();
		int localDepth = depth - branchSize;
		for(int i = 0;i < localDepth;i++)
		{
			long startTime = System.currentTimeMillis();
			IncreaseDepth(branchMoves);			
			long endTime = System.currentTimeMillis();
			System.out.println("Add Moves: " + (endTime - startTime));
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
			//System.out.println("Branch size: " + branchMoves.get(branchMoves.size()-1).size());
			if(i == localDepth-1)
			{
				branchSizeAvg = (branchSizeAvg*branches+branchMoves.get(branchMoves.size()-1).size());
				branches++;
				branchSizeAvg/= branches;
				System.out.println(branchSizeAvg);
			}
		}
		int bestMoveIndex = 0;
		for(int i = 0;i < moveScores.size();i++)
		{
			if(moveScores.get(i).score > moveScores.get(bestMoveIndex).score)
			{
				bestMoveIndex = i;
			}
		}
		data.canvas.doMakeMove(moveScores.get(bestMoveIndex).move);
		prevMove = moveScores.get(bestMoveIndex);
	}
	
	private void IncreaseDepth(ArrayList<ArrayList<CheckersMoveScore>> branchMoves)
	{		
		//Calculate scores for each move in new depth
		//Thread[] depthIncThreads = new Thread[branchMoves.get(maxDepth).size()];
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

			}; 
		}

		//Update scores back through each CheckersMove
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
	}
	
void WriteCoeff()
	{
		//Serialization to file
		try
		{
			FileOutputStream file = new FileOutputStream("coeff.dat");
			
			ObjectOutputStream os = new ObjectOutputStream(file);
			
			os.writeObject(polynomialCoefficients);
			os.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();			
		}
	}
}
