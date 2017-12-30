package checkersAIPackage;

import java.util.ArrayList;

public class Polynomial 
{
	ArrayList<Float> coefficients;
	
	public Polynomial(ArrayList<Float> thisCoefficients)
	{
		coefficients = thisCoefficients;
	}

	//Favourability board position for player red
	//Returns float from low(bad for red) to high(good for red)
	public float scoreBoard(CheckersData dataLocal)
	{
		//{RedPieces,-BlackPieces,RedKingPieces,-BlackKingPieces,RedAdjacent,-BlackAdjacent,
		//RedCentre,-BlackCentre,RedToKing,-BlackToKing}
		int RedPieces=0; int BlackPieces = 0;
		int RedKingPieces = 0; int BlackKingPieces = 0;
		int RedAdjacent = 0; int BlackAdjacent = 0;
		int RedCentre = 0; int BlackCentre = 0;
		int RedToKing = 0; int BlackToKing = 0;
		
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
		
		//Calculate Red and Black piece, king, toKing and adjacent
		for(int i = 0;i < 8;i+=2)
		{
			for(int j = 1;j < 8;j+=2)
			{
				if(dataLocal.board.Get(i, j) == CheckersData.RED)
				{
					RedPieces++;
					RedToKing += 8-i;
				}
				else if(dataLocal.board.Get(i, j) == CheckersData.BLACK)
				{
					BlackPieces++;
					BlackToKing += i;
					
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
		}
		
		
		//Return score of favourability of board relative to red player
		float score = RedPieces*coefficients.get(0) - BlackPieces*coefficients.get(1)
				 	+ RedKingPieces*coefficients.get(2) - BlackKingPieces*coefficients.get(3)
					+ RedAdjacent*coefficients.get(4) - BlackAdjacent*coefficients.get(5)
					+ RedCentre*coefficients.get(6) - BlackCentre*coefficients.get(7)
					+ RedToKing*coefficients.get(8) - BlackToKing*coefficients.get(9);
		return score;
	}
}
