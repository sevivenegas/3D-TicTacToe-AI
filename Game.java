/**
 * Generic interface for a two-player zero-sum game state.
 *
 * Parameterized by Action so that different game implementations
 * (e.g. 2D or 3D TicTacToe) can reuse the same Minimax logic.
 */
public class Game {

    public static interface State<Action> {
        /** Heuristic score for the current state (positive = good for maximizer). */
        public int evaluate();

        /** Returns true if the game is over (win or draw). */
        public boolean isTerminal();

        /** Returns the state that results from applying the given action. */
        public State next(Action action);

        /** Returns all legal actions available from this state. */
        public Iterable<Action> moves();

        /** Terminal utility value: win, loss, or draw. */
        int utility();
    }
}
