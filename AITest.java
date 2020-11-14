import static org.junit.Assert.*;

import org.junit.Test;

import com.atomicobject.connectfour.AI;
import com.atomicobject.connectfour.GameState;


public class AITest {

	@Test
	public void test() {
		// This is just an example test to show JUnit working. It won't be useful
		// for your real implementation.
		AI ai = new AI();
		GameState state = new GameState();
		state.setPlayer(1);
		state.setBoard(new int[][]{{0, 0, 0, 0, 0, 0, 0},
				                   {0, 0, 0, 0, 0, 0, 0},
				                   {0, 0, 0, 0, 0, 0, 0},
				                   {0, 0, 0, 0, 0, 0, 0},
				                   {0, 0, 0, 0, 0, 0, 0},
				                   {0, 0, 0, 0, 0, 0, 0}});

		// Our first canned move is 1
		assertEquals(1, ai.computeMove(state));

		// Our second canned move is still 1
		assertEquals(1, ai.computeMove(state));
	}
}
