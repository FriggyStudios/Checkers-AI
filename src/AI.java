import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class AI
{
	//Coefficients range from 0 to 1
	//Entries multiplied by below values in that order to calculate favourability of a board to a player
	//RedPieces,-BlackPieces,RedKingPieces,-BlackKingPieces,RedHoppingOpportunities,-BlackHoppingOpportunities,RedCenters,-BlackCenters
	ArrayList<Float> polynomialCoefficients;
	CheckersData data;
	int depth = 7;
	
	@SuppressWarnings("unchecked")
	public AI(CheckersData data)
	{
		this.data = data;
		try
		{
			FileInputStream fileStream = new FileInputStream("coeff.dat");
		
			ObjectInputStream os = new ObjectInputStream(fileStream);
			
			polynomialCoefficients = (ArrayList<Float>)os.readObject();

			os.close();
		
		}
		//Catch Input errors
		catch(Exception e)
		{
			e.printStackTrace();
			polynomialCoefficients = new ArrayList<Float>();
			polynomialCoefficients.add(.1f);
			polynomialCoefficients.add(.1f);
			polynomialCoefficients.add(.2f);
			polynomialCoefficients.add(.2f);
			polynomialCoefficients.add(.0f);
			polynomialCoefficients.add(.0f);
			polynomialCoefficients.add(.0f);
			polynomialCoefficients.add(.0f);
			WriteCoeff();
		}
	}
	//Favourability board position for player red
	//Returns float from low(bad for red) to high(good for red)
	private float ScoreBoard(CheckersData dataLocal)
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
				if(dataLocal.board[i][j] == CheckersData.RED)
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
				else if(dataLocal.board[i][j] == CheckersData.BLACK)
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
				else if(dataLocal.board[i][j] == CheckersData.RED_KING)
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
				else if(dataLocal.board[i][j] == CheckersData.BLACK_KING)
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
			moveScores.add(new CheckersMoveScore(null,scores[i],moves[i],localData,playerMove,playerMoveNext));
		}
		for(int i = 0;i < depth;i++)
		{
			IncreaseDepth(moveScores,i);
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
	}
	
	private void IncreaseDepth(ArrayList<CheckersMoveScore> moveScores,int maxDepth)
	{		
		//Add each new move to be added
		//Store each move by reference
		//Entry is move at first index depth
		ArrayList<ArrayList<CheckersMoveScore>> branchMoves = new ArrayList<ArrayList<CheckersMoveScore>>();
		branchMoves.add(moveScores);
		for(int i = 0;i < maxDepth;i++)
		{
			ArrayList<CheckersMoveScore> localDepthMoves = new ArrayList<CheckersMoveScore>();
			for(int j = 0;j < branchMoves.get(i).size();j++)
			{
				CheckersMoveScore localMove = branchMoves.get(i).get(j);
				if(!localMove.goalState)
				{
					for(int k = 0;k < localMove.moves.size();k++)
					{
						localDepthMoves.add(localMove.moves.get(k));
					}
				}
			}
			branchMoves.add(localDepthMoves);
		}
		
		//Calculate scores for each move in new depth
		for(int i = 0;i < branchMoves.get(maxDepth).size();i++)
		{
			CheckersData localData = new CheckersData(branchMoves.get(maxDepth).get(i).board);
			CheckersData localDataTemp = new CheckersData(branchMoves.get(maxDepth).get(i).board);
			ArrayList<CheckersMoveScore> localMoveScores = new ArrayList<CheckersMoveScore>();
			CheckersMove[] moves = localData.getLegalMoves(localData.currentPlayer);
			if(!branchMoves.get(maxDepth).get(i).goalState)
			{
				for(int j = 0;j < moves.length;j++)
				{
					localData = new CheckersData(localDataTemp);
					byte playerMove = localData.currentPlayer;
					byte playerMoveNext = localData.currentPlayer;
					localData.makeMove(moves[j]);
					float score = ScoreBoard(localData);
					if(score > 0)
						System.out.println("Scoreboard: " + score);
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
					localMoveScores.add(new CheckersMoveScore(branchMoves.get(maxDepth).get(i),score,moves[j],localData,playerMove,playerMoveNext));
				}	
				branchMoves.get(maxDepth).get(i).IncreaseDepth(localMoveScores);
			}
		}
		
		//Update scores back through each CheckersMove
		for(int i = maxDepth;i >= 0;i--)
		{
			for(int k = 0;k < branchMoves.get(i).size();k++)
			{
				CheckersMoveScore localMove = branchMoves.get(i).get(k);
				int scoreIndex = 0;
				if(!localMove.goalState)
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
