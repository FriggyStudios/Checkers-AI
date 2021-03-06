package checkersAIPackage;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.*;

public class EvolveTest 
{
	Evolve evolve;
	AI aiRed;
	AI aiBlack;
	
	@Before
	public void setUp()
	{
		ArrayList<Float> polyRed = new ArrayList<Float>();
		ArrayList<Float> polyBlack = new ArrayList<Float>();
		polyRed.add(0f);polyRed.add(0f);polyRed.add(0f);
		polyRed.add(0f);polyRed.add(0f);polyRed.add(0f);
		polyBlack.add(1f);polyBlack.add(0f);polyBlack.add(0f);
		polyBlack.add(1f);polyBlack.add(0f);polyBlack.add(0f);
		aiRed = new AI(null,CheckersData.RED,CheckersData.BLACK);
		aiBlack = new AI(null,CheckersData.BLACK,CheckersData.RED);
		evolve = new Evolve(aiRed ,aiBlack,"EvolveTestFile.txt");
		evolve.aiRed.updatePolynomial(new Polynomial(polyRed));
		evolve.aiBlack.updatePolynomial(new Polynomial(polyBlack));
		evolve.parentPoly = evolve.aiRed.poly;
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
		evolve.endGame(evolve.parentAI);
		assertEquals("Parent polynomial that wins game should stay same",
				evolve.aiRed.poly.coefficients,evolve.parentPoly.coefficients);
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
	@Test
	public void endGameDrawParentWinsTest()
	{
        evolve.parentPoly = new Polynomial(evolve.aiBlack.poly);
		CheckersData data = new CheckersData((CheckersCanvas)(null));
		data.board.Add(CheckersData.BLACK_KING, 0, 1);
		evolve.endGameDraw(data);
		assertEquals("Parent polynomial that wins draw game should stay same",
				aiBlack.poly.coefficients,evolve.parentPoly.coefficients);
	}
	@Test
	public void endGameDrawParentLosesTest()
	{
        evolve.parentPoly = new Polynomial(evolve.aiBlack.poly);
		evolve.gamesWon = 1;
		CheckersData data = new CheckersData((CheckersCanvas)(null));
		data.board.Add(CheckersData.RED_KING, 1, 0);
		evolve.endGameDraw(data);
		assertEquals("Parent polynomial should update when parent loses final drawen game",
				aiRed.poly.coefficients,evolve.parentPoly.coefficients);
	}
	
}
