import java.util.ArrayList;
import java.util.Iterator;

/**
 * Represents the 4×4×4 game board using a bitboard encoding.
 *
 * Two 64-bit longs (x and o) serve as boolean vectors where bit i is set if
 * player X or O occupies position i. This allows fast win detection and move
 * generation using bitwise AND against precomputed line masks.
 *
 * Positions are encoded by Coordinate: position = z*N² + y*N + x, where N = 4.
 */
public class Board {

    public static final int N = 4;

    private long x;  // bitmask: bit i is set if X occupies position i
    private long o;  // bitmask: bit i is set if O occupies position i


    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    public Board() {
        this.x = 0;
        this.o = 0;
    }

    /** Copy constructor. */
    public Board(Board board) {
        this.x = board.x;
        this.o = board.o;
    }

    /**
     * Constructs a board from a string representation.
     * 'X'/'x' = X, 'O'/'o' = O, '.' = empty. Spaces and '|' are ignored.
     */
    public Board(String s) {
        int position = 0;
        this.x = 0;
        this.o = 0;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case 'x':
                case 'X':
                    this.set(position++, Player.X);
                    break;

                case 'o':
                case 'O':
                    this.set(position++, Player.O);
                    break;

                case '.':
                    position++;
                    break;

                case ' ':
                case '|':
                    break;

                default:
                    throw new IllegalArgumentException("Invalid player: " + c);
            }
        }
    }


    // -----------------------------------------------------------------------
    // Evaluation / Heuristic
    // -----------------------------------------------------------------------

    /**
     * Scores the board from X's perspective using a heuristic over all 76 winning lines.
     *
     * Returns Integer.MAX_VALUE if X has won, Integer.MIN_VALUE+1 if O has won.
     * Otherwise, scores based on unblocked threats:
     *   - 3-in-a-row unblocked: weight 100
     *   - 2-in-a-row unblocked: weight 10
     *
     * The player whose turn it is gets their threat counts doubled to favor offense.
     *
     * @param playing the player about to move (used for the offensive weighting)
     */
    public int evaluation(Player playing) {
        int value = 0;

        int xthree = 0;
        int othree = 0;
        int xtwo   = 0;
        int otwo   = 0;

        for (Line line : Line.lines) {
            long xMask = x & line.positions();
            long oMask = o & line.positions();

            // A line is only useful if it's unblocked (only one player has pieces on it)
            if (xMask != 0 && oMask == 0) {
                int xCount = Bit.countOnes(xMask);
                if (xCount == 4) return Integer.MAX_VALUE;       // X wins
                if (xCount == 3) xthree++;
                else if (xCount == 2) xtwo++;
            } else if (oMask != 0 && xMask == 0) {
                int oCount = Bit.countOnes(oMask);
                if (oCount == 4) return Integer.MIN_VALUE + 1;   // O wins
                if (oCount == 3) othree++;
                if (oCount == 2) otwo++;
            }
        }

        // Double the active player's threat counts to bias toward offensive play
        if (playing == Player.X) {
            xthree *= 2;
            xtwo   *= 2;
        } else {
            othree *= 2;
            otwo   *= 2;
        }

        value = (xthree - othree) * 100 + (xtwo - otwo) * 10;
        return value;
    }

    public int simpleEvaluation(Player playing) {
        return 0;
    }


    // -----------------------------------------------------------------------
    // Empty square queries
    // -----------------------------------------------------------------------

    public boolean isEmpty(int position) {
        assert Coordinate.isValid(position);
        return !Bit.isSet(this.x | this.o, position);
    }

    public boolean isEmpty(Coordinate coordinate) {
        return this.isEmpty(coordinate.position());
    }

    public boolean isEmpty(int x, int y, int z) {
        return this.isEmpty(Coordinate.position(x, y, z));
    }

    /** Returns the count of unoccupied cells using a popcount on the complement of occupied bits. */
    public int numberEmptySquares() {
        return Bit.countOnes(~(this.x | this.o));
    }


    // -----------------------------------------------------------------------
    // Get the occupant of a cell
    // -----------------------------------------------------------------------

    /**
     * Returns the bitmask of all cells belonging to the given player.
     * Player.EMPTY returns the complement of all occupied cells.
     */
    public long get(Player player) {
        switch (player) {
            case EMPTY: return ~(this.x | this.o);
            case X:     return this.x;
            case O:     return this.o;
            default:    return 0;
        }
    }

    public Player get(int position) {
        assert Coordinate.isValid(position);
        if (Bit.isSet(this.x, position)) return Player.X;
        if (Bit.isSet(this.o, position)) return Player.O;
        return Player.EMPTY;
    }

    public Player get(Coordinate coordinate) {
        return this.get(coordinate.position());
    }

    public Player get(int x, int y, int z) {
        return this.get(Coordinate.position(x, y, z));
    }


    // -----------------------------------------------------------------------
    // Set / clear a cell
    // -----------------------------------------------------------------------

    public void set(int position, Player player) {
        assert (isEmpty(position));
        switch (player) {
            case X:
                this.x = Bit.set(this.x, position);
                break;

            case O:
                this.o = Bit.set(this.o, position);
                break;

            default:
                break;
        }
    }

    public void set(Coordinate coordinate, Player player) {
        set(coordinate.position(), player);
    }

    public void set(int x, int y, int z, Player player) {
        set(Coordinate.position(x, y, z), player);
    }

    public void clear(int position) {
        this.x = Bit.clear(this.x, position);
        this.o = Bit.clear(this.o, position);
    }

    public void clear(Coordinate coordinate) {
        clear(coordinate.position());
    }

    public void clear(int x, int y, int z) {
        clear(Coordinate.valueOf(x, y, z));
    }


    // -----------------------------------------------------------------------
    // Equality
    // -----------------------------------------------------------------------

    public boolean equals(Board other) {
        return this.o == other.o && this.x == other.x;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Board && this.equals((Board) other);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.x) * Long.hashCode(this.o);
    }


    // -----------------------------------------------------------------------
    // Printing
    // -----------------------------------------------------------------------

    @Override
    public String toString() {
        String result = "";
        String separator = "";

        for (int position = 0; position < 64; position++) {
            result += separator;
            result += this.get(position).toString();
            if (position % 16 == 0) {
                separator = " | ";
            } else if (position % 4 == 0) {
                separator = " ";
            } else {
                separator = "";
            }
        }
        return result;
    }

    public static Board valueOf(String s) {
        return new Board(s);
    }

    /**
     * Prints the board as four side-by-side 4×4 layers (z=0 to z=3, left to right).
     * Rows are printed top-to-bottom with y decreasing (y=3 at the top).
     */
    public void print() {
        for (int y = N-1; y >= 0; y--) {
            for (int z = 0; z < N; z++) {
                for (int x = 0; x < N; x++) {
                    System.out.print(this.get(x, y, z));
                }
                System.out.print("    ");
            }
            System.out.println();
        }
    }

    /**
     * Prints the board with X and O swapped (used when the human plays as X
     * so the display always shows the human as 'X' on screen).
     */
    public void print(boolean temp) {
        for (int y = N-1; y >= 0; y--) {
            for (int z = 0; z < N; z++) {
                for (int x = 0; x < N; x++) {
                    System.out.print(this.get(x, y, z).other());
                }
                System.out.print("    ");
            }
            System.out.println();
        }
    }


    // -----------------------------------------------------------------------
    // Immutable move application
    // -----------------------------------------------------------------------

    /** Returns a new Board with the given coordinate set to player. */
    public Board next(Coordinate move, Player player) {
        assert this.isEmpty(move);
        Board result = new Board(this);
        result.set(move, player);
        return result;
    }

    public Board next(int position, Player player) {
        return next(Coordinate.valueOf(position), player);
    }

    public Board next(int x, int y, int z, Player player) {
        return next(Coordinate.valueOf(x, y, z), player);
    }


    // -----------------------------------------------------------------------
    // Empty-square iterator
    // -----------------------------------------------------------------------

    private class EmptySquareIterator implements Iterator<Coordinate> {

        private Iterator<Integer> iterator;

        public EmptySquareIterator() {
            // Iterate over the set bits of the empty-cell bitmask
            this.iterator = Bit.iterator(Board.this.get(Player.EMPTY));
        }

        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        public Coordinate next() {
            return Coordinate.valueOf(this.iterator.next());
        }
    }

    public Iterator<Coordinate> emptySquareIterator() {
        return new EmptySquareIterator();
    }

    public Iterable<Coordinate> emptySquares() {
        return new Iterable<Coordinate>() {
            @Override
            public Iterator<Coordinate> iterator() {
                return new EmptySquareIterator();
            }
        };
    }


    // -----------------------------------------------------------------------
    // Test / debug entry point
    // -----------------------------------------------------------------------

    public static void main(String[] args) {
        for (String arg : args) {
            Board board = new Board(arg);
            board.print();
            for (Coordinate coord : board.emptySquares()) {
                System.out.print(coord.position() + " ");
            }
            System.out.println();
        }
    }
}
