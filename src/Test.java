public class Test {
    public static void main(String[] args) {
        GameState state = new GameState();
        int[][] board = new int[][]{
                {0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0}};
        state.setBoard(board);
        state.setPlayer(2);
        state.setMoveIn(-1);
//        state.findChildren();
//        for (GameState child : state.getChildren()) {
//            for (int r = 0; r < 6; r++) {
//                for (int c = 0; c < 7; c++)
//                    System.out.print(child.getBoard()[r][c]);
//                System.out.println();
//            } System.out.println(child.checkWin(5)+"\n");
//        }
//        AI.prune(state, 3, Double.MIN_VALUE, Double.MAX_VALUE);
//        state.printTree(0);

        MCTS mcts = new MCTS();
        int move = mcts.chooseMove(state);
        System.out.println(move);
    }
}
