import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class GameState {

	private int player;
	private int[][] board;
	private int maxTurnTime;
	private int moveIn;
	private double score;
	private ArrayList<GameState> children = new ArrayList<>();
	private ArrayList<Integer[]> horiz, vert, rise, fall;

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
		child.make4s();
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

	public double scoreBoard() {
		if (score == 0) score = new Random().nextDouble()*9.999+0.001;
		return score;
	}

	public double checkWin(int depth) {
		double s = depth+100.0;
		Integer[] win_1 = new Integer[]{1, 1, 1, 1};
		Integer[] win_2 = new Integer[]{2, 2, 2, 2};
		for (Integer[] i : horiz)
			if (Arrays.equals(i, win_1)) return setScore(s);
			else if (Arrays.equals(i, win_2)) return setScore(1.0/s);
		for (Integer[] i : vert)
			if (Arrays.equals(i, win_1)) return setScore(s);
			else if (Arrays.equals(i, win_2)) return setScore(1.0/s);
		for (Integer[] i : rise)
			if (Arrays.equals(i, win_1)) return setScore(s);
			else if (Arrays.equals(i, win_2)) return setScore(1.0/s);
		for (Integer[] i : fall)
			if (Arrays.equals(i, win_1)) return setScore(s);
			else if (Arrays.equals(i, win_2)) return setScore(1.0/s);
		return -1.0;
	}

	public void make4s() {
		horiz = new ArrayList<>();
		for (int r = 0; r < 6; r++)
			for (int c = 0; c < 4; c++)
				horiz.add(new Integer[]{board[r][c], board[r][c+1],
						board[r][c+2], board[r][c+3]});
		vert = new ArrayList<>();
		for (int r = 0; r < 3; r++)
			for (int c = 0; c < 7; c++)
				vert.add(new Integer[]{board[r][c], board[r+1][c],
						board[r+2][c], board[r+3][c]});
		rise = new ArrayList<>();
		for (int r = 3; r < 6; r++)
			for (int c = 0; c < 4; c++)
				rise.add(new Integer[]{board[r][c], board[r-1][c+1],
						board[r-2][c+2], board[r-3][c+3]});
		fall = new ArrayList<>();
		for (int r = 0; r < 3; r++)
			for (int c = 0; c < 4; c++)
				fall.add(new Integer[]{board[r][c], board[r+1][c+1],
						board[r+2][c+2], board[r+3][c+3]});
	}

	void printTree(int depth) {
		if (score != 0)
			System.out.printf("%s%s: %.5f %s\n", "  ".repeat(depth),
					moveIn, score, player==1?"^":"v");
		for (GameState child : children) child.printTree(depth+1);
	}
}
