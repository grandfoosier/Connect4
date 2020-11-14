public class AI {
	private final int MAX_DEPTH = 7;

	public AI() {}

	public int computeMove(GameState state) {
		long startTime = System.currentTimeMillis();
		prune(state, MAX_DEPTH, Double.MIN_VALUE, Double.MAX_VALUE);
		return 0;
	}

	private double prune(GameState state, int depth, double a, double b) {
		if (depth == 0) return state.scoreBoard();

		double win_score = state.checkWin();
		if (win_score != 0.0) return win_score;

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
}
