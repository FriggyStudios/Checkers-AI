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
	private CheckersMoveScore NoBlackPieces;
	private CheckersMoveScore NoRedPieces;
	private CheckersMoveScore NoBlackMoves;
	private CheckersMoveScore NoRedMoves;
	private CheckersMoveScore OpenGame;

	@Before
 	public void setUp()
	{
		move1 = new CheckersMoveScore(score,new CheckersMove(0,0,1,1),new CheckersData((CheckersCanvas)null),CheckersData.BLACK,CheckersData.RED);
		move2 = new CheckersMoveScore(lowerScore,new CheckersMove(0,0,1,1),new CheckersData((CheckersCanvas)null),CheckersData.BLACK,CheckersData.RED);
		move3 = new CheckersMoveScore(score,new CheckersMove(0,0,1,1),new CheckersData((CheckersCanvas)null),CheckersData.RED,CheckersData.BLACK);
		move4 = new CheckersMoveScore(lowerScore,new CheckersMove(0,0,1,1),new CheckersData((CheckersCanvas)null),CheckersData.RED,CheckersData.BLACK);		
		
		//NoBlackPieces
		ArrayList<Byte> entries = new ArrayList<Byte>();
		Board board;
		for(int i = 0;i < 32;i++)
	    {
			entries.add(CheckersData.EMPTY);		   
	    }
		entries.add(0,CheckersData.RED_KING);
		entries.add(20,CheckersData.RED);
		board = new Board(entries);
		NoBlackPieces = new CheckersMoveScore(score,new CheckersMove(0,0,1,1),new CheckersData(board),CheckersData.RED,CheckersData.BLACK);
		
		//NoRedPieces
		entries = new ArrayList<Byte>();
		for(int i = 0;i < 32;i++)
	    {
			entries.add(CheckersData.EMPTY);		   
	    }
		entries.add(14,CheckersData.BLACK);
		entries.add(4,CheckersData.BLACK_KING);
		board = new Board(entries);
		NoRedPieces = new CheckersMoveScore(score,new CheckersMove(0,0,1,1),new CheckersData(board),CheckersData.BLACK,CheckersData.RED);
		
		//NoBlackMoves
		entries = new ArrayList<Byte>();
		for(int i = 0;i < 32;i++)
	    {
			entries.add(CheckersData.EMPTY);		   
	    }
		entries.add(27,CheckersData.BLACK_KING);
		entries.add(31,CheckersData.BLACK_KING);
		entries.add(26,CheckersData.RED_KING);
		entries.add(23,CheckersData.RED_KING);
		entries.add(22,CheckersData.RED_KING);
		entries.add(18,CheckersData.RED_KING);
		board = new Board(entries);
		NoBlackMoves = new CheckersMoveScore(score,new CheckersMove(0,0,1,1),new CheckersData(board),CheckersData.BLACK,CheckersData.RED);
		
		//NoRedMoves
		entries = new ArrayList<Byte>();
		for(int i = 0;i < 32;i++)
	    {
			entries.add(CheckersData.EMPTY);		   
	    }
		entries.add(27,CheckersData.RED_KING);
		entries.add(31,CheckersData.RED_KING);
		entries.add(26,CheckersData.BLACK_KING);
		entries.add(23,CheckersData.BLACK_KING);
		entries.add(22,CheckersData.BLACK_KING);
		entries.add(18,CheckersData.BLACK_KING);
		board = new Board(entries);
		NoRedMoves = new CheckersMoveScore(score,new CheckersMove(0,0,1,1),new CheckersData(board),CheckersData.RED,CheckersData.BLACK);
		
		//OpenGame
		entries = new ArrayList<Byte>();
		for(int i = 0;i < 32;i++)
	    {
			entries.add(CheckersData.EMPTY);		   
	    }
		entries.add(0,CheckersData.RED_KING);
		entries.add(20,CheckersData.RED);
		entries.add(14,CheckersData.BLACK);
		entries.add(4,CheckersData.BLACK_KING);
		board = new Board(entries);
		OpenGame = new CheckersMoveScore(score,new CheckersMove(0,0,1,1),new CheckersData(board),CheckersData.RED,CheckersData.BLACK);
	}
 	@After
 	public void setDown()
 	{
 		move1 = null;
 		move2 = null;
 		move3 = null;
 		move4 = null;
 		NoBlackPieces = null;
 		NoRedPieces = null;
 		NoBlackMoves = null;
 		NoRedMoves = null;
 		OpenGame = null;
 	}
	@Test(timeout=10)
	public void compareToTest()
	{
		assertNotNull("CheckersMoveScore comparison should not be null",
				move1.compareTo(move2));
	}
	@Test(timeout=10)
	public void sortTest()
	{
		moves.add(move1);
		moves.add(move2);
		Collections.sort(moves);
		assertNotNull("Sorted CheckersMoveScore should not be null", moves);
		
		assertSame("CheckersMoveScore top should be BLACK",CheckersData.BLACK,moves.get(0).playerMove);
		assertSame("CheckersMoveScore second top should be BLACK",CheckersData.BLACK,moves.get(1).playerMove);
		assertSame("CheckersMoveScore top should be move 2",move2,moves.get(0));
		assertSame("CheckersMoveScore second top should be move 1",move1,moves.get(1));
		assertTrue("CheckersMoveScore top should be less than CheckersMoveScore second top",moves.get(0).score < moves.get(1).score);
		
		moves = new ArrayList<CheckersMoveScore>();
		moves.add(move3);
		moves.add(move4);
		Collections.sort(moves);
		assertNotNull("Sorted CheckersMoveScore should not be null",moves);
		
		assertSame("CheckersMoveScore top should be RED",CheckersData.RED,moves.get(0).playerMove);
		assertSame("CheckersMoveScore second top should be BLACK",CheckersData.RED,moves.get(1).playerMove);
		assertSame("CheckersMoveScore top should be move 4",move4,moves.get(0));
		assertSame("CheckersMoveScore second top should be move 3",move3,moves.get(1));
		assertTrue("CheckersMoveScore top should be less than CheckersMoveScore second top",moves.get(0).score < moves.get(1).score);
	}
	@Test(timeout=10)
	public void finalStateTest()
	{
		assertTrue("No black pieces, red should win",NoBlackPieces.finalState);
		assertTrue("No red pieces, black should win",NoRedPieces.finalState);
		assertTrue("No black moves, red should win",NoBlackMoves.finalState);
		assertTrue("No red moves, black should win",NoRedMoves.finalState);
		assertTrue("Moves for both players, game not over",!OpenGame.finalState);
	}
}