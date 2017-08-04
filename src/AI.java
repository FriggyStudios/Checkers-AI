import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AI
{
	//Coefficients range from 0 to 1
	//Entries multiplied by below values in that order to calculate favourability of a board to a player
	//RedPieces,-BlackPieces,RedKingPieces,-BlackKingPieces,RedHoppingOpportunities,-BlackHoppingOpportunities,RedCenters,-BlackCenters
	ArrayList<Float> polynomialCoefficients;
	CheckersData data;
	
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
			polynomialCoefficients.add(.8f);
			polynomialCoefficients.add(.8f);
			polynomialCoefficients.add(.25f);
			polynomialCoefficients.add(.25f);
			//WriteCoeff();
		}
	}
	
	public void ScoreBoard()
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
				if(data.board[i][j] == CheckersData.RED)
				{
					RedPieces++;
					if(( i == 2 || i == 4) && j == 4)
					{
						RedCenters++;
					}
					if(data.canJump(CheckersData.RED, i, j, i-1, j-1, i-2, j-2))
					{
						RedHoppingOpportunities++;
					}
					if(data.canJump(CheckersData.RED, i, j, i+1, j-1, i+2, j-2))
					{
						RedHoppingOpportunities++;
					}
				}
				else if(data.board[i][j] == CheckersData.BLACK)
				{
					BlackPieces++;
					if(( i == 3 || i == 5) && j == 3)
					{
						BlackCenters++;
					}
					if(data.canJump(CheckersData.BLACK, i, j, i-1, j+1, i-2, j+2))
					{
						BlackHoppingOpportunities++;
					}
					if(data.canJump(CheckersData.BLACK, i, j, i+1, j+1, i+2, j+2))
					{
						BlackHoppingOpportunities++;
					}
				}
				else if(data.board[i][j] == CheckersData.RED_KING)
				{
					RedKingPieces++;
					if(( i == 2 || i == 4) && j == 4)
					{
						RedCenters++;
					}
					if(data.canJump(CheckersData.RED, i, j, i-1, j-1, i-2, j-2))
					{
						RedHoppingOpportunities++;
					}
					if(data.canJump(CheckersData.RED, i, j, i+1, j-1, i+2, j-2))
					{
						RedHoppingOpportunities++;
					}
				}
				else if(data.board[i][j] == CheckersData.BLACK_KING)
				{
					BlackKingPieces++;
					if(( i == 3 || i == 5) && j == 3)
					{
						BlackCenters++;
					}
					if(data.canJump(CheckersData.BLACK, i, j, i-1, j+1, i-2, j+2))
					{
						BlackHoppingOpportunities++;
					}
					if(data.canJump(CheckersData.BLACK, i, j, i+1, j+1, i+2, j+2))
					{
						BlackHoppingOpportunities++;
					}
				}
			}
		}
		float score = RedPieces*polynomialCoefficients.get(0) - BlackPieces*polynomialCoefficients.get(1)
				 	+ RedKingPieces*polynomialCoefficients.get(2) - BlackKingPieces*polynomialCoefficients.get(3)
				 	+ RedHoppingOpportunities*polynomialCoefficients.get(4) - BlackHoppingOpportunities*polynomialCoefficients.get(5)
				 	+ RedCenters*polynomialCoefficients.get(6) - BlackCenters*polynomialCoefficients.get(7);
		System.out.println("Board score: " + score);
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