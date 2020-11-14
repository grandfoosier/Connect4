public class AI {
	private final int MAX_DEPTH = 5;

	public AI() {}

	public int computeMove(GameState state) {
		long startTime = System.currentTimeMillis();
		state.make4s();
		prune(state, MAX_DEPTH, Double.MIN_VALUE, Double.MAX_VALUE);
		System.out.printf("\nTime: %.3f s\n\n",
				(System.currentTimeMillis()-startTime)/1000.0);
		for (int r = 0; r < 6; r++) {
			for (int c = 0; c < 7; c++)
				System.out.print(state.getBoard()[r][c] + " ");
			System.out.println();
		}
		return bestChild(state);
	}

	private double prune(GameState state, int depth, double a, double b) {
		if (depth == 0) return state.scoreBoard();

		double win_score = state.checkWin(depth);
		if (win_score != -1.0) return win_score;

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
			if (state.getPlayer()==1) {
				if (child.getScore() > value) best = child;
			} else {
				if (child.getScore() < value) best = child;
			}
			value = best.getScore();
			System.out.printf("%s: %.5f\n",
					child.getMoveIn(), child.getScore());
		}
		System.out.printf("\nBest: %s\n", best.getMoveIn());
		return best.getMoveIn();
	}
}
