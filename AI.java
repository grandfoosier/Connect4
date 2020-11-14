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
			//change the sum value as per how "good" a move is
		} else if (oppCount == 0) {
			//change the sum value as per how "good" a move is
		}

		return sum;
	}
}