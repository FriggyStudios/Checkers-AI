import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.*;

public class CheckersMoveScoreTest
{
	private CheckersMoveScore move;
	private CheckersMoveScore move2;
	ArrayList<CheckersMoveScore> moves = new ArrayList<CheckersMoveScore>();
	private float score = 2;
	byte playerMove = CheckersData.RED;
	byte playerNextMove = CheckersData.BLACK;

	@Before
 	public void setUp()
	{
		move = new CheckersMoveScore(null,score,new CheckersMove(0,0,1,1),new CheckersData((CheckersCanvas)null),playerMove,playerNextMove);
		move2 = new CheckersMoveScore(null,score-1,new CheckersMove(0,0,1,1),new CheckersData((CheckersCanvas)null),playerMove,playerNextMove);
		
		moves.add(move);
		moves.add(move);
 	}
 	@After
 	public void setDown()
 	{
 		move = null;
 		move2 = null;
 	}
 	//Test Constructor
	@Test(timeout=1)
	public void compareToTest()
	{
		assertNotNull(move.compareTo(move2));
	}
	
	@Test(timeout=1)
	public void sortTest()
	{
		Collections.sort(moves);
		assertNotNull(moves);
	}
}
