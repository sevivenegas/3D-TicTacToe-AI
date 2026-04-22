/**
 * Represents the two players and the empty cell state on the board.
 * X is always the AI (maximizing player); O is always the human (minimizing player).
 */
public enum Player {

    X,
    O,
    EMPTY;

    /** Returns the opposing player. EMPTY returns EMPTY. */
    public Player other() {
        switch (this) {
            case X: return O;
            case O: return X;
            default: return EMPTY;
        }
    }

    /** Parses a character into a Player. Returns null for unrecognized characters. */
    public static Player valueOf(char c) {
        switch(c) {
            case 'x':
            case 'X':
                return X;

            case 'o':
            case 'O':
                return O;

            case '.':
                return EMPTY;

            default:
                return null;
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case X: return "X";
            case O: return "O";
            default: return ".";
        }
    }
}
