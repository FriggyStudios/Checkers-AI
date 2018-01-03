/*
   Checkers Applet acquired from http://math.hws.edu/eck/cs124/javanotes3/source/Checkers.java
   Applet used as base to develop Checkers AI.
   */

/*
   This applet lets two users play checkers against each other.
   Red always starts the game.  If a player can jump an opponent's
   piece, then the player must jump.  When a player can make no more
   moves, the game ends.
   
   This file defines four classes: the main applet class, Checkers;
   CheckersCanvas, CheckersMove, and CheckersData.
   (This is not very good style; the other classes really should be
   nested classes inside the Checkers class.)
 
*/
package checkersAIPackage;


import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.Vector;


public class Checkers extends Applet {

   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

/* The main applet class only lays out the applet.  The work of
      the game is all done in the CheckersCanvas object.   Note that
      the Buttons and Label used in the applet are defined as 
      instance variables in the CheckersCanvas class.  The applet
      class gives them their visual appearance and sets their
      size and positions.*/

   public void init() {
   	   
      setLayout(null);  // I will do the layout myself.
   
      setBackground(new Color(0,150,0));  // Dark green background.
      
      /* Create the components and add them to the applet. */

      CheckersCanvas board = new CheckersCanvas();
          // Note: The constructor creates the buttons board.resignButton
          // and board.newGameButton and the Label board.message.
      add(board);

      board.newGameButton.setBackground(Color.lightGray);
      add(board.newGameButton);

      board.resignButton.setBackground(Color.lightGray);
      add(board.resignButton);
      
      board.evolButton.setBackground(Color.lightGray);
      add(board.evolButton);

      board.message.setForeground(Color.green);
      board.message.setFont(new Font("Serif", Font.BOLD, 14));
      add(board.message);
      
      /* Set the position and size of each component by calling
         its setBounds() method. */

      board.setBounds(20,20,164,164); // Note:  size MUST be 164-by-164 !
      board.newGameButton.setBounds(210, 25, 100, 30);
      board.resignButton.setBounds(210, 85, 100, 30);
      board.evolButton.setBounds(210, 145, 100, 30);
      board.message.setBounds(0, 200, 330, 30);
      if(board.board.evol)
      {
    	  board.doClickSquare(2,5);
      }
      repaint();
   }
   
} // end class Checkers




class CheckersCanvas extends Canvas implements ActionListener, MouseListener {

     /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// This canvas displays a 160-by-160 checkerboard pattern with
     // a 2-pixel black border.  It is assumed that the size of the
     // canvas is set to exactly 164-by-164 pixels.  This class does
     // the work of letting the users play checkers, and it displays
     // the checkerboard.

   Button resignButton;   // Current player can resign by clicking this button.
   Button newGameButton;  // This button starts a new game.  It is enabled only
                          //     when the current game has ended.
   Button evolButton; //Change state to 1 parent 1 offspring mutation oriented evolution of the AI's heuristic polynomia
   
   AI aiRed;
   AI aiBlack;
   Evolve evolve;
   
   String makingMoveText;
   String startGameText;
   
   Label message;   // A label for displaying messages to the user.
   
   CheckersData board;  // The data for the checkers board is kept here.
                        //    This board is also responsible for generating
                        //    lists of legal moves.

   boolean gameInProgress; // Is a game currently in progress?
   
   /* The next three variables are valid only when the game is in progress. */
   
   int selectedRow, selectedCol;  // If the current player has selected a piece to
                                  //     move, these give the row and column
                                  //     containing that piece.  If no piece is
                                  //     yet selected, then selectedRow is -1.
   

