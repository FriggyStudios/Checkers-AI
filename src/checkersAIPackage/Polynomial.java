package checkersAIPackage;

import java.util.ArrayList;

public class Polynomial implements java.io.Serializable
{
	private static final long serialVersionUID = 727970146609602996L;
	protected ArrayList<Float> coefficients;
	private final int coefficientsLength = 6;
	int RedPieces=0; int BlackPieces = 0;
	int RedKingPieces = 0; int BlackKingPieces = 0;
	int RedAdjacent = 0; int BlackAdjacent = 0;
	int RedCentre = 0; int BlackCentre = 0;
	int RedToKing = 0; int BlackToKing = 0;
	int RedBackTile = 0; int BlackBackTile = 0;
	
	public Polynomial(ArrayList<Float> newCoefficients)
	{
		if(newCoefficients.size() != coefficientsLength)
		{
			throw new java.lang.RuntimeException("Coefficients passed to Polynomial not length: " + coefficientsLength);
		}
		coefficients = new ArrayList<Float>(newCoefficients);
	}
	public Polynomial(Polynomial poly)
	{
		coefficients = new ArrayList<Float>(poly.coefficients);
	}

	
	public void Count(CheckersData dataLocal,int i, int j)
	{
		if(dataLocal.board.Get(i, j) == CheckersData.RED)
		{
			RedPieces++;
			RedToKing += 8-i;
			if(i == 7)
			{
				RedBackTile++;
			}
		}
		else if(dataLocal.board.Get(i, j) == CheckersData.BLACK)
		{
			BlackPieces++;
			BlackToKing += i;
			if(i == 0)
			{
				BlackBackTile++;
			}					
		}
		else if(dataLocal.board.Get(i, j) == CheckersData.RED_KING)
		{
			RedKingPieces++;
		}
		else if(dataLocal.board.Get(i, j) == CheckersData.BLACK_KING)
		{
			BlackKingPieces++;		
		}
		if(dataLocal.board.Get(i, j) == CheckersData.RED || 
				dataLocal.board.Get(i, j) == CheckersData.RED_KING)
		{
			if(dataLocal.board.Get(i+1, j+1) == CheckersData.RED || 
					dataLocal.board.Get(i+1, j+1) == CheckersData.RED_KING ||
					dataLocal.board.Get(i-1, j+1) == CheckersData.RED || 
					dataLocal.board.Get(i-1, j+1) == CheckersData.RED_KING ||
					dataLocal.board.Get(i+1, j-1) == CheckersData.RED || 
					dataLocal.board.Get(i+1, j-1) == CheckersData.RED_KING || 
					dataLocal.board.Get(i-1, j-1) == CheckersData.RED || 
					dataLocal.board.Get(i-1, j-1) == CheckersData.RED_KING)
			{
				RedAdjacent++;
			}
		}
		if(dataLocal.board.Get(i, j) == CheckersData.BLACK || 
				dataLocal.board.Get(i, j) == CheckersData.BLACK_KING)
		{
			if(dataLocal.board.Get(i+1, j+1) == CheckersData.BLACK || 
					dataLocal.board.Get(i+1, j+1) == CheckersData.BLACK_KING ||
					dataLocal.board.Get(i-1, j+1) == CheckersData.BLACK || 
					dataLocal.board.Get(i-1, j+1) == CheckersData.BLACK_KING ||
					dataLocal.board.Get(i+1, j-1) == CheckersData.BLACK || 
					dataLocal.board.Get(i+1, j-1) == CheckersData.BLACK_KING || 
					dataLocal.board.Get(i-1, j-1) == CheckersData.BLACK || 
					dataLocal.board.Get(i-1, j-1) == CheckersData.BLACK_KING)
			{
				BlackAdjacent++;
			}
		}
	}
	//Favourability board position for player red
	//Returns float from low(bad for red) to high(good for red)
	//{RedPieces,-BlackPieces,RedKingPieces,-BlackKingPieces,RedAdjacent,-BlackAdjacent,
	//RedCentre,-BlackCentre,RedToKing,-BlackToKing,RedBackTile,-BlackBackTile}
	public float scoreBoard(CheckersData dataLocal)
	{
		 RedPieces=0;  BlackPieces = 0;
		 RedKingPieces = 0;  BlackKingPieces = 0;
		 RedAdjacent = 0;  BlackAdjacent = 0;
		 RedCentre = 0;  BlackCentre = 0;
		 RedToKing = 0;  BlackToKing = 0;
		 RedBackTile = 0;  BlackBackTile = 0;
		
		//Calculate Red and Black centre 3,3 3,5 4,2 4,4
		if(dataLocal.board.Get(3, 3) == CheckersData.RED ||
			dataLocal.board.Get(3, 3) == CheckersData.RED_KING)
		{
			RedCentre++;
		}
		else if(dataLocal.board.Get(3, 3) == CheckersData.BLACK ||
				dataLocal.board.Get(3, 3) == CheckersData.BLACK_KING)
		{
			BlackCentre++;
		}
		if(dataLocal.board.Get(3, 5) == CheckersData.RED ||
				dataLocal.board.Get(3, 5) == CheckersData.RED_KING)
		{
			RedCentre++;
		}
		else if(dataLocal.board.Get(3, 5) == CheckersData.BLACK ||
				dataLocal.board.Get(3, 5) == CheckersData.BLACK_KING)
		{
			BlackCentre++;
		}
		if(dataLocal.board.Get(4, 2) == CheckersData.RED ||
				dataLocal.board.Get(4, 2) == CheckersData.RED_KING)
		{
			RedCentre++;
		}
		else if(dataLocal.board.Get(4, 2) == CheckersData.BLACK ||
				dataLocal.board.Get(4, 2) == CheckersData.BLACK_KING)
		{
			BlackCentre++;
		}
		if(dataLocal.board.Get(4, 4) == CheckersData.RED ||
				dataLocal.board.Get(4, 4) == CheckersData.RED_KING)
		{
			RedCentre++;
		}
		else if(dataLocal.board.Get(4, 4) == CheckersData.BLACK ||
				dataLocal.board.Get(4, 4) == CheckersData.BLACK_KING)
		{
			BlackCentre++;
		}
		
		//Calculate Red and Black piece, king, toKing, adjacent and backTile
		for(int i = 0;i < 8;i+=2)
		{
			for(int j = 1;j < 8;j+=2)
			{
				Count(dataLocal,i,j);
			}
				
		}
		for(int i = 1;i < 8;i+=2)
		{
			for(int j = 0;j < 8;j+=2)
			{
				Count(dataLocal,i,j);
			}
		}
		
		//Return score of favourability of board relative to red player
		float score = RedPieces*coefficients.get(0) - BlackPieces*coefficients.get(0)
				 	+ RedKingPieces*coefficients.get(1) - BlackKingPieces*coefficients.get(1)
					+ RedAdjacent*coefficients.get(2) - BlackAdjacent*coefficients.get(2)
					+ RedCentre*coefficients.get(3) - BlackCentre*coefficients.get(3)
					+ RedToKing*coefficients.get(4) - BlackToKing*coefficients.get(4);
		return score;
	}
	
	public String toString()
	{
		String s = "Pieces,KingPieces,Adjacent,Centre,ToKing,BackTile\n";
		s += coefficients.toString();
		return s;
	}

	public boolean equals(Polynomial other)
	{
		return coefficients.equals(other.coefficients);
	}
}
