package com.atomicobject.connectfour;
import java.util.ArrayList;
import java.util.Random;

public class AI {
	private final int MAX_DEPTH = 7;

	public AI() {}

	public int computeMove(GameState state) {
		long startTime = System.currentTimeMillis();
		prune(state, MAX_DEPTH, Double.MIN_VALUE, Double.MAX_VALUE);
		return 0;
	}

	/**
	 * Method for checking the Connect 4 verticals and assigning them based on how close they are to a connect 4
	 *
	 * @param mainP the player whose turn it is
	 * @param opp the player's opponent
	 * @param row row we are checking
	 * @param column column we are checking
	 * @return sum int that is a representation of the position strength
	 */
	private int scoreCalcVertical(GameState state, char mainP, char opp, int row, int column){
		int bottomValue;
		bottomValue = Math.max(0, row - 3);
		int topValue = bottomValue + 4;

		// counters to calculate eval values
		int pCount = 0;
		int oppCount = 0;
		int vValue = 0;

		// Check for the Connect 4 from the bottom up
		for (int checkRow = bottomValue; checkRow < topValue; checkRow++){
			if (state.getBoard()[checkRow][column] == opp){
				oppCount = oppCount + 1;
			} else if (state.getBoard()[checkRow][column] == mainP){
				pCount = pCount + 1;
			}
		}

		// if there isn't the other player's piece in the way, weight by position strength
		vValue = applyWeights(pCount, oppCount, vValue);

		// return this sum for the analysis of the verticals
		return vValue;
	}

	/**
	 * Public helper method to apply weights after looking at Connect 4
	 * possibilities
	 *
	 * @param pCount the number of pieces player has in the connect 4 line
	 * @param oppCount the number of pieces opponent has in the connect 4 line
	 * @param sum the weighted sum so far
	 * @return the new sum after applying the weights.
	 */
	public static int applyWeights(int pCount, int oppCount, int sum){
		// apply the weights based on the previous connect 4 possibilities
		if (pCount == 0){
			sum = sum - HOW_GOOD[oppCount];
		} else if (oppCount == 0) {
			sum = sum + HOW_GOOD[pCount];
		}

		return sum;
	}

	/**
	 * Evaluates the possibilities for diagonal and horizontal connect fours
	 *
	 * @param mainP the main player
	 * @param opp the other player
	 * @param leftBound the leftside bound of the connect 4
	 * @param rightBound the rightside bound of the connect 4
	 * @param currentRow the open row that the move piece would go into
	 * @param offsetRow the offset for diagonals (1 or -1), 0 for horizontals
	 * @return
	 */
	private int evalPossibilities(GameState state, char mainP, char opp, int leftBound, int rightBound, int currentRow, int offsetRow){

		int boundDiff = rightBound - leftBound;
		int oppCount = 0;
		int pCount = 0;
		int sum = 0;
		int checkColumn = leftBound;
		int checkRow = currentRow;

		// -4 or 4 depending on which type of diagonal
		// 0 if checking horizontal
		int diagonalDelta = offsetRow * 4;

		if (boundDiff < 3) {
			return 0;
		}

		// ++ for row and column for diagonals
		// ++ for column for horizontals
		for (checkColumn = checkColumn; checkColumn <= leftBound + 3; checkRow += offsetRow) {

			// check whose pieces belong to whom
			if (state.getBoard()[checkRow][checkColumn] == opp){
				oppCount = oppCount + 1;
			} else if (state.getBoard()[checkRow][checkColumn] == mainP){
				pCount = pCount + 1;
			}
			checkColumn = checkColumn + 1;

		}

		// apply the weights based on the previous connect 4 possibilities
		sum = applyWeights(pCount, oppCount, sum);

		// ++ for row and column for diagonals
		// ++ for column for horizontals
		for (checkColumn = checkColumn;
			 checkColumn <= rightBound;
			 checkRow += offsetRow){
			if (state.getBoard()[(checkRow - diagonalDelta)][(checkColumn - 4)] == opp){
				oppCount = oppCount -1;
			}

			if (state.getBoard()[(checkRow - diagonalDelta)][(checkColumn - 4)] == mainP) {
				pCount = pCount -1;
			}

			if (state.getBoard()[checkRow][checkColumn] == opp){
				oppCount = oppCount + 1;
			}

			if (state.getBoard()[checkRow][checkColumn] == mainP) {
				pCount = pCount + 1;
			}

			// apply the weights
			sum = applyWeights(pCount, oppCount, sum);

			checkColumn = checkColumn + 1;
		}
		return sum;
	}




