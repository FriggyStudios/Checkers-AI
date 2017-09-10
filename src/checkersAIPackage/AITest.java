package checkersAIPackage;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

import org.junit.*;

public class AITest 
{
	AI ai;
	
	@Before
 	public void setUp()
	{
		ai = new AI(new CheckersData(new CheckersCanvas()));
		assertNotNull(ai);
	}
 	@After
 	public void setDown()
 	{
 		ai = null;
 	}
	@Test
	public void increaseDepthTest()
	{
		//lower depth, so can add another depth
		ai.depth = 7;
		ai.makeAIMove();
		assertNull(ai.branchMoves.get(ai.branchMoves.size()-1).get(0).moves);
		ai.increaseDepth();
		assertNotNull(ai.branchMoves.get(ai.branchMoves.size()-1).get(0).moves);
	}
}
