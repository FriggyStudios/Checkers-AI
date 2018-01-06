package checkersAIPackage;
import java.util.ArrayList;

public class CheckersMoveScore implements Comparable<CheckersMoveScore>
{
	byte playerMove;
	byte playerNextMove;
	float score;
	CheckersMoveScore parent;
	CheckersMove move;
	CheckersData board;
	ArrayList<CheckersMoveScore> moves;
	//finalState true if board after move played is an end game board
	boolean finalState = false;
	byte playerWin;
	CheckersMoveScore(CheckersMoveScore parent,float score,CheckersMove move,CheckersData board,byte playerMove,byte playerNextMove)
	{
		this.playerMove = playerMove;
		this.playerNextMove = playerNextMove;
		this.score = score;
		this.move = new CheckersMove(move);
		this.board = new CheckersData(board);
		if(board == null)
			return;
		//Count black and red pieces
		int RedPieces=0; int BlackPieces = 0;
		for(int i = 1;i < 8;i+=2)
		{
			for(int j = 0;j < 8;j+=2)
			{
				if(board.board.Get(i, j) == CheckersData.RED)
				{
					RedPieces++;
					if(BlackPieces != 0)
					{
						break;
					}
				}
				else if(board.board.Get(i, j) == CheckersData.BLACK)
				{
					BlackPieces++;
					if(RedPieces != 0)
					{
						break;
					}
				}
			}
			if(RedPieces != 0 && BlackPieces != 0)
			{
				break;
			}
		}
		for(int i = 0;i < 8;i+=2)
		{
			for(int j = 1;j < 8;j+=2)
			{
				if(board.board.Get(i, j) == CheckersData.RED)
				{
					RedPieces++;
					if(BlackPieces != 0)
					{
						break;
					}
				}
				else if(board.board.Get(i, j) == CheckersData.BLACK)
				{
					BlackPieces++;
					if(RedPieces != 0)
					{
						break;
					}
				}
			}
			if(RedPieces != 0 && BlackPieces != 0)
			{
				break;
			}
		}
		//Check if one player has no pieces
		if(RedPieces == 0 && BlackPieces == 0)
		{
			finalState = true;
		}
		//Check if player to move has no moves
		else
		{
			CheckersMove[] moves = board.getLegalMoves(playerNextMove);
			if(moves == null)
			{
				finalState = true;
			}
		}
	}
	void IncreaseDepth(ArrayList<CheckersMoveScore> moves)
	{
		this.moves = new ArrayList<CheckersMoveScore>(moves);		
	}
	void UpdateScore(float score)
	{
		this.score = score;
	}
	public int compareTo(CheckersMoveScore other)
	{
		return (int)((score-other.score)*1000000);
	}
}