	public static final int[] HOW_GOOD = {0, 2, 10^2, 10^3, 10^8}; // index is # of unblocked four-in-row potentials

	// the closer a piece is to the center, the more 4-in-row permutations available.
	// i.e.., generally center piece is most valuable
	private static final int[] movesByCol = { 3, 4, 2, 5, 1, 6, 0 };


	/**
	 * Helper method that counts the moves made
	 *
	 * @param state the input state of the board
	 * @return the number of moves already made
	 */
	private static int movesDone(GameState state){
		// count the pieces

		int counter = 0;
		for (int row = 0; row < GameState.ROWS; row++){
			for (int column = 0; column < GameState.COLS; column++){
				if (state.getBoard()[row][column] != GameState.EMPTY)
					counter++;
			}
		}

		return counter;
	}

	/**
	 * Evaluate position by finding unblocked 4 in a rows
	 *
	 * @param state the input state of the board
	 * @return a total int evaluation of unblocked four-in-rows for opp and computer
	 */
	public static int evaluate(GameState state){
		int player = state.getPlayer();

		int[][] board = state.getBoard();

		// value that evaluates the unblocked four-in-rows
		int totalEvaluation = 0;

		// Evaluate patterns for winning
		//
		//   . X X . .   => unblocked on both sides so we can connect 4
		//  by placing another piece to become
		//  . X X X .
		for (int checkColumn = 0; checkColumn < 3; checkColumn ++){
			// if 0 is empty, followed by 2 of my pieces and two more empty, this is a pattern
			if (board[0][checkColumn] == Connect4State.EMPTY &&
					board[0][checkColumn + 1] == player &&
					board[0][checkColumn + 2] == player &&
					board[0][checkColumn + 3] == GameState.EMPTY &&
					board[0][checkColumn + 4] == GameState.EMPTY){
				totalEvaluation += HOW_GOOD[3];
			} else if (board[0][checkColumn] == GameState.EMPTY &&
					board[0][checkColumn + 1] == GameState.EMPTY &&
					board[0][checkColumn + 2] == player &&
					board[0][checkColumn + 3] == player &&
					board[0][checkColumn + 4] == GameState.EMPTY){
				totalEvaluation += HOW_GOOD[3];
			}
		}


		// Evaluate unblocked verticals
		// all potential ver 4-in-row start from at most from row 2
		for (int column = 0; column < 7; column++){
			for (int row = 0; row < 3; row++){
				int compCount = 0;
				int oppCount = 0;

				for (int checkRow = row; checkRow < row + 4; checkRow++){
					if (board[checkRow][column] == player){
						compCount++;
					} else{
						oppCount++;
					}
				}

				totalEvaluation = applyWeights(oppCount, compCount, totalEvaluation);
			}
		}

		// Evaluate unblocked horizontals
		// all potential hor 4-in-row start from at most from halfway col
		for (int column = 0; column <= 3; column++){
			for(int row = 0; row < GameState.ROWS; row++){
				// counters for computer and opponent
				int compCount = 0;
				int oppCount = 0;

				for (int checkColumn = column; checkColumn < column + 4; checkColumn++){
					// check whose checker it is and increment their counter
					if (board[row][checkColumn] == player){
						compCount++;
					} else{
						oppCount++;
					}
				}

				totalEvaluation = applyWeights(oppCount, compCount, totalEvaluation);
			}
		}

		// Evaluate unblocked diagonals (up to right)
		// up to right diagonal start at most from row 2, column 3
		for (int column = 0; column < 4; column++){
			for (int row = 0; row < 3; row++){
				int compCount = 0;
				int oppCount = 0;

				int checkRow = row; // need a checkrow parameter for diag
				for (int checkColumn = column; checkRow < row + 4; checkColumn++){
					if (board[checkRow][checkColumn] == player){
						compCount++;
					} else {
						oppCount++;
					}

					checkRow++; // adjust for diagonal
				}


				totalEvaluation = applyWeights(oppCount, compCount, totalEvaluation);
			}
		}

		// Evaluate unblocked diagonals (down to right)
		// down to right diagonal start at most from row 3, column 3
		for (int column = 0; column < 4; column++){
			for (int row = 3; row <= 5; row++){
				int compCount = 0;
				int oppCount = 0;

				int checkRow = row; // need a checkrow parameter for diag
				for (int checkColumn = column; checkColumn < column + 4; checkColumn++){
					if (board[checkRow][checkColumn] == player){
						compCount++;
					} else{
						oppCount++;
					}

					checkRow--; // adjust for diagonal
				}

				totalEvaluation = applyWeights(oppCount, compCount, totalEvaluation);
			}
		}

		return totalEvaluation;

	}
}