   public CheckersCanvas() {
          // Constructor.  Create the buttons and lable.  Listen for mouse
          // clicks and for clicks on the buttons.  Create the board and
          // start the first game.
      setBackground(Color.black);
      addMouseListener(this);
      setFont(new  Font("Serif", Font.BOLD, 14));
      resignButton = new Button("Resign");
      resignButton.addActionListener(this);
      newGameButton = new Button("New Game");
      newGameButton.addActionListener(this);
      evolButton = new Button("Evolve");
      evolButton.addActionListener(this);
      message = new Label("",Label.CENTER);
      board = new CheckersData(this);
      aiRed = new AI(board,CheckersData.RED,CheckersData.BLACK);
      aiBlack = new AI(board,CheckersData.BLACK,CheckersData.RED);
      evolve = new Evolve(aiRed,aiBlack,AI.fileName);
      setNotEvol();
      doNewGame();
      aiRed.makeAIMove();
	  message.setText(startGameText);
	  message.setText(startGameText);
   }
   
   public void setEvol()
   {
	   board.evol = true;
	   evolve.init();
	   makingMoveText = "Computer is making their move";
	   startGameText ="Evolution: Computer vs Computer";
   }
   public void setNotEvol()
   {
	   board.evol = false;
	   makingMoveText = "Make your move";
	   startGameText ="Playing against AI";
   }

   public void actionPerformed(ActionEvent evt) {
         // Respond to user's click on one of the two buttons.
      Object src = evt.getSource();
      if (src == newGameButton)
         doNewGame();
      else if (src == resignButton)
         doResign();
      if(src == evolButton)
      {
    	  if(board.evol)
    	  {
    		  message.setText("Already evolving!");
    		  return;
    	  }
    	  setEvol();
    	  doNewGame();
      }
   }
   

   void doNewGame() {
         // Begin a new game.
      if (gameInProgress == true && !board.evol) {
             // This should not be possible, but it doens't 
             // hurt to check.
         message.setText("Finish the current game first!");
         return;
      }
      board.setUpGame();   // Set up the pieces.
      board.currentPlayer = CheckersData.RED;   // RED moves first.
      board.legalMoves = board.getLegalMoves(CheckersData.RED);  // Get RED's legal moves.
      selectedRow = -1;   // RED has not yet selected a piece to move.
      message.setText("Computer is making their move");
      gameInProgress = true;
      newGameButton.setEnabled(false);
      resignButton.setEnabled(true);
      evolButton.setEnabled(false);
      board.movesCount = 0;
      repaint();
   }
   
   void doResign() {
          // Current player resigns.  Game ends.  Opponent wins.
       if (gameInProgress == false) {
          message.setText("There is no game in progress!");
          return;
       }
       setNotEvol();
       if (board.currentPlayer == CheckersData.RED)
          gameOver("RED resigns.  BLACK wins.",aiBlack);
       else
          gameOver("BLACK resigns.  RED winds.",aiRed);
   }
   

   void gameOver(String str,AI winner) {
          // The game ends.  The parameter, str, is displayed as a message
          // to the user.  The states of the buttons are adjusted so playes
          // can start a new game.
      message.setText(str);
      newGameButton.setEnabled(true);
      resignButton.setEnabled(false);
      gameInProgress = false;
      if(board.evol)
      {
    	  evolve.endGame(winner);
	      evolButton.setEnabled(false);
	      setEvol();
	      doNewGame();
      }
      else
      {
	      evolButton.setEnabled(true);
      }
   }
      

