import java.util.ArrayList;

public class CheckersMoveScore
{
	int playerMove;
	int playerNextMove;
	boolean depthBelowCalculated;
	float score;
	CheckersMoveScore moveAbove;
	CheckersMove move;
	CheckersData board;
	ArrayList<CheckersMoveScore> moves;
	CheckersMoveScore(CheckersMoveScore moveAbove,float score,CheckersMove move,CheckersData board,int playerMove,int playerNextMove)
	{
		this.moveAbove = moveAbove;
		this.playerMove = playerMove;
		this.playerNextMove = playerNextMove;
		depthBelowCalculated = false;
		this.score = score;
		this.move = new CheckersMove(move);
		this.board = new CheckersData(board);
	}
	void IncreaseDepth(ArrayList<CheckersMoveScore> moves)
	{
		depthBelowCalculated = true;
		this.moves = new ArrayList<CheckersMoveScore>(moves);		
	}
	void UpdateScore(float score)
	{
		this.score = score;
	}
}

