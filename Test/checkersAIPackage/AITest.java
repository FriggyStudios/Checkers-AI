package checkersAIPackage;

import static org.junit.Assert.*;

import org.junit.*;

public class AITest 
{
	AI ai;
	
	@Before
 	public void setUp()
	{
		ai = new AI(new CheckersData(new CheckersCanvas()),CheckersData.RED,CheckersData.BLACK);
		assertNotNull(ai);
	}
 	@After
 	public void setDown()
 	{
 		ai = null;
 	}
	@Test
	public void increaseDepthToDepthMovesTest()
	{
		//lower depth, so can add another depth
		ai.depth = 4;
		ai.makeAIMove();
		assertNull("AI search branches should not be null after making more",
				ai.branchMoves.get(ai.branchMoves.size()-1).get(0).moves);
		int size = ai.branchMoves.size();
		ai.depth = 5;
		ai.increaseDepthToDepthMoves();
		assertEquals("AI search branches should increase after increaseDepth",
				size + 1, ai.branchMoves.size());
	}
	@Test
	public void calculateFirstMovesTest()
	{
		ai.calculateFirstMoves();
		assertEquals("branchMoves should be length one after calculationFirstMoves called",
				1,ai.branchMoves.size());
	}
	@Test
	public void updateBranchMovesFromPreviousBranchTest()
	{
		ai.makeAIMove();
		int size = ai.branchMoves.size();
		ai.updateBranchMovesFromPreviousBranch();
		assertEquals("updateBranchMovesFromPreviousBranch should find board in previously searched branchMoves, one move made so one depth less",
				1, ai.branchMoves.size());
	}
	@Ignore
	public void pruneBranchMovesTest()
	{
		
	}
	@Test
	public void bestMoveTest()
	{
		ai.calculateFirstMoves();
		int bestMoveIndex = ai.bestMove();
		assertTrue("bestMoveIndex must be within moveScores bounds",
				bestMoveIndex < ai.moveScores.size());
		
		
	}
}