   void doClickSquare(int row, int col) {
         // This is called by mousePressed() when a player clicks on the
         // square in the specified row and col.  It has already been checked
         // that a game is, in fact, in progress.
         
      /* If the player clicked on one of the pieces that the player
         can move, mark this row and col as selected and return.  (This
         might change a previous selection.)  Reset the message, in
         case it was previously displaying an error message. */

      for (int i = 0; i < board.legalMoves.length; i++)
         if (board.legalMoves[i].fromRow == row && board.legalMoves[i].fromCol == col) {
            selectedRow = row;
            selectedCol = col;
            if (board.currentPlayer == CheckersData.RED)
               message.setText("Computer is making their move");
            else
               message.setText(makingMoveText);
            repaint();
            return;
         }

      /* If no piece has been selected to be moved, the user must first
         select a piece.  Show an error message and return. */

      if (selectedRow < 0) {
          message.setText("Click the piece you want to move.");
          return;
      }
      
      /* If the user clicked on a square where the selected piece can be
         legally moved, then make the move and return. */

      for (int i = 0; i < board.legalMoves.length; i++)
         if (board.legalMoves[i].fromRow == selectedRow && board.legalMoves[i].fromCol == selectedCol
                 && board.legalMoves[i].toRow == row && board.legalMoves[i].toCol == col) {
            doMakeMove(board.legalMoves[i]);
            return;
         }
         
      /* If we get to this point, there is a piece selected, and the square where
         the user just clicked is not one where that piece can be legally moved.
         Show an error message. */

      message.setText("Click the square you want to move to.");

   }  // end doClickSquare()
   

   void doMakeMove(CheckersMove move) {
          // This is called when the current player has chosen the specified
          // move.  Make the move, and then either end or continue the game
          // appropriately.
      
      board.makeMove(move);
      
      /* If the move was a jump, it's possible that the player has another
         jump.  Check for legal jumps starting from the square that the player
         just moved to.  If there are any, the player must jump.  The same
         player continues moving.
      */
      
      if (move.isJump()) {
    	  board.legalMoves = board.getLegalJumpsFrom(board.currentPlayer,move.toRow,move.toCol);
         if (board.legalMoves != null) {
            if (board.currentPlayer == CheckersData.RED)
               message.setText("Computer is making their move");
            else
               message.setText("You must continue jumping.");
            selectedRow = move.toRow;  // Since only one piece can be moved, select it.
            selectedCol = move.toCol;
            repaint();
            return;
         }
      }
      
      /* The current player's turn is ended, so change to the other player.
         Get that player's legal moves.  If the player has no legal moves,
         then the game ends. */
      
      if (board.currentPlayer == CheckersData.RED) {
    	  board.currentPlayer = CheckersData.BLACK;
    	  board.legalMoves = board.getLegalMoves(board.currentPlayer);
         if (board.legalMoves == null)
            gameOver("BLACK has no moves.  RED wins.",aiRed);
         else if (board.legalMoves[0].isJump())
            message.setText(makingMoveText +  "You must jump.");
         else
            message.setText(makingMoveText);
      }
      else {
    	  board.currentPlayer = CheckersData.RED;
    	  board.legalMoves = board.getLegalMoves(board.currentPlayer);
         if (board.legalMoves == null)
            gameOver("RED has no moves.  BLACK wins.",aiBlack);
         else if (board.legalMoves[0].isJump())
            message.setText("Computer is making their move");
         else
            message.setText("Computer is making their move");
      }
      
      /* Set selectedRow = -1 to record that the player has not yet selected
          a piece to move. */
      
      selectedRow = -1;
      
      /* As a courtesy to the user, if all legal moves use the same piece, then
         select that piece automatically so the use won't have to click on it
         to select it. */
      
      if (board.legalMoves != null) {
         boolean sameStartSquare = true;
         for (int i = 1; i < board.legalMoves.length; i++)
            if (board.legalMoves[i].fromRow != board.legalMoves[0].fromRow
                                 || board.legalMoves[i].fromCol != board.legalMoves[0].fromCol) {
                sameStartSquare = false;
                break;
            }
         if (sameStartSquare) {
            selectedRow = board.legalMoves[0].fromRow;
            selectedCol = board.legalMoves[0].fromCol;
         }
      }
      
      /* Make sure the board is redrawn in its new state. */
      
      repaint();
      
   }  // end doMakeMove();
   

