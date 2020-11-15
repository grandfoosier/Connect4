import java.util.*;

public class MCTS {
    private static long total_runs = 0;
    private static HashMap<String, Long[]> wins = new HashMap<>();
    private final static HashMap<String, Double> checked = new HashMap<>();

    public int chooseMove(GameState state) {

        // Check if any moves win the game (record all states)
        ArrayList<GameState> children = state.getChildren();
        if (children == null) children = state.findChildren();
        for (GameState child : children)
            if (checkWin(child)) {
                System.out.println("should win");
                return child.getMoveIn();
            }

        // If not, run the Monte Carlo search
        for (int i =0; i < 10000; i++) iterate(state);

        GameState best = null;
        long max_denom = 0;
        String hash = getHash(state);
        System.out.println(wins.get(hash)[0]+", "+wins.get(hash)[1]+", "+wins.get(hash)[2]);
        for (GameState child : children) {
            String hashC = getHash(child);
            System.out.println(wins.get(hashC)[0]+", "+wins.get(hashC)[1]+", "+wins.get(hashC)[2]+" | ");
            long denom = wins.get(hashC)[0]+wins.get(hashC)[1]+wins.get(hashC)[2];
            if (denom > max_denom) {
                max_denom = denom; best = child;
            }
        } System.out.println();

        return best.getMoveIn();
    }

    boolean checkWin(GameState state) {
        String hash = getHash(state);
        if (checked.containsKey(hash) && checked.get(hash) != -1.0)
            return true;
        else if (!checked.containsKey(hash)) {
            double isWin = state.checkWin(0);
            checked.put(hash, isWin);
            return isWin != -1.0;
        }
        return false;
    }

    int iterate(GameState state) {
        int p;

        // Tie game
        ArrayList<GameState> children = state.getChildren();
        if (children == null) children = state.findChildren();
        if (children.size() == 0) p = 0;
        // A leaf is when at least one of the state's children is untried
        else if (checkLeaf(state)) {
            GameState randomNode = getRandomChild(state);
            p = randomFinish(randomNode);
        }
        else p = iterate(runUCT(state));

        update(state, p);
        return p;
    }

    boolean checkLeaf(GameState state) {
        ArrayList<GameState> children = state.getChildren();
        for (GameState child : children)
            if (!wins.containsKey(getHash(child))) return true;
        return false;
    }

    String getHash(GameState state) {
        int[][] board = state.getBoard();
//        int[] hashes = new int[6];
//        for (int r = 0; r < 6; r++) hashes[r] = Arrays.hashCode(board[r]);
//        return Arrays.hashCode(hashes);
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < 6; r++)
            for (int c = 0; c < 7; c++)
                sb.append(board[r][c]);
        return sb.toString();
    }

    GameState getRandomChild(GameState state) {
        ArrayList<GameState> children = state.getChildren();
        int index = children.size() == 1? 0:
                (new Random()).nextInt(children.size());
        return children.get(index);
    }

    int randomFinish(GameState state) {
        String hash = getHash(state);
        int p;
        ArrayList<GameState> children = state.getChildren();
        if (children == null) children = state.findChildren();

        // Check if someone wins this board
        if (checkWin(state)) {
            double win = checked.get(hash);
            if (win == 100.0) p = 1;
            else p = 2;
        } else if (children.size() == 0) return 0;
        else {
            GameState randomNode = getRandomChild(state);
            p = randomFinish(randomNode);
        }

        // Update the HashTable
        update(state, p);
        return p;
    }

    void update(GameState state, int p) {
        String hash = getHash(state);
        Long[] winArray;
        if (wins.containsKey(hash)) winArray = wins.get(hash);
        else winArray = new Long[3];
        if (winArray[p] == null) {
            if (p == 0) {
                winArray[0] = 1L; winArray[1] = 0L; winArray[2] = 0L;
            } else {
                winArray[0] = 0L; winArray[p] = 1L; winArray[3 - p] = 0L;
            }
        }
        else winArray[p]++;
        wins.put(hash, winArray);
        total_runs++;
    }

    GameState runUCT(GameState state) {
        int player = state.getPlayer();
        ArrayList<GameState> children = state.getChildren();
        ArrayList<Double> uct = new ArrayList<>();
        for (GameState child : children) {
            String hashC = getHash(child);
            uct.add((double)wins.get(hashC)[player] /
                    (wins.get(hashC)[1] + wins.get(hashC)[2]) +
                    Math.sqrt(2*Math.log(total_runs) /
                            (wins.get(hashC)[1] + wins.get(hashC)[2])));
        }
        int index = uct.indexOf(Collections.max(uct));
        return children.get(index);
    }
}
