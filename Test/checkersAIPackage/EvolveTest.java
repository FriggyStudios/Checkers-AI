package checkersAIPackage;

import static org.junit.Assert.*;

import org.junit.*;

public class EvolveTest 
{
	Evolve evolve;
	AI aiRed;
	AI aiBlack;
	
	@Before
	public void setUp()
	{
		aiRed = new AI(null,CheckersData.RED,CheckersData.BLACK);
		aiBlack = new AI(null,CheckersData.BLACK,CheckersData.RED);
		evolve = new Evolve(aiRed ,aiBlack,"EvolveTestFile.txt");
	}
	
	@After public void setDown()
	{
		evolve = null;
		aiRed = null;
		aiBlack = null;
	}
	
	@Test 
	public void initTest()
	{
		evolve.init();
		assertNotNull("Parent of evolve must be initialised in init",evolve.parentAI);
		assertTrue("Parent of evolve must be red or black AI",
				evolve.parentAI == evolve.aiBlack || evolve.parentAI == evolve.aiRed);
	}	
	@Test
	public void mutatePolyTest()
	{
		Polynomial poly = evolve.mutatePoly(evolve.aiBlack.poly);
		assertNotNull(poly);
		assertNotNull(poly.coefficients);
	}
	@Test
	public void endGameParentWinsTest()
	{
		evolve.parentAI = evolve.aiBlack;
		evolve.endGame(evolve.parentAI);
		assertEquals("Parent polynomial that wins game should stay same",
				aiBlack.poly.coefficients,evolve.parentPoly.coefficients);
	}
	@Test
	public void endGameParentLosesTest()
	{
		evolve.parentAI = evolve.aiBlack;
		evolve.gamesWon = 1;
		evolve.endGame(aiRed);
		assertEquals("Parent polynomial should update when parent loses",
				aiRed.poly.coefficients,evolve.parentPoly.coefficients);
	}
	
}