   public void update(Graphics g) {
        // The paint method completely redraws the canvas, so don't erase
        // before calling paint().
      paint(g);
      if(gameInProgress)
      {
    	  if(board.currentPlayer == CheckersData.RED)
		      while(board.currentPlayer == CheckersData.RED)
		      {
		    	  aiRed.makeAIMove();
		    	  /*try 
	    	   	{
					Thread.sleep(1000);
				}
				catch (InterruptedException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
		      }
    	  else if(board.evol)
    		  while(board.currentPlayer == CheckersData.BLACK)
		      {
		    	  aiBlack.makeAIMove();  
		    	  /*try 
		    	  {
						Thread.sleep(1000);
					} 
					catch (InterruptedException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		      	  }*/
		      }
      }
   }
   

   public void paint(Graphics g) {
        // Draw  checker board pattern in gray and lightGray.  Draw the
        // checkers.  If a game is in progress, hilite the legal moves.
      
      /* Draw a two-pixel black border around the edges of the canvas. */
      
      g.setColor(Color.black);
      g.drawRect(0,0,getSize().width-1,getSize().height-1);
      g.drawRect(1,1,getSize().width-3,getSize().height-3);
      
      /* Draw the squares of the checker board and the checkers. */
      
      for (int row = 0; row < 8; row++) {
         for (int col = 0; col < 8; col++) {
             if ( row % 2 == col % 2 )
                g.setColor(Color.lightGray);
             else
                g.setColor(Color.gray);
             g.fillRect(2 + col*20, 2 + row*20, 20, 20);
             switch (board.pieceAt(row,col)) {
                case CheckersData.RED:
                   g.setColor(Color.red);
                   g.fillOval(4 + col*20, 4 + row*20, 16, 16);
                   break;
                case CheckersData.BLACK:
                   g.setColor(Color.black);
                   g.fillOval(4 + col*20, 4 + row*20, 16, 16);
                   break;
                case CheckersData.RED_KING:
                   g.setColor(Color.red);
                   g.fillOval(4 + col*20, 4 + row*20, 16, 16);
                   g.setColor(Color.white);
                   g.drawString("K", 7 + col*20, 16 + row*20);
                   break;
                case CheckersData.BLACK_KING:
                   g.setColor(Color.black);
                   g.fillOval(4 + col*20, 4 + row*20, 16, 16);
                   g.setColor(Color.white);
                   g.drawString("K", 7 + col*20, 16 + row*20);
                   break;
             }
         }
      }
    
      /* If a game is in progress, hilite the legal moves.   Note that legalMoves
         is never null while a game is in progress. */      
      
      if (gameInProgress) {
            // First, draw a cyan border around the pieces that can be moved.
         g.setColor(Color.cyan);
         if(board.currentPlayer == CheckersData.BLACK)
	         for (int i = 0; i < board.legalMoves.length; i++) 
	         {
	            g.drawRect(2 + board.legalMoves[i].fromCol*20, 2 + board.legalMoves[i].fromRow*20, 19, 19);
	         }
            // If a piece is selected for moving (i.e. if selectedRow >= 0), then
            // draw a 2-pixel white border around that piece and draw green borders 
            // around eacj square that that piece can be moved to.
         if (selectedRow >= 0) {
            g.setColor(Color.white);
            g.drawRect(2 + selectedCol*20, 2 + selectedRow*20, 19, 19);
            g.drawRect(3 + selectedCol*20, 3 + selectedRow*20, 17, 17);
            g.setColor(Color.green);
            for (int i = 0; i < board.legalMoves.length; i++) {
               if (board.legalMoves[i].fromCol == selectedCol && board.legalMoves[i].fromRow == selectedRow)
                  g.drawRect(2 + board.legalMoves[i].toCol*20, 2 + board.legalMoves[i].toRow*20, 19, 19);
            }
         }
      }
   }  // end paint()
   
   
   public Dimension getPreferredSize() {
         // Specify desired size for this component.  Note:
         // the size MUST be 164 by 164.
      return new Dimension(164, 164);
   }


   public Dimension getMinimumSize() {
      return new Dimension(164, 164);
   }
   

