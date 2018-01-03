package checkersAIPackage;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.*;

public class PolynomialTest 
{
	Polynomial poly;
	
	@Before
	public void setUp()
	{
		ArrayList<Float>  polynomialCoefficients = new ArrayList<Float>();
		polynomialCoefficients.add(.1f);
		polynomialCoefficients.add(.13f);
		polynomialCoefficients.add(.01f);
		polynomialCoefficients.add(.03f);
		polynomialCoefficients.add(.004f);
		polynomialCoefficients.add(.008f);
		poly = new Polynomial(polynomialCoefficients);
	}
	@After
	public void SetDown()
	{
		poly = null;	
	}
	
	@Test(expected=java.lang.RuntimeException.class)
	public void PolynomialWrongCoefficientSize()
	{
		ArrayList<Float> coefficients = new ArrayList<Float>();
		coefficients.add(5.5f);
		coefficients.add(5.5f);
		coefficients.add(5.5f);
		Polynomial p = new Polynomial(coefficients);
	}
	@Test
	public void scoreBoardPiecesMore()
	{
		Board board = new Board();
		board.entries.add(6, CheckersData.RED);
		CheckersData dataWithRed = new CheckersData(board);
		board = new Board();
		CheckersData dataWithoutRed = new CheckersData(board);
		assertTrue("ScoreBoard should give higher score for more red pieces",
				poly.scoreBoard(dataWithRed) > poly.scoreBoard(dataWithoutRed));		

		board = new Board();
		board.entries.add(32-6, CheckersData.BLACK);
		CheckersData dataWithBlack = new CheckersData(board);
		board = new Board();
		CheckersData dataWithoutBlack = new CheckersData(board);
		assertTrue("ScoreBoard should give higher score for more black pieces",
				poly.scoreBoard(dataWithoutBlack) > poly.scoreBoard(dataWithBlack));
		
	}
	@Test
	public void scoreBoardKingsMore()
	{
		Board board = new Board();
		board.entries.add(6, CheckersData.RED_KING);
		CheckersData dataWithRed = new CheckersData(board);
		board = new Board();
		board.entries.add(32-6, CheckersData.BLACK);
		CheckersData dataWithoutRed = new CheckersData(board);
		assertTrue("ScoreBoard should give higher score for more red kings",
				poly.scoreBoard(dataWithRed) > poly.scoreBoard(dataWithoutRed));

		board = new Board();
		board.entries.add(6, CheckersData.BLACK_KING);
		CheckersData dataWithBlack = new CheckersData(board);
		board = new Board();
		CheckersData dataWithoutBlack = new CheckersData(board);
		board.entries.add(32-6, CheckersData.RED);
		assertTrue("ScoreBoard should give higher score for more black kings",
				poly.scoreBoard(dataWithoutBlack) > poly.scoreBoard(dataWithBlack));
		
	}
	@Test
	public void scoreBoardAdjacentMore()
	{
		Board board = new Board();
		board.entries.add(4, CheckersData.RED);
		board.entries.add(8, CheckersData.RED);
		CheckersData dataWithRed = new CheckersData(board);
		board = new Board();
		board.entries.add(32-4, CheckersData.BLACK);
		board.entries.add(32-9, CheckersData.BLACK);
		CheckersData dataWithoutRed = new CheckersData(board);
		assertTrue("ScoreBoard should give higher score for more red adjacents",
				poly.scoreBoard(dataWithRed) > poly.scoreBoard(dataWithoutRed));

		board = new Board();
		board.entries.add(4, CheckersData.BLACK);
		board.entries.add(8, CheckersData.BLACK);
		CheckersData dataWithBlack = new CheckersData(board);
		board = new Board();
		CheckersData dataWithoutBlack = new CheckersData(board);
		board.entries.add(32-4, CheckersData.RED);
		board.entries.add(32-9, CheckersData.RED);
		assertTrue("ScoreBoard should give higher score for more black adjacents",
				poly.scoreBoard(dataWithoutBlack) > poly.scoreBoard(dataWithBlack));
	}
	@Test
	public void scoreBoardCentreMore()
	{
		Board board = new Board();
		board.entries.add(13, CheckersData.RED);
		CheckersData dataWithRed = new CheckersData(board);
		board = new Board();
		board.entries.add(4, CheckersData.BLACK);
		CheckersData dataWithoutRed = new CheckersData(board);
		assertTrue("ScoreBoard should give higher score for more red centres",
				poly.scoreBoard(dataWithRed) > poly.scoreBoard(dataWithoutRed));

		board = new Board();
		board.entries.add(13, CheckersData.BLACK);
		CheckersData dataWithBlack = new CheckersData(board);
		board = new Board();
		CheckersData dataWithoutBlack = new CheckersData(board);
		board.entries.add(4, CheckersData.RED);
		assertTrue("ScoreBoard should give higher score for more black centres",
				poly.scoreBoard(dataWithoutBlack) > poly.scoreBoard(dataWithBlack));
	}
	@Test
	public void scoreBoardToKingMore()
	{
		Board board = new Board();
		board.entries.add(6, CheckersData.RED);
		board.entries.add(8, CheckersData.RED_KING);
		CheckersData dataWithRed = new CheckersData(board);
		board = new Board();
		board.entries.add(6, CheckersData.BLACK);
		board.entries.add(32-8, CheckersData.BLACK_KING);
		CheckersData dataWithoutRed = new CheckersData(board);
		assertTrue("ScoreBoard should give higher score for less red to king distance",
				poly.scoreBoard(dataWithRed) > poly.scoreBoard(dataWithoutRed));

		board = new Board();
		board.entries.add(32-6, CheckersData.BLACK);
		board.entries.add(8, CheckersData.BLACK);
		CheckersData dataWithBlack = new CheckersData(board);
		board = new Board();
		CheckersData dataWithoutBlack = new CheckersData(board);
		board.entries.add(32-6, CheckersData.RED);
		board.entries.add(32-8, CheckersData.RED);
		assertTrue("ScoreBoard should give higher score for less black to king distance",
				poly.scoreBoard(dataWithoutBlack) > poly.scoreBoard(dataWithBlack));
	}
	@Test
	public void scoreBoardBackTileMore()
	{
		Board board = new Board();
		board.entries.add(32-3, CheckersData.RED);
		board.entries.add(8, CheckersData.RED);
		CheckersData dataWithRed = new CheckersData(board);
		board = new Board();
		board.entries.add(32-3, CheckersData.BLACK);
		board.entries.add(32-8, CheckersData.BLACK);
		CheckersData dataWithoutRed = new CheckersData(board);
		assertTrue("ScoreBoard should give higher score for more red adjacents",
				poly.scoreBoard(dataWithRed) > poly.scoreBoard(dataWithoutRed));

		board = new Board();
		board.entries.add(3, CheckersData.BLACK);
		board.entries.add(32-8, CheckersData.BLACK);
		CheckersData dataWithBlack = new CheckersData(board);
		board = new Board();
		CheckersData dataWithoutBlack = new CheckersData(board);
		board.entries.add(3, CheckersData.RED);
		board.entries.add(8, CheckersData.RED);
		assertTrue("ScoreBoard should give higher score for more black adjacents",
				poly.scoreBoard(dataWithoutBlack) > poly.scoreBoard(dataWithBlack));
	}
}
