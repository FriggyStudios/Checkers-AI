package checkersAIPackage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;

public class AI
{
	static final String fileName = "heuristicPolynomial.txt";
	Polynomial poly;
	CheckersData data;
	byte player, otherPlayer;
	int scoreModifier = 1;
	int depth = 7;
	ArrayList<ArrayList<CheckersMoveScore>> branchMoves;
	ArrayList<CheckersMoveScore> moveScores = new ArrayList<CheckersMoveScore>();
	CheckersMoveScore prevMove;
	//Max time allowed in IncreaseDepth
	int timePerIncreaseDepth = 10000;
	
	public AI(CheckersData data,byte newPlayer,byte newOtherPlayer)
	{		
		this.data = data;
		player = newPlayer;
		if(player == CheckersData.RED)
		{
			scoreModifier = 1;
		}
		else
		{
			scoreModifier = -1;
		}
		otherPlayer = newOtherPlayer;//Get polynomial from serialized file
		try	 {
	         FileInputStream fileIn = new FileInputStream(fileName);
	         ObjectInputStream in = new ObjectInputStream(fileIn);
	         poly = (Polynomial) in.readObject();
	         in.close();
	         fileIn.close();
	      } catch (IOException i) {
	    	//{RedPieces,-BlackPieces,RedKingPieces,-BlackKingPieces,RedAdjacent,-BlackAdjacent,
	  		//RedCentre,-BlackCentre,RedToKing,-BlackToKing,RedBackTile,-BlackBackTile}
	  		ArrayList<Float>  polynomialCoefficients = new ArrayList<Float>();
	  		polynomialCoefficients.add(0.04f);
	  		polynomialCoefficients.add(0.3f);
	  		polynomialCoefficients.add(0.0882918f);
	  		polynomialCoefficients.add(0.4091838f);
	  		polynomialCoefficients.add(0.44403866f);
	  		polynomialCoefficients.add(0.23308124f);
	  		poly = new Polynomial(polynomialCoefficients);
	      } catch (ClassNotFoundException c) {
	         c.printStackTrace();
	         return;
	      }
	}	
	
	public AI(CheckersData data,byte newPlayer,byte newOtherPlayer,Polynomial newPoly)
	{
		this.data = data;
		player = newPlayer;
		if(player == CheckersData.RED)
		{
			scoreModifier = 1;
		}
		else
		{
			scoreModifier = -1;
		}
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
		moveScores = new ArrayList<CheckersMoveScore>();
		//branchMoves null on first MakeAIMove call or if failure in finding board in previous branchMoves
		//Calculate first list of moves for brancMoves
		if(branchMoves == null)
		{
			if(calculateFirstMoves())
			{
				return;
			}
		}
		//Find current board in branchMoves
		//Make new branchMoves starting from this point in old branchMoves
		else
		{
			//calculateFirstMoves();
			if(updateBranchMovesFromPreviousBranch())
			{
				return;
			}
		}
		//Try adding as many branches to depthMoves so depthMoves is size depth
		increaseDepthToDepthMoves();
		//System.out.println("Branch Depth: " + branchMoves.size());
		
		//Choose index of best scoring move
		int bestMoveIndex = bestMove();

		//Make best move
		data.canvas.doMakeMove(moveScores.get(bestMoveIndex).move);
		//Store move made to be referred to in next move
		prevMove = moveScores.get(bestMoveIndex);
	}
	
