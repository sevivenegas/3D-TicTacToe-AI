/**
 * Represents one of the 76 possible winning lines on a 4×4×4 board.
 *
 * A winning line is any set of 4 collinear cells. The 76 lines break down as:
 *   - 48 straight lines: rows (Y/Z fixed), columns (X/Z fixed), pillars (X/Y fixed)
 *   - 24 face diagonals: forward and reverse diagonals within each axis-aligned layer
 *   - 4 space diagonals: the 4 main diagonals passing through the center of the cube
 *
 * Each line stores its 4 positions as a bitmask (long) for fast bitwise intersection
 * with the board's bitboard representation.
 */
public class Line {

    private long positions;  // bitmask of the 4 cells that form this line
    private String name;

    private Line(String name) {
        this.positions = 0;
        this.name = name;
    }

    private Line(long positions) {
        this.positions = positions;
        this.name = "";
    }

    /** Returns the bitmask of the 4 positions making up this line. */
    public long positions() {
        return this.positions;
    }

    public String name() {
        return this.name;
    }

    public boolean contains(Coordinate coordinate) {
        return Bit.isSet(this.positions, coordinate.position());
    }

    public boolean intersects(Line other) {
        return (this.positions & other.positions) != 0;
    }

    /** Returns the single coordinate where two intersecting lines cross, or null if they don't. */
    public Coordinate intersection(Line other) {
        if (this.intersects(other)) {
            return Coordinate.forMask(this.positions & other.positions);
        } else {
            return null;
        }
    }

    public boolean equals(Line other) {
        return this.positions == other.positions;
    }

    @Override
    public boolean equals(Object other) {
        if (other != null && other instanceof Line) {
            return this.equals((Line) other);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        String result = "{";
        String separator = "";
        for (int position : Bit.ones(this.positions)) {
            result += separator;
            result += position;
            separator = ", ";
        }
        result += "}";
        return result;
    }


    // -----------------------------------------------------------------------
    // Private factory methods for constructing each class of winning line
    // -----------------------------------------------------------------------

    private static enum Axis { X, Y, Z }
    private static final int N = Coordinate.N;

    private void set(int x, int y, int z) {
        this.positions = Bit.set(this.positions, Coordinate.position(x, y, z));
    }

    /**
     * Creates a straight line along the given axis at the fixed (row, column) cross-section.
     * E.g. Axis.X with row=1, column=2 is the row where Y=1, Z=2.
     */
    private static Line Straight(Axis axis, int row, int column) {
        String name = "Straight line: ";
        switch (axis) {
            case X: name += "Row: Y = " + row + " Z = " + column; break;
            case Y: name += "Column: X = " + row + " Z = " + column; break;
            case Z: name += "Pillar: X = " + row + " Y = " + column; break;
            default: name += "???: row = " + row + " col = " + column; break;
        }

        Line line = new Line(name);
        for (int i = 0; i < N; i++) {
            switch (axis) {
                case X: line.set(i, row, column); break;
                case Y: line.set(row, i, column); break;
                case Z: line.set(row, column, i); break;
            }
        }
        return line;
    }

    /**
     * Creates a forward face diagonal (i increases along both varying axes)
     * within the plane where the given axis is fixed to value.
     */
    private static Line ForwardDiagonal(Axis axis, int value) {
        String name = "Forward diagonal: ";
        switch (axis) {
            case X: name += "YZ-Plane X = " + value; break;
            case Y: name += "XZ-Plane Y = " + value; break;
            case Z: name += "XY-Plane Z = " + value; break;
            default: name += "??-Plane (" + value + ")"; break;
        }

        Line line = new Line(name);
        for (int i = 0; i < N; i++) {
            switch (axis) {
                case X: line.set(value, i, i); break;
                case Y: line.set(i, value, i); break;
                case Z: line.set(i, i, value); break;
            }
        }
        return line;
    }

    /** The single main space diagonal where x == y == z. */
    private static Line ForwardDiagonal() {
        String name = "Main diagonal";
        Line line = new Line(name);
        for (int i = 0; i < N; i++) {
            line.set(i, i, i);
        }
        return line;
    }

    /**
     * Creates a reverse face diagonal within the plane where the given axis is fixed to value.
     * One axis increases while the other decreases.
     */
    private static Line ReverseDiagonal(Axis axis, int value) {
        String name = "Reverse diagonal: ";
        switch (axis) {
            case X: name += "YZ-Plane X = " + value; break;
            case Y: name += "XZ-Plane Y = " + value; break;
            case Z: name += "XY-Plane Z = " + value; break;
            default: name += "??-Plane (" + value + ")"; break;
        }

        Line line = new Line(name);
        for (int i = 0; i < N; i++) {
            switch (axis) {
                case X: line.set(value, i, N-i-1); break;
                case Y: line.set(i, value, N-i-1); break;
                case Z: line.set(i, N-i-1, value); break;
            }
        }
        return line;
    }

    /** One of the 3 reverse space diagonals — flips one axis relative to the main diagonal. */
    private static Line ReverseDiagonal(Axis axis) {
        String name = "Main diagonal: reverse";
        switch (axis) {
            case X: name += "X-Axis"; break;
            case Y: name += "Y-Axis"; break;
            case Z: name += "Z-Axis"; break;
        }

        Line line = new Line(name);
        for (int i = 0; i < N; i++) {
            switch (axis) {
                case X: line.set(N-i-1, i, i); break;
                case Y: line.set(i, N-i-1, i); break;
                case Z: line.set(i, i, N-i-1); break;
            }
        }
        return line;
    }


    // -----------------------------------------------------------------------
    // Precomputed array of all 76 winning lines, built at class-load time
    // -----------------------------------------------------------------------

    public static final Line[] lines = new Line[76];
    static {
        int count = 0;

        for (Axis axis : Axis.values()) {
            // 16 straight lines per axis (4 rows × 4 columns) = 48 total
            for (int row = 0; row < N; row++) {
                for (int column = 0; column < N; column++) {
                    lines[count++] = Straight(axis, row, column);
                }
            }
            // 4 forward + 4 reverse face diagonals per axis = 24 total
            for (int value = 0; value < N; value++) {
                lines[count++] = ForwardDiagonal(axis, value);
                lines[count++] = ReverseDiagonal(axis, value);
            }
            // 1 reverse space diagonal per axis = 3 total
            lines[count++] = ReverseDiagonal(axis);
        }

        // 1 main forward space diagonal (x == y == z)
        lines[count++] = ForwardDiagonal();
        assert count == 76;
    }

    /** Finds the Line whose bitmask exactly matches positions, or null if none found. */
    public static Line find(long positions) {
        for (Line line : lines) {
            if (line.positions() == positions) return line;
        }
        return null;
    }
}