   public void mousePressed(MouseEvent evt) {
         // Respond to a user click on the board.  If no game is
         // in progress, show an error message.  Otherwise, find
         // the row and column that the user clicked and call
         // doClickSquare() to handle it.
      if (gameInProgress == false)
         message.setText("Click \"New Game\" to start a new game.");
      else {
         int col = (evt.getX() - 2) / 20;
         int row = (evt.getY() - 2) / 20;
         if (col >= 0 && col < 8 && row >= 0 && row < 8)
            doClickSquare(row,col);
      }
   }
   

   public void mouseReleased(MouseEvent evt) { }
   public void mouseClicked(MouseEvent evt) { }
   public void mouseEntered(MouseEvent evt) { }
   public void mouseExited(MouseEvent evt) { }


}  // end class SimpleCheckerboardCanvas




class CheckersMove {
     // A CheckersMove object represents a move in the game of Checkers.
     // It holds the row and column of the piece that is to be moved
     // and the row and column of the square to which it is to be moved.
     // (This class makes no guarantee that the move is legal.)
   byte fromRow, fromCol;  // Position of piece to be moved.
   byte toRow, toCol;      // Square it is to move to.
   CheckersMove(int r1, int c1, int r2, int c2) {
        // Constructor.  Just set the values of the instance variables.
      fromRow = (byte) r1;
      fromCol = (byte) c1;
      toRow = (byte) r2;
      toCol = (byte) c2;
   }
   CheckersMove(CheckersMove move)
   {
	   fromRow = move.fromRow;
	   fromCol = move.fromCol;
	   toRow = move.toRow;
	   toCol = move.toCol;
   }
   boolean isJump() {
        // Test whether this move is a jump.  It is assumed that
        // the move is legal.  In a jump, the piece moves two
        // rows.  (In a regular move, it only moves one row.)
      return (fromRow - toRow == 2 || fromRow - toRow == -2);
   }
}  // end class CheckersMove.




//implement board Sparse Matrix!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
class CheckersData {

      // An object of this class holds data about a game of checkers.
      // It knows what kind of piece is on each sqaure of the checkerboard.
      // Note that RED moves "up" the board (i.e. row number decreases)
      // while BLACK moves "down" the board (i.e. row number increases).
      // Methods are provided to return lists of available legal moves.
      
   /*  The following constants represent the possible contents of a square
       on the board.  The constants RED and BLACK also represent players
       in the game.
   */
  
   CheckersCanvas canvas;
   public static final byte
             EMPTY = 0,
             RED = 1,
             RED_KING = 2,
             BLACK = 3,
             BLACK_KING = 4;

   Board board; 

   byte currentPlayer;      // Whose turn is it now?  The possible values
                           //    are CheckersData.RED and CheckersData.BLACK.

   CheckersMove[] legalMoves;  // An array containing the legal moves for the
                               //   current player.   
   int movesCount = 0;
   int movesBeforeDraw = 100;
   boolean evol = false;

   public CheckersData(CheckersCanvas canvas) {
         // Constructor.  Create the board and set it up for a new game.
	  board = new Board();
	  if(canvas != null)
	  {
	    this.canvas = canvas;
        setUpGame();
	  }
   }
   
