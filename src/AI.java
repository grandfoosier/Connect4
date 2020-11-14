import java.util.ArrayList;
import java.util.Random;

public class AI {

	public AI() {}

	public int computeMove(GameState state) {
		ArrayList<Integer> valid_moves = new ArrayList<>();
		int[][] board = state.getBoard();
		for (int i = 0; i < 7; i++) if (board[0][i] == 0) valid_moves.add(i);
		return valid_moves.get((new Random()).nextInt(valid_moves.size()));
	}
}
