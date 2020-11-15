import java.util.*;

public class MCTS {
    private static long total_runs = 0;
    private final static HashMap<Integer, Long[]> wins = new HashMap<>();
    private final static HashMap<Integer, Double> checked = new HashMap<>();

    public int chooseMove(GameState state) {
        // Check if any moves win the game (record all states)
        ArrayList<GameState> children = state.getChildren();
        if (children == null) children = state.findChildren();
        for (GameState child : children)
            if (checkWin(child)) return child.getMoveIn();

        // If not, run the Monte Carlo search
        for (int i =0; i < 1e4; i++) iterate(state);

        GameState best = null;
        long max_denom = 0;
        int hash = getHash(state);
        System.out.println(wins.get(hash)[0]+", "+wins.get(hash)[1]+", "+wins.get(hash)[2]);
        for (GameState child : children) {
            int hashC = getHash(child);
            System.out.println(wins.get(hashC)[0]+", "+wins.get(hashC)[1]+", "+wins.get(hashC)[2]+" | ");
            long denom = wins.get(hashC)[1]+wins.get(hashC)[2];
            if (denom > max_denom) {
                max_denom = denom; best = child;
            }
        } System.out.println();

        return best.getMoveIn();
    }

    boolean checkWin(GameState state) {
        int hash = getHash(state);
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
        int hash = getHash(state), p;

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

    int getHash(GameState state) {
        int[][] board = state.getBoard();
        int[] hashes = new int[6];
        for (int r = 0; r < 6; r++) hashes[r] = Arrays.hashCode(board[r]);
        return Arrays.hashCode(hashes);
    }

    GameState getRandomChild(GameState state) {
        ArrayList<GameState> children = state.getChildren();
        int index = children.size() == 1? 0:
                (new Random()).nextInt(children.size());
        return children.get(index);
    }

    int randomFinish(GameState state) {
        int hash = getHash(state), p;
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
        int hash = getHash(state);
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
            int hashC = getHash(child);
            uct.add((double)wins.get(hashC)[player] /
                    (wins.get(hashC)[1] + wins.get(hashC)[2]) +
                    Math.sqrt(2*Math.log(total_runs) /
                            (wins.get(hashC)[1] + wins.get(hashC)[2])));
        }
        int index = uct.indexOf(Collections.max(uct));
        return children.get(index);
    }
}
