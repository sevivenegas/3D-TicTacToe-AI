/**
 * Represents one of the 18 two-dimensional cross-sectional planes of the 4×4×4 board.
 *
 * The 18 planes break down as:
 *   - 12 axis-aligned flat planes (4 per axis: XY, XZ, YZ at each integer value 0–3)
 *   - 6 diagonal planes (forward and reverse diagonal along each of the 3 axes)
 *
 * Planes are used for geometric queries such as detecting whether a Line is fully
 * contained in a plane, or finding intersection points between planes and lines.
 *
 * Like Line, each plane stores its cell membership as a bitmask (long).
 */
public class Plane {

    private long positions;  // bitmask of all cells belonging to this plane
    private String name;

    private Plane(String name) {
        this.positions = 0;
        this.name = name;
    }

    private Plane(long positions) {
        this.positions = positions;
        this.name = "";
    }

    public long positions() {
        return this.positions;
    }

    public String name() {
        return this.name;
    }

    public boolean contains(Coordinate coordinate) {
        return Bit.isSet(this.positions, coordinate.position());
    }

    /** Returns true if this plane contains all 4 cells of the given line. */
    public boolean contains(Line line) {
        return Bit.countOnes(this.positions & line.positions()) == N;
    }

    public boolean intersects(Plane plane) {
        return (this.positions & plane.positions) != 0;
    }

    public boolean intersects(Line line) {
        return (this.positions & line.positions()) != 0;
    }

    /**
     * Returns the Line formed by the intersection of two distinct, intersecting planes.
     * Returns null if the planes are identical or don't intersect.
     */
    public Line intersection(Plane plane) {
        if (this.intersects(plane) && !this.equals(plane)) {
            return Line.find(this.positions & plane.positions);
        } else {
            return null;
        }
    }

    /**
     * Returns the single Coordinate where a line crosses this plane (if not fully contained).
     * Returns null if the line is fully inside the plane or doesn't intersect.
     */
    public Coordinate intersection(Line line) {
        if (this.intersects(line) && !this.contains(line)) {
            return Coordinate.forMask(line.positions() & this.positions);
        } else {
            return null;
        }
    }

    public boolean equals(Plane other) {
        return this.positions == other.positions;
    }

    @Override
    public boolean equals(Object other) {
        if (other != null && other instanceof Plane) {
            return this.equals((Plane) other);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        String result = "{";
        String separator = "";
        for (int position = 0; position < Coordinate.NCubed; position++) {
            Coordinate coordinate = Coordinate.valueOf(position);
            if (this.contains(coordinate)) {
                result += separator;
                result += coordinate;
                separator = ", ";
            }
        }
        result += "}";
        return result;
    }


    // -----------------------------------------------------------------------
    // Private factory methods for constructing each class of plane
    // -----------------------------------------------------------------------

    private static enum Axis { X, Y, Z }
    private static final int N = Coordinate.N;

    private void set(int x, int y, int z) {
        this.positions = Bit.set(this.positions, Coordinate.position(x, y, z));
    }

    /**
     * Creates a flat axis-aligned plane: all cells where the given axis equals value.
     * E.g. Axis.Z, value=2 produces the 4×4 layer at z=2 (the XY-plane at Z=2).
     */
    private static Plane Straight(Axis axis, int value) {
        String name = "";
        switch (axis) {
            case X: name = "YZ-Plane, X = " + value; break;
            case Y: name = "XZ-Plane, Y = " + value; break;
            case Z: name = "XY-Plane, Z = " + value; break;
        }

        Plane plane = new Plane(name);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                switch (axis) {
                    case X: plane.set(value, i, j); break;
                    case Y: plane.set(i, value, j); break;
                    case Z: plane.set(i, j, value); break;
                }
            }
        }
        return plane;
    }

    /**
     * Creates a forward diagonal plane: cells where the two non-fixed axes are equal
     * (e.g. Axis.Z → cells where x == y, for all z).
     */
    private static Plane ForwardDiagonal(Axis axis) {
        String name = "";
        switch (axis) {
            case X: name = "YZ-Forward Diagonal"; break;
            case Y: name = "XZ-Forward Diagonal"; break;
            case Z: name = "XY-Forward Diagonal"; break;
        }

        Plane plane = new Plane(name);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                switch (axis) {
                    case X: plane.set(i, j, j); break;
                    case Y: plane.set(j, i, j); break;
                    case Z: plane.set(j, j, i); break;
                }
            }
        }
        return plane;
    }

    /**
     * Creates a reverse diagonal plane: cells where one non-fixed axis equals N-1 minus the other
     * (e.g. Axis.Z → cells where x + y == N-1, for all z).
     */
    private static Plane ReverseDiagonal(Axis axis) {
        String name = "";
        switch (axis) {
            case X: name = "YZ-Reverse Diagonal"; break;
            case Y: name = "XZ-Reverse Diagonal"; break;
            case Z: name = "XY-Reverse Diagonal"; break;
        }

        Plane plane = new Plane(name);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                switch (axis) {
                    case X: plane.set(i, j, N-1-j); break;
                    case Y: plane.set(j, i, N-1-j); break;
                    case Z: plane.set(j, N-1-j, i); break;
                }
            }
        }
        return plane;
    }


    // -----------------------------------------------------------------------
    // Precomputed array of all 18 planes, built at class-load time
    // -----------------------------------------------------------------------

    public static final Plane[] planes = new Plane[18];
    static {
        int count = 0;
        for (Axis axis : Axis.values()) {
            // 4 flat axis-aligned planes per axis = 12 total
            for (int value = 0; value < N; value++) {
                planes[count++] = Straight(axis, value);
            }
            // 1 forward + 1 reverse diagonal plane per axis = 6 total
            planes[count++] = ForwardDiagonal(axis);
            planes[count++] = ReverseDiagonal(axis);
        }
        assert count == 18;
    }
}