   public CheckersData(CheckersData data) 
   {
	  board = new Board(data.board);
	  this.currentPlayer = data.currentPlayer;
	  this.legalMoves = data.legalMoves;
   }
   public CheckersData(Board newBoard) 
   {
	  board = new Board(newBoard);
   }
   
@Override
public boolean equals(Object o) 
{ 
	// self check
    if (this == o)
        return true;
    // null check
    if (o == null)
        return false; 
    // type check and cast
    if (getClass() != o.getClass())
        return false;
    CheckersData data = (CheckersData) o;
    if(currentPlayer != data.currentPlayer)
    {
    	return false;
    }
	for (int row = 0; row < 8; row++) {
	      for (int col = 0; col < 8; col++) {
	    	 if(board.Get(row,col) != data.board.Get(row,col))
	    	 {
	    		 return false;
	    	 }
	      }
	  }
	return true;
}

public void setUpGame() {
          // Set up the board with checkers in position for the beginning
          // of a game.  Note that checkers can only be found in squares
          // that satisfy  row % 2 == col % 2.  At the start of the game,
          // all such squares in the first three rows contain black squares
          // and all such squares in the last three rows contain red squares.
      for (int row = 0; row < 8; row++) {
         for (int col = 0; col < 8; col++) {
            if ( row % 2 != col % 2 ) {
               if (row < 3)
            	   board.Add(BLACK,row,col);
               else if (row > 4)
            	   board.Add(RED,row,col);
               else
            	   board.Add(EMPTY, row, col);
            }
         }
      }
    
   }  // end setUpGame()
   

   public byte pieceAt(int row, int col) {
          // Return the contents of the square in the specified row and column.
       return board.Get(row,col);
   }
   

   public void setPieceAt(int row, int col, byte piece) {
          // Set the contents of the square in the specified row and column.
          // piece must be one of the constants EMPTY, RED, BLACK, RED_KING,
          // BLACK_KING.
	   board.Add(piece,row,col);
   }
   

   public void makeMove(CheckersMove move) {
         // Make the specified move.  It is assumed that move
         // is non-null and that the move it represents is legal.
	   if(evol && movesCount >= movesBeforeDraw)
	   {
		   canvas.evolve.endGameDraw(this);
		   canvas.evolve.init();
		   canvas.doNewGame();
	   }
      makeMove(move.fromRow, move.fromCol, move.toRow, move.toCol);
   }
   

   public void makeMove(int fromRow, int fromCol, int toRow, int toCol) {
         // Make the move from (fromRow,fromCol) to (toRow,toCol).  It is
         // assumed that this move is legal.  If the move is a jump, the
         // jumped piece is removed from the board.  If a piece moves
         // the last row on the opponent's side of the board, the 
         // piece becomes a king.
	  movesCount++;
      board.Add(board.Get(fromRow, fromCol),toRow, toCol);
      board.Add(EMPTY, fromRow, fromCol);
      if (fromRow - toRow == 2 || fromRow - toRow == -2) {
            // The move is a jump.  Remove the jumped piece from the board.
         int jumpRow = (fromRow + toRow) / 2;  // Row of the jumped piece.
         int jumpCol = (fromCol + toCol) / 2;  // Column of the jumped piece.
         board.Add(EMPTY, jumpRow, jumpCol);
      }
      if (toRow == 0 && board.Get(toRow, toCol) == RED)
         board.Add(RED_KING, toRow, toCol);
      if (toRow == 7 && board.Get(toRow, toCol) == BLACK)
    	  board.Add(BLACK_KING, toRow, toCol);
      
   }
   

