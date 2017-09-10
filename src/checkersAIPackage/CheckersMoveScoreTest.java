package checkersAIPackage;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.*;

public class CheckersMoveScoreTest
{
	private CheckersMoveScore move1;
	private CheckersMoveScore move2;
	private CheckersMoveScore move3;
	private CheckersMoveScore move4;
	ArrayList<CheckersMoveScore> moves = new ArrayList<CheckersMoveScore>();
	private final float score = 2;
	private final float lowerScore = 1;

	@Before
 	public void setUp()
	{
		move1 = new CheckersMoveScore(score,new CheckersMove(0,0,1,1),new CheckersData((CheckersCanvas)null),CheckersData.BLACK,CheckersData.RED);
		move2 = new CheckersMoveScore(lowerScore,new CheckersMove(0,0,1,1),new CheckersData((CheckersCanvas)null),CheckersData.BLACK,CheckersData.RED);
		move3 = new CheckersMoveScore(score,new CheckersMove(0,0,1,1),new CheckersData((CheckersCanvas)null),CheckersData.RED,CheckersData.BLACK);
		move4 = new CheckersMoveScore(lowerScore,new CheckersMove(0,0,1,1),new CheckersData((CheckersCanvas)null),CheckersData.RED,CheckersData.BLACK);		
 	}
 	@After
 	public void setDown()
 	{
 		move1 = null;
 		move2 = null;
 		move3 = null;
 		move4 = null;
 	}
	@Test(timeout=10)
	public void compareToTest()
	{
		assertNotNull(move1.compareTo(move2));
	}
	
	@Test(timeout=10)
	public void sortTest()
	{
		moves.add(move1);
		moves.add(move2);
		Collections.sort(moves);
		assertNotNull(moves);
		
		assertSame(CheckersData.BLACK,moves.get(0).playerMove);
		assertSame(CheckersData.BLACK,moves.get(1).playerMove);
		assertSame(move2,moves.get(0));
		assertSame(move1,moves.get(1));
		assertTrue(moves.get(0).score < moves.get(1).score);
		
		moves = new ArrayList<CheckersMoveScore>();
		moves.add(move3);
		moves.add(move4);
		Collections.sort(moves);
		assertNotNull(moves);
		
		assertSame(CheckersData.RED,moves.get(0).playerMove);
		assertSame(CheckersData.RED,moves.get(1).playerMove);
		assertSame(move4,moves.get(0));
		assertSame(move3,moves.get(1));
		assertTrue(moves.get(0).score < moves.get(1).score);
	}
}
