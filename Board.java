import java.util.ArrayList;
import java.util.Iterator;

public class Board {

    public static final int N = 4;

    private long x; // Boolean vector of positions containing X's
    private long o; // Boolean vector of positions containing O's


    // Constructors.

    public Board() {
        this.x = 0;
        this.o = 0;
    }

    public Board(Board board) {
        this.x = board.x;
        this.o = board.o;
    }

    public Board(String s) {
        int position = 0;
        this.x = 0;
        this.o = 0;

        for (int i= 0; i < s.length(); i++) {
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

    // Evaluation Function

    public int evaluation(Player playing){
        int value = 0;

        int xthree = 0;
        int othree = 0;

        int xtwo = 0;
        int otwo = 0;

        for(Line line : Line.lines){

            Long xMask = x & line.positions();
            Long oMask = o & line.positions();

            if(xMask != 0 && oMask == 0){
                int xCount = Bit.countOnes(xMask);
                if(xCount == 4) return Integer.MAX_VALUE;
                if(xCount == 3) xthree++;
                else if(xCount == 2) xtwo++;
            }
            else if(oMask != 0 && xMask == 0){
                int oCount = Bit.countOnes(oMask);
                if(oCount == 4) return Integer.MIN_VALUE+1;
                if(oCount == 3) othree++;
                if(oCount == 2) otwo++;
            }

        }



        if(playing == Player.X){
            xthree *= 2;
            xtwo *= 2;
        }
        else{
            othree *= 2;
            otwo *= 2;
        }
        value = (xthree - othree) * 100 + (xtwo - otwo) * 10;
        return value;
    }

    public int simpleEvaluation(Player playing){
        return 0;
    }

    // Empty squares.

    public boolean isEmpty(int position) {
        assert Coordinate.isValid(position);
        return ! Bit.isSet(this.x | this.o, position);
    }

    public boolean isEmpty(Coordinate coordinate) {
        return this.isEmpty(coordinate.position());
    }

    public boolean isEmpty(int x, int y, int z) {
        return this.isEmpty(Coordinate.position(x, y, z));
    }

    public int numberEmptySquares() {
        return Bit.countOnes(~(this.x | this.o));
    }


    // Get value of a square on the board.

    public long get(Player player) {
        switch(player) {
            case EMPTY: return ~(this.x | this.o);
            case X: return this.x;
            case O: return this.o;
            default: return 0;
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


    // Set value of a square on the board.

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


    // Equality.

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


    // Image & printing functions.

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


    // Generate new board for a given move.

    public Board next(Coordinate move, Player player) {
        assert this.isEmpty(move);
        Board result = new Board(this);
        result.set(move, player);
        return result;
    }

    public Board next(int position, Player player) {
        return next(Coordinate.valueOf(position), player);
        // return next(new Coordinate(position), player);
    }

    public Board next(int x, int y, int z, Player player) {
        Coordinate val = Coordinate.valueOf(x, y, z);
        return next (val, player);
        // return next (new Coordinate(x, y, z), player);
    }


    // Iterators.

    private class EmptySquareIterator implements Iterator<Coordinate> {

        private Iterator<Integer> iterator;

        public EmptySquareIterator() {
            this.iterator = Bit.iterator(Board.this.get(Player.EMPTY));
        }

        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        public Coordinate next() {
            return Coordinate.valueOf(this.iterator.next());
            // return new Coordinate(this.iterator.next());
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

//    @Override
//    public Iterable<Action> moves() {
//        return new Iterable<Action> () {
//            @Override
//            public Iterator<Action> iterator() {
//                return State.this.new MoveIterator();
//            }
//        };
//    }
//
//    private class MoveIterator implements Iterator<Action> {
//        public ArrayList<Integer> empty = new ArrayList<>(); //prob could make this more efficient revisit later
//        public int current = 0;
//
//        public MoveIterator(){
//            for(int i = 0; i < 9; i++){
//                if(isEmpty(i)) empty.add(i);
//            }
//        }
//
//        @Override
//        public boolean hasNext() {
//            // TODO
//            return current < empty.size();
//        }
//
//        @Override
//        public Action next() {
//            // TODO
//            int move = empty.get(current);
//            current++;
//            return new Action(move, player);
//        }
//    }

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