   public CheckersMove[] getLegalMoves(int player) {
          // Return an array containing all the legal CheckersMoves
          // for the specified player on the current board.  If the player
          // has no legal moves, null is returned.  The value of player
          // should be one of the constants RED or BLACK; if not, null
          // is returned.  If the returned value is non-null, it consists
          // entirely of jump moves or entirely of regular moves, since
          // if the player can jump, only jumps are legal moves.

      if (player != RED && player != BLACK)
         return null;

      int playerKing;  // The constant representing a King belonging to player.
      if (player == RED)
         playerKing = RED_KING;
      else
         playerKing = BLACK_KING;

      Vector<CheckersMove> moves = new Vector<CheckersMove>();  // Moves will be stored in this vector.
      
      /*  First, check for any possible jumps.  Look at each square on the board.
          If that square contains one of the player's pieces, look at a possible
          jump in each of the four directions from that square.  If there is 
          a legal jump in that direction, put it in the moves vector.
      */
      for (byte row = 1; row < 8; row+=2) {
         for (byte col = 0; col < 8; col+=2) {
            if (board.Get(row, col) == player || board.Get(row, col) == playerKing) {
               if (canJump(player, row, col, row+1, col+1, row+2, col+2))
                  moves.addElement(new CheckersMove(row, col, row+2, col+2));
               if (canJump(player, row, col, row-1, col+1, row-2, col+2))
                  moves.addElement(new CheckersMove(row, col, row-2, col+2));
               if (canJump(player, row, col, row+1, col-1, row+2, col-2))
                  moves.addElement(new CheckersMove(row, col, row+2, col-2));
               if (canJump(player, row, col, row-1, col-1, row-2, col-2))
                  moves.addElement(new CheckersMove(row, col, row-2, col-2));
            }
         }
      }for (byte row = 0; row < 8; row+=2) {
          for (byte col = 1; col < 8; col+=2) {
              if (board.Get(row, col) == player || board.Get(row, col) == playerKing) {
                 if (canJump(player, row, col, row+1, col+1, row+2, col+2))
                    moves.addElement(new CheckersMove(row, col, row+2, col+2));
                 if (canJump(player, row, col, row-1, col+1, row-2, col+2))
                    moves.addElement(new CheckersMove(row, col, row-2, col+2));
                 if (canJump(player, row, col, row+1, col-1, row+2, col-2))
                    moves.addElement(new CheckersMove(row, col, row+2, col-2));
                 if (canJump(player, row, col, row-1, col-1, row-2, col-2))
                    moves.addElement(new CheckersMove(row, col, row-2, col-2));
              }
           }
        }
      
      /*  If any jump moves were found, then the user must jump, so we don't 
          add any regular moves.  However, if no jumps were found, check for
          any legal regular moves.  Look at each square on the board.
          If that square contains one of the player's pieces, look at a possible
          move in each of the four directions from that square.  If there is 
          a legal move in that direction, put it in the moves vector.
      */
      
      if (moves.size() == 0) {
         for (int row = 1; row < 8; row+=2) {
            for (int col = 0; col < 8; col+=2) {
               if (board.Get(row, col) == player || board.Get(row, col) == playerKing) {
                  if (canMove(player,row,col,row+1,col+1))
                     moves.addElement(new CheckersMove(row,col,row+1,col+1));
                  if (canMove(player,row,col,row-1,col+1))
                     moves.addElement(new CheckersMove(row,col,row-1,col+1));
                  if (canMove(player,row,col,row+1,col-1))
                     moves.addElement(new CheckersMove(row,col,row+1,col-1));
                  if (canMove(player,row,col,row-1,col-1))
                     moves.addElement(new CheckersMove(row,col,row-1,col-1));
               }
            }
         }for (int row = 0; row < 8; row+=2) {
             for (int col = 1; col < 8; col+=2) {
                 if (board.Get(row, col) == player || board.Get(row, col) == playerKing) {
                    if (canMove(player,row,col,row+1,col+1))
                       moves.addElement(new CheckersMove(row,col,row+1,col+1));
                    if (canMove(player,row,col,row-1,col+1))
                       moves.addElement(new CheckersMove(row,col,row-1,col+1));
                    if (canMove(player,row,col,row+1,col-1))
                       moves.addElement(new CheckersMove(row,col,row+1,col-1));
                    if (canMove(player,row,col,row-1,col-1))
                       moves.addElement(new CheckersMove(row,col,row-1,col-1));
                 }
              }
           }
      }
      
      /* If no legal moves have been found, return null.  Otherwise, create
         an array just big enough to hold all the legal moves, copy the
         legal moves from the vector into the array, and return the array. */
      
      if (moves.size() == 0)
         return null;
      else {
         CheckersMove[] moveArray = new CheckersMove[moves.size()];
         for (int i = 0; i < moves.size(); i++)
            moveArray[i] = (CheckersMove)moves.elementAt(i);
         return moveArray;
      }

   }  // end getLegalMoves
   

