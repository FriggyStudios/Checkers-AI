package checkersAIPackage;
import java.io.*;
import java.util.ArrayList;

public class Evolve 
{
	AI aiRed;
	AI aiBlack;
	AI parentAI;
	Polynomial parentPoly;
	Polynomial mutatedPoly;
	private float step = 0.07f;
	private int generations = 0;
	private int successes = 0;
	private int failures = 0;
	protected int gamesWon;
	private String polyFileName;
	
	Evolve(AI newAIRed,AI newAIBlack,String newPolyFileName)
	{
		aiRed = newAIRed;
		aiBlack = newAIBlack;
		polyFileName = newPolyFileName;
        parentPoly = new Polynomial(newAIRed.poly);
        mutatedPoly = new Polynomial(newAIBlack.poly);
        gamesWon = 0;
        System.out.println(parentPoly.toString());
	}
	
	protected void init()
	{
		if(gamesWon == 0)
		{
			System.out.println("Generation:" + generations);
			mutatedPoly = mutatePoly(parentPoly);
			aiBlack.updatePolynomial(parentPoly);
			aiRed.updatePolynomial(mutatedPoly);
			System.out.println(mutatedPoly.toString());
			parentAI = aiRed;
		}
		else //if(gamesWon == 1)
		{
			aiBlack.updatePolynomial(parentPoly);
			aiRed.updatePolynomial(mutatedPoly);
			parentAI = aiBlack;
		}
		//System.out.println(parentPoly.toString());
		aiBlack.branchMoves = null;
		aiRed.branchMoves = null;
	}
	
	protected Polynomial mutatePoly(Polynomial poly)
	{
		float lengthInverse = 1.f /poly.coefficients.size();
		Polynomial newPoly = new Polynomial(poly);
		boolean change = false;
		for(int i = 0;i < newPoly.coefficients.size();i++)
		{
			if((Math.random() < 2f*lengthInverse))
			{
				change = true;
				float x = (float)(Math.random() * 2 - 1);
				float mutation = (x * step);
				float coeff = newPoly.coefficients.get(i) + mutation;
				coeff = Math.min(coeff, 1);
				coeff = Math.max(coeff, 0);
				newPoly.coefficients.remove(i);
				newPoly.coefficients.add(i,coeff);
			}
		}	
		if(!change)
			return mutatePoly(poly);
		else
			return newPoly;
	}
	
	protected void endGame(AI aiWinner)
	{
		//Evolutionary computation 1/5 rule for changing mutation rate
		if(successes+failures >= 10)
		{
			if(successes > 2)
			{
				step/=1.2f;
			}
			else if(successes < 2)
			{
				step*=1.2f;
			}
			successes = 0;
			failures = 0;
		}
		
		if(gamesWon == 0)
		{
			if(aiWinner == parentAI)
			{
				generations++;
				gamesWon = 0;
				failures++;
			}
			else 
			{
				gamesWon = 1;
			}
		}
		else //if(gamesWon == 1)
		{
			generations++;
			gamesWon = 0;
			if(aiWinner != parentAI)
			{
				successes++;
				parentPoly = new Polynomial(aiWinner.poly);	
				//Print new polynomial
				System.out.println("New evolved heuristic");
				System.out.println(aiWinner.poly.toString());
				//Write to serialized file
				try 
				{
			         FileOutputStream fileOut =
			         new FileOutputStream(polyFileName);
			         ObjectOutputStream out = new ObjectOutputStream(fileOut);
			         out.writeObject(parentPoly);
			         out.close();
			         fileOut.close();
			     } 
				 catch (IOException i) 
				 {
			         i.printStackTrace();
				 }
			}
			else
			{
				failures++;
			}
		}
	}

	protected void endGameDraw(CheckersData dataLocal)
	{
		int RedPieces=0; int BlackPieces = 0;
		int RedKingPieces = 0; int BlackKingPieces = 0;
		
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
		if(gamesWon == 0)
		{
			if(BlackPieces+BlackKingPieces*2 > RedPieces+RedKingPieces*2)
			{
				gamesWon = 1;
			}
			else
			{
				gamesWon = 0;
				failures++;
				System.out.println("Draw");
				generations++;
			}
		}
		else
		{
			if(BlackPieces+BlackKingPieces*2 < RedPieces+RedKingPieces*2)
			{
				successes++;
				parentPoly = new Polynomial(aiRed.poly);	
				//Print new polynomial
				System.out.println("New evolved heuristic");
				System.out.println(aiRed.poly.toString());
				generations++;
				gamesWon = 0;
				//Write to serialized file
				try 
				{
			         FileOutputStream fileOut =
			         new FileOutputStream(polyFileName);
			         ObjectOutputStream out = new ObjectOutputStream(fileOut);
			         out.writeObject(parentPoly);
			         out.close();
			         fileOut.close();
			     } 
				 catch (IOException i) 
				 {
			         i.printStackTrace();
				 }
			}
			else
			{
				gamesWon = 0;
				failures++;
				System.out.println("Draw");
				generations++;
			}
		}		
	}

}
