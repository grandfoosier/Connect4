public class AI {
	private final int MAX_DEPTH = 7;

	public AI() {}

	public int computeMove(GameState state) {
		long startTime = System.currentTimeMillis();
		prune(state, MAX_DEPTH, Double.MIN_VALUE, Double.MAX_VALUE);
		System.out.printf("\nTime: %.3f s\n\n",
				(System.currentTimeMillis()-startTime)/1000.0);
		return bestChild(state);
	}

	public static double prune(GameState state, int depth, double a, double b) {
		double win_score = state.checkWin(depth);
		if (win_score != -1.0) return win_score;

		if (depth == 0) return state.scoreBoard();

		double value;
		state.findChildren();
		if (state.getPlayer() == 1) {
			value = Double.MIN_VALUE;
			for (GameState child : state.getChildren()) {
				value = Math.max(value, prune(child, depth-1, a, b));
				a = Math.max(a, value);
				if (a >= b) break;
			}
		} else {
			value = Double.MAX_VALUE;
			for (GameState child : state.getChildren()) {
				value = Math.min(value, prune(child, depth-1, a, b));
				b = Math.min(b, value);
				if (b <= a) break;
			}
		}
		state.setScore(value);
		return value;
	}

	public int bestChild(GameState state) {
		double value = state.getPlayer()==1? Double.MIN_VALUE: Double.MAX_VALUE;
		GameState best = state;
		for (GameState child : state.getChildren()) {
			if ((state.getPlayer()==1 && child.getScore() > value) ||
					(state.getPlayer()==2 && child.getScore() < value))
				best = child;
			value = best.getScore();
		}
		return best.getMoveIn();
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
			//sum + value as per how "good" a player's move is
		} else if (oppCount == 0) {
			//sum - value as per how "good" an opponent's move is
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
}