   public CheckersMove[] getLegalJumpsFrom(int player, int row, int col) {
         // Return a list of the legal jumps that the specified player can
         // make starting from the specified row and column.  If no such
         // jumps are possible, null is returned.  The logic is similar
         // to the logic of the getLegalMoves() method.
      if (player != RED && player != BLACK)
         return null;
      int playerKing;  // The constant representing a King belonging to player.
      if (player == RED)
         playerKing = RED_KING;
      else
         playerKing = BLACK_KING;
      Vector<CheckersMove> moves = new Vector<CheckersMove>();  // The legal jumps will be stored in this vector.
      if (board.Get(row, col) == player || board.Get(row, col) == playerKing) {
         if (canJump(player, row, col, row+1, col+1, row+2, col+2))
            moves.addElement(new CheckersMove(row, col, row+2, col+2));
         if (canJump(player, row, col, row-1, col+1, row-2, col+2))
            moves.addElement(new CheckersMove(row, col, row-2, col+2));
         if (canJump(player, row, col, row+1, col-1, row+2, col-2))
            moves.addElement(new CheckersMove(row, col, row+2, col-2));
         if (canJump(player, row, col, row-1, col-1, row-2, col-2))
            moves.addElement(new CheckersMove(row, col, row-2, col-2));
      }
      if (moves.size() == 0)
         return null;
      else {
         CheckersMove[] moveArray = new CheckersMove[moves.size()];
         for (int i = 0; i < moves.size(); i++)
            moveArray[i] = (CheckersMove)moves.elementAt(i);
         return moveArray;
      }
   }  // end getLegalMovesFrom()
   

   public boolean canJump(int player, int r1, int c1, int r2, int c2, int r3, int c3) {
           // This is called by the two previous methods to check whether the
           // player can legally jump from (r1,c1) to (r3,c3).  It is assumed
           // that the player has a piece at (r1,c1), that (r3,c3) is a position
           // that is 2 rows and 2 columns distant from (r1,c1) and that 
           // (r2,c2) is the square between (r1,c1) and (r3,c3).
           
      if (r3 < 0 || r3 >= 8 || c3 < 0 || c3 >= 8)
         return false;  // (r3,c3) is off the board.
         
      if (board.Get(r3, c3) != EMPTY)
         return false;  // (r3,c3) already contains a piece.
         
      if (player == RED) {
         if (board.Get(r1, c1) == RED && r3 > r1)
            return false;  // Regular red piece can only move  up.
         if (board.Get(r2,c2) != BLACK && board.Get(r2,c2) != BLACK_KING)
            return false;  // There is no black piece to jump.
         return true;  // The jump is legal.
      }
      else {
         if (board.Get(r1,c1) == BLACK && r3 < r1)
            return false;  // Regular black piece can only move down.
         if (board.Get(r2,c2) != RED && board.Get(r2,c2) != RED_KING)
            return false;  // There is no red piece to jump.
         return true;  // The jump is legal.
      }

   }  // end canJump()
   

   private boolean canMove(int player, int r1, int c1, int r2, int c2) {
         // This is called by the getLegalMoves() method to determine whether
         // the player can legally move from (r1,c1) to (r2,c2).  It is
         // assumed that (r1,r2) contains one of the player's pieces and
         // that (r2,c2) is a neighboring square.
         
      if (r2 < 0 || r2 >= 8 || c2 < 0 || c2 >= 8)
         return false;  // (r2,c2) is off the board.
         
      if (board.Get(r2,c2) != EMPTY)
         return false;  // (r2,c2) already contains a piece.

      if (player == RED) {
         if (board.Get(r1,c1) == RED && r2 > r1)
             return false;  // Regular red piece can only move down.
          return true;  // The move is legal.
      }
      else {
         if (board.Get(r1,c1) == BLACK && r2 < r1)
             return false;  // Regular black piece can only move up.
          return true;  // The move is legal.
      }
      
   }  // end canMove()
   

} // end class CheckersData