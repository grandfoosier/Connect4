import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Connect4 class representing the board
 * functionality includes finding all valid moves, seeing if a board is a win
 * for either player, and scoring the board (High is better for player 1, low
 * for player 2)
 */
public class GameState {

	private int player;
	private int[][] board;
	private int maxTurnTime;
	private int moveIn;
	private double score;
	private ArrayList<GameState> children = new ArrayList<>();
	private final static HashMap<Integer, Double> evals = new HashMap<>();

	public int getPlayer() {
		return player;
	}

	public void setPlayer(int player) {
		this.player = player;
	}

	public int[][] getBoard() {
		return board;
	}

	public void setBoard(int[][] board) {
		this.board = new int[6][7];
		for (int r = 0; r < 6; r++)
			System.arraycopy(board[r], 0, this.board[r], 0, 7);
	}

	public int getMaxTurnTime() {
		return maxTurnTime;
	}

	public void setMaxTurnTime(int maxTurnTime) {
		this.maxTurnTime = maxTurnTime;
	}

	public int getMoveIn() {
		return moveIn;
	}

	public void setMoveIn(int moveIn) {
		this.moveIn = moveIn;
	}

	public double getScore() {
		return score;
	}

	public double setScore(double score) {
		this.score = score;
		return score;
	}

	public ArrayList<GameState> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<GameState> children ) {
		this.children = children;
	}

	public GameState() {
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Player: ");
		builder.append(player);
		builder.append(" board: ");
		builder.append("[");
		for (int i = 0; i < board.length; i++) {
			builder.append(Arrays.toString(board[i]));
			if (i < board.length - 1) builder.append(",");
		}
		builder.append("]");
		builder.append(" maxTurnTime: ");
		builder.append(maxTurnTime);
		return builder.toString();
	}

	void printTree(int depth) {
		if (score != 0)
			System.out.printf("%s%s: %.5f %s\n", "  ".repeat(depth),
					moveIn, score, player==1?"^":"v");
		for (GameState child : children) child.printTree(depth+1);
	}

	/**
	 *
	 */
	void findChildren() {
		for (int i = 0; i < 7; i++)
			if (board[0][i] == 0) children.add(applyMove(i));
	}

	GameState applyMove(int move) {
		GameState child = new GameState();
		child.setBoard(board);
		child.setPlayer(3-player);
		child.setMoveIn(move);
		child.dropCoin(move, player);
		return child;
	}

	void dropCoin(int move, int coin) {
		board[getDepth(move)][move] = coin;
	}

	int getDepth(int i) {
		int j;
		for (j = 0; j < 6; j++) if (board[j][i] != 0) break;
		return j-1;
	}

	/**
	 *
	 * @param depth
	 * @return
	 */
	public double checkWin(int depth) {
		double s = depth+100.0;
		Integer[] win_1 = new Integer[]{1, 1, 1, 1};
		Integer[] win_2 = new Integer[]{2, 2, 2, 2};

		// Horizontal 4mers
		for (int r = 0; r < 6; r++)
			for (int c = 0; c < 4; c++)
				if (Arrays.equals(new Integer[]{board[r][c], board[r][c+1],
						board[r][c+2], board[r][c+3]}, win_1)) return setScore(s);
				else if (Arrays.equals(new Integer[]{board[r][c], board[r][c+1],
						board[r][c+2], board[r][c+3]}, win_2)) return setScore(1.0/s);
		// Vertical 4mers
		for (int r = 0; r < 3; r++)
			for (int c = 0; c < 7; c++)
				if (Arrays.equals(new Integer[]{board[r][c], board[r+1][c],
						board[r+2][c], board[r+3][c]}, win_1)) return setScore(s);
				else if (Arrays.equals(new Integer[]{board[r][c], board[r+1][c],
						board[r+2][c], board[r+3][c]}, win_2)) return setScore(1.0/s);
		// Rising diagonal 4mers
		for (int r = 3; r < 6; r++)
			for (int c = 0; c < 4; c++)
				if (Arrays.equals(new Integer[]{board[r][c], board[r-1][c+1],
						board[r-2][c+2], board[r-3][c+3]}, win_1)) return setScore(s);
				else if (Arrays.equals(new Integer[]{board[r][c], board[r-1][c+1],
						board[r-2][c+2], board[r-3][c+3]}, win_2)) return setScore(1.0/s);
		// Falling diagonal 4mers
		for (int r = 0; r < 3; r++)
			for (int c = 0; c < 4; c++)
				if (Arrays.equals(new Integer[]{board[r][c], board[r+1][c+1],
						board[r+2][c+2], board[r+3][c+3]}, win_1)) return setScore(s);
				else if (Arrays.equals(new Integer[]{board[r][c], board[r+1][c+1],
						board[r+2][c+2], board[r+3][c+3]}, win_2)) return setScore(1.0/s);
		return -1.0;
	}

	public double scoreBoard() {
		Integer sum = 0;
		for (int r = 0; r < 6; r++) sum += Arrays.hashCode(board[r]);
		if (evals.containsKey(sum)) return evals.get(sum);

		int[] scores = new int[3]; scores[1] = 1; scores[2] = 1;
		int[] scoresH = scoreHoriz();
		int[] scoresV = scoreVert();
		int[] scoresR = scoreRise();
		int[] scoresF = scoreFall();
		scores[1] += scoresH[1] + scoresV[1] + scoresR[1] + scoresF[1];
		scores[2] += scoresH[2] + scoresV[2] + scoresR[2] + scoresF[2];
		score = (double)scores[1] / scores[2];
//		if (Double.isNaN(score)) score = 100.0;
		evals.put(sum, score);
		return score;
	}

	public int[] scoreByCol() {
		// Points for each coin, more for more central columns
		int[] scores = new int[3];
		for (int r = 0; r < 6; r++) {
			scores[board[r][0]] += 1;
			scores[board[r][1]] += 2;
			scores[board[r][2]] += 3;
			scores[board[r][3]] += 4;
			scores[board[r][4]] += 3;
			scores[board[r][5]] += 2;
			scores[board[r][6]] += 1;
		}
		return scores;
	}

	public int[] scoreHoriz() {
		int[] scores = new int[3];
		int[] counts;
		// Horizontal 4mers
		for (int r = 0; r < 6; r++)
			for (int c = 0; c < 4; c++) {
				counts = new int[3];
				for (int i = 0; i < 4; i++) counts[board[r][c+i]]++;
				if (counts[1] > 0 && !(counts[2] > 0)) scores[1] += counts[1];
				else if (!(counts[1] > 0) && counts[2] > 0) scores[2] += counts[2];
			}
		return scores;
	}

	public int[] scoreVert() {
		int[] scores = new int[3];
		int[] counts;
		// Vertical 4mers
		for (int r = 0; r < 3; r++)
			for (int c = 0; c < 7; c++) {
				counts = new int[3];
				for (int i = 0; i < 4; i++) counts[board[r+i][c]]++;
				if (counts[1] > 0 && !(counts[2] > 0)) scores[1] += counts[1];
				else if (!(counts[1] > 0) && counts[2] > 0) scores[2] += counts[2];
			}
		return scores;
	}

	public int[] scoreRise() {
		int[] scores = new int[3];
		int[] counts;
		// Rising diagonal 4mers
		for (int r = 3; r < 6; r++)
			for (int c = 0; c < 4; c++) {
				counts = new int[3];
				for (int i = 0; i < 4; i++) counts[board[r-i][c+i]]++;
				if (counts[1] > 0 && !(counts[2] > 0)) scores[1] += counts[1];
				else if (!(counts[1] > 0) && counts[2] > 0) scores[2] += counts[2];
			}
		return scores;
	}

	public int[] scoreFall() {
		int[] scores = new int[3];
		int[] counts;
		// Falling diagonal 4mers
		for (int r = 0; r < 3; r++)
			for (int c = 0; c < 4; c++) {
				counts = new int[3];
				for (int i = 0; i < 4; i++) counts[board[r+i][c+i]]++;
				if (counts[1] > 0 && !(counts[2] > 0)) scores[1] += counts[1];
				else if (!(counts[1] > 0) && counts[2] > 0) scores[2] += counts[2];
			}
		return scores;
	}
}