	protected boolean calculateFirstMoves()
	{
		CheckersMove[] moves = data.getLegalMoves(player);
		float[] scores = new float[moves.length];
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
			moveScores.add(new CheckersMoveScore(null,scores[i],moves[i],localData,playerMove,playerMoveNext));
		}
		branchMoves = new ArrayList<ArrayList<CheckersMoveScore>>();
		branchMoves.add(moveScores);
		if(moveScores.size() == 1)
		{
			//Make only move
			data.canvas.doMakeMove(moveScores.get(0).move);
			//Store move made to be referred to in next move
			prevMove = moveScores.get(0);
			return true;
		}
		return false;
	}
	
	protected boolean updateBranchMovesFromPreviousBranch()
	{
		ArrayList<ArrayList<CheckersMoveScore>> localBranchMoves = new ArrayList<ArrayList<CheckersMoveScore>>();
		ArrayList<ArrayList<CheckersMoveScore>> oldMoves = new ArrayList<ArrayList<CheckersMoveScore>>();
		ArrayList<CheckersMoveScore> localDepthMoves1 = new ArrayList<CheckersMoveScore>();
		ArrayList<CheckersMoveScore> localDepthMoves3 = new ArrayList<CheckersMoveScore>();
		if(prevMove.moves == null)
		{
			//System.out.println("prevMove moves is null");
			branchMoves = null;
			return calculateFirstMoves();
		}
		oldMoves.add(prevMove.moves);
		boolean boardFound = false;
		int depthBoardFind = 3;
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
						//System.out.println(player + " moves of prevMove null");
						return calculateFirstMoves();
					}
					if(localDepthMoves1.size() == 1)
					{
						//Make only move
						data.canvas.doMakeMove(localDepthMoves1.get(0).move);
						//Store move made to be referred to in next move
						prevMove = localDepthMoves1.get(0);
						return true;
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
			//System.out.println("Moves empty");
			return calculateFirstMoves();
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
		branchMoves = localBranchMoves;
		
		return false;
	}
	
	protected void increaseDepthToDepthMoves()
	{
		int branchSize = branchMoves.size();
		int localDepth = depth - branchSize;
		for(int i = 0;i < localDepth;i++)
		{
			//If time limit reached stop adding branches to branchMoves
			if(!increaseDepth())
			{
				return;
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
		updateScores();
	}
	
	/*private void updateScores()
	{
		//Update scores back through each branchMove
		for(int i = branchMoves.size()-1;i >= 0;i--)
		{
			for(int k = 0;k < branchMoves.get(i).size();k++)
			{
				CheckersMoveScore localMove = branchMoves.get(i).get(k);
				int scoreIndex = 0;
				if(localMove.moves != null)
				{
					for(int j = 0;j < localMove.moves.size();j++)
					{
						if(localMove.playerMove == player)
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
	}*/

	protected void updateScores()
	{
		//AlphaBeta pruning
		if(branchMoves.size() < 1)
			return;
		CheckersMoveScore origin = new CheckersMoveScore(null,0,null,null,player,otherPlayer);
		origin.IncreaseDepth(branchMoves.get(0));
		alphabeta(origin,-Float.MAX_VALUE,Float.MAX_VALUE);
	}
	 
	private float alphabeta(CheckersMoveScore node,float a,float b)
	 {
	       if(node.moves == null || node.moves.size() == 0)
	           return node.score;
	       if (node.playerMove == player)
	       {
	           float v = -Float.MAX_VALUE;
	           for (CheckersMoveScore child : node.moves)
	           {
	        	   v = Math.max(v, alphabeta(child, a, b));
	               a = Math.max(a, v);
	               if (b <= a)
	               {
	            	   break;
	               }
	           } 
	           node.score = v;
	           return v;
	       }
	       else
	       {
	           float v = Float.MAX_VALUE;
	           for(CheckersMoveScore child : node.moves)
	           {
	               v = Math.min(v, alphabeta(child, a, b));
	               b = Math.min(b, v);
	               if (b <= a)
	               {
	            	   break;
	               }
	           }
	           node.score = v;
	           return v;
	       }
	 }
	
	protected int bestMove()
	{
		int bestMoveIndex = 0;
		for(int i = 0;i < moveScores.size();i++)
		{
			if(moveScores.get(i).score > moveScores.get(bestMoveIndex).score)
			{
				bestMoveIndex = i;
			}
		}
		return bestMoveIndex;
	}
	
	//Calculate moves for last list of branchMoves' last list
	//Updates scores back through branchMoves
	//Return true if time limit timePerIncreaseDepth not reached, false otherwise
	protected boolean increaseDepth()
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
					float score = poly.scoreBoard(localData)*scoreModifier;
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
					localMoveScores.add(new CheckersMoveScore(branchMoves.get(branchMoves.size()-1).get(i),score,moves[j],localData,playerMove,playerMoveNext));
				}	
				branchMoves.get(branchMoves.size()-1).get(i).IncreaseDepth(localMoveScores);

			}
			long endTime = System.currentTimeMillis();
			if(endTime - startTime > timePerIncreaseDepth)
			{
				return false;
			}
		}
		return true;
	}
}
