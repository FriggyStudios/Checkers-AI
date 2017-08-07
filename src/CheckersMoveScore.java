import java.util.ArrayList;

public class CheckersMoveScore
{
	int playerMove;
	int playerNextMove;
	boolean depthBelowCalculated;
	float score;
	CheckersMove move;
	CheckersData board;
	ArrayList<CheckersMoveScore> moves;
	CheckersMoveScore(float score,CheckersMove move,CheckersData board,int playerMove,int playerNextMove)
	{
		this.playerMove = playerMove;
		this.playerNextMove = playerNextMove;
		depthBelowCalculated = false;
		this.score = score;
		this.move = new CheckersMove(move);
		this.board = new CheckersData(board);
	}
	void IncreaseDepth(float score,ArrayList<CheckersMoveScore> moves)
	{
		depthBelowCalculated = true;
		this.score = score;
		this.moves = new ArrayList<CheckersMoveScore>(moves);		
	}
}

