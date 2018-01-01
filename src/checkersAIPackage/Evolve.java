package checkersAIPackage;
import java.io.*;

public class Evolve 
{
	AI aiRed;
	AI aiBlack;
	AI parentAI;
	Polynomial parentPoly;
	float step = 0.001f;
	String polyFileName;
	
	Evolve(AI newAIRed,AI newAIBlack,String newPolyFileName)
	{
		aiRed = newAIRed;
		aiBlack = newAIBlack;
		polyFileName = newPolyFileName;
		//Get polynomial from serialized file
		try {
	         FileInputStream fileIn = new FileInputStream(polyFileName);
	         ObjectInputStream in = new ObjectInputStream(fileIn);
	         parentPoly = (Polynomial) in.readObject();
	         in.close();
	         fileIn.close();
	      } catch (IOException i) {
	         parentPoly = new Polynomial(newAIRed.poly);
	      } catch (ClassNotFoundException c) {
	         System.out.println("Employee class not found");
	         c.printStackTrace();
	         return;
	      }
	}
	
	protected void init()
	{
		int aiBlackOrRed = (int)(Math.random() * 2);
		Polynomial mutatedPoly = mutatePoly(parentPoly);
		if(aiBlackOrRed == 0)
		{
			aiBlack.updatePolynomial(parentPoly);
			aiRed.updatePolynomial(mutatedPoly);
			parentAI = aiBlack;
		}
		else
		{
			aiBlack.updatePolynomial(mutatedPoly);
			aiRed.updatePolynomial(parentPoly);	
			parentAI = aiRed;
		}
	}
	
	protected Polynomial mutatePoly(Polynomial poly)
	{
		int lengthInverse = 1 /poly.coefficients.size();
		Polynomial newPoly = new Polynomial(poly);
		for(Float coeff : newPoly.coefficients)
		{
			if((Math.random() < lengthInverse))
			{
				float x = (float)(Math.random() * 2 - 1);
				float mutation = (x * step);
				coeff += mutation;
				coeff = Math.max(coeff, 1);
				coeff = Math.min(coeff, 0);
			}
		}		
		//Should I normalise?
		return newPoly;
	}
	
	protected void endGame(AI aiWinner)
	{
		if(aiWinner != parentAI)
		{
			parentPoly = new Polynomial(aiWinner.poly);	
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
	}
}
