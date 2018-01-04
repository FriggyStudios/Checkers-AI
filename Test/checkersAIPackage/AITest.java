package checkersAIPackage;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.*;

public class AITest 
{
	AI ai;
	
	@Before
 	public void setUp()
	{
		CheckersData data = new CheckersData(new CheckersCanvas());
		ai = new AI(data,CheckersData.RED,CheckersData.BLACK);
	}
 	@After
 	public void setDown()
 	{
 		ai = null;
 	}
 	@Test
 	public void aiSetupTest()
 	{
		assertNotNull("AI constructed should not be null",ai);
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
		AI ai2 = new AI(ai.data,CheckersData.BLACK,CheckersData.RED);
		ai2.makeAIMove();
		ai.moveScores = new ArrayList<CheckersMoveScore>();
		int size = ai.branchMoves.size();
		ai.updateBranchMovesFromPreviousBranch();
		assertEquals("updateBranchMovesFromPreviousBranch two moves made so two depth less",
				size-2, ai.branchMoves.size());
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