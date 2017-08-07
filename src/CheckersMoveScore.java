import java.util.ArrayList;

public class CheckersMoveScore
{
	boolean depthBelowCalculated;
	float score;
	CheckersMove move;
	CheckersData board;
	ArrayList<CheckersMoveScore> moves;
	CheckersMoveScore(float score,CheckersMove move,CheckersData board)
	{
		depthBelowCalculated = false;
		this.score = score;
		this.move = new CheckersMove(move);
		board = new CheckersData(board);
	}
	void IncreaseDepth(float score,ArrayList<CheckersMoveScore> moves)
	{
		depthBelowCalculated = true;
		this.score = score;
		this.moves = new ArrayList<CheckersMoveScore>(moves);		
	}
}

