import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.io.Console;

public class TicTacToe {

    public static enum Player {
        ME('X') {
            @Override
            public Player other() {
                return Player.YOU;
            }
        },

        YOU('O') {
            @Override
            public Player other() {
                return Player.ME;
            }
        };

        private final char mark;

        private Player(char mark) {
            this.mark = mark;
        }

        public char mark() {
            return this.mark;
        }

        public abstract Player other();
    }


    public static class Action {

        int position;
        Player player;

        public Action(int index, Player player) {
            // TODO
            position = index;
            this.player = player;
        }

        public String toString() {
            return String.valueOf(this.player + "  " + this.position);
        }

    }


    public static class State implements Game.State<Action> {

        public static final int N = 3;
        public char[] board = new char[N*N];
        public Player player;

        public State(Player player) {
            // TODO
            this.player = player;
            for(int i = 0; i < 9; i++) board[i] = ' ';
        }

        public State(State state, Action move) {
            // TODO
            this.board = Arrays.copyOf(state.board, N*N);
            player = move.player.other();

            this.board[move.position] = move.player.mark;

        }

        public int emptySquares() {
            // TODO
            int num = 0;
            for(int i = 0; i < 9; i++){
                if(isEmpty(i)) num++;
            }
            return num;
        }

        public char get(int row, int col) {
            // TODO
            return board[row * 3 + col];
        }

        public boolean isEmpty(int index) {
            // TODO
            return board[index] == ' ';
        }

        public boolean wins(Player player) {
            for (int i = 0; i < 3; i++) {
                if (player.mark == board[i * 3] && player.mark == board[i * 3 + 1] && player.mark == board[i * 3 + 2]) return true;
                if (player.mark == board[i] && player.mark == board[i + 3] && player.mark == board[i + 6]) return true;
            }
            if (player.mark == board[0] && player.mark == board[4] && player.mark == board[8]) return true;
            if (player.mark == board[2] && player.mark == board[4] && player.mark == board[6]) return true;
            return false;
        }


        @Override
        public boolean isTerminal() {
            // TODO
            for(int i = 0; i < 3; i++){
                if(board[i] != ' ' && board[i] == board[i+3] && board[i] == board[i+6])return true;
                if(board[i*3] != ' ' && board[i*3] == board[i*3+1] && board[i*3+1] == board[i*3+2])return true;
            }
            if(board[0] != ' ' && board[0] == board[4] && board[0] == board[8]) return true;
            if(board[0] != ' ' && board[2] == board[4] && board[0] == board[6]) return true;
            return emptySquares() == 0;
        }

        public int utility(){

            int utilityValue = 0;

            if (wins(Player.ME)) {
                utilityValue = 100; //computer win
            } else if (wins(Player.YOU)) {
                utilityValue = -100; //I win
            } else {

                //2 unblocked
                utilityValue += (number2Unblocked(Player.ME) - number2Unblocked(Player.YOU)) * 4;
                //1 unblocked
                utilityValue += (number1Unblocked(Player.ME) - number1Unblocked(Player.YOU)) * 2;
                //corners
                utilityValue += (countCornerTiles(Player.ME) - countCornerTiles(Player.YOU)) * 3;
            }
            return utilityValue;

        }
        
        public int number2Unblocked(Player player){
            int count = 0;
            char temp = player.mark;
            //rows
            for (int row = 0; row < 3; row++) {
                if ((board[row * 3] == temp && board[row * 3 + 1] == temp && board[row * 3 + 2] == ' ') || (board[row * 3] == temp && board[row * 3 + 1] == ' ' && board[row * 3 + 2] == temp) || (board[row * 3] == ' ' && board[row * 3 + 1] == temp && board[row * 3 + 2] == temp)) {
                    count++;
                }
            }

            //columns
            for (int col = 0; col < 3; col++) {
                if ((board[col] == temp && board[col + 3] == temp && board[col + 6] == ' ') || (board[col] == temp && board[col + 3] == ' ' && board[col + 6] == temp) || (board[col] == ' ' && board[col + 3] == temp && board[col + 6] == temp)) {
                    count++;
                }
            }

            //diagonals
            if ((board[0] == temp && board[4] == temp && board[8] == ' ') || (board[0] == temp && board[4] == ' ' && board[8] == temp) || (board[0] == ' ' && board[4] == temp && board[8] == temp)) {
                count++;
            }
            if ((board[2] == temp && board[4] == temp && board[6] == ' ') || (board[2] == temp && board[4] == ' ' && board[6] == temp) || (board[2] == ' ' && board[4] == temp && board[6] == temp)) {
                count++;
            }

            return count;
        }

        public int number1Unblocked(Player player){
            int count = 0;
            char temp = player.mark;
            //rows
            for (int row = 0; row < 3; row++) {
                if ((board[row * 3] == temp && board[row * 3 + 1] == ' ' && board[row * 3 + 2] == ' ') || (board[row * 3] == ' ' && board[row * 3 + 1] == temp && board[row * 3 + 2] == ' ') || (board[row * 3] == ' ' && board[row * 3 + 1] == ' ' && board[row * 3 + 2] == temp)) {
                    count++;
                }
            }

            //columns
            for (int col = 0; col < 3; col++) {
                if ((board[col] == temp && board[col + 3] == ' ' && board[col + 6] == ' ') || (board[col] == ' ' && board[col + 3] == temp && board[col + 6] == ' ') || (board[col] == ' ' && board[col + 3] == ' ' && board[col + 6] == temp)) {
                    count++;
                }
            }
            //diagonals
            if ((board[0] == temp && board[4] == ' ' && board[8] == ' ') || (board[0] == ' ' && board[4] == temp && board[8] == ' ') || (board[0] == ' ' && board[4] == ' ' && board[8] == temp)) {
                count++;
            }
            if ((board[2] == temp && board[4] == ' ' && board[6] == ' ') || (board[2] == ' ' && board[4] == temp && board[6] == ' ') || (board[2] == ' ' && board[4] == ' ' && board[6] == temp)) {
                count++;
            }

            return count;
        }

        public int countCornerTiles(Player player){
            int count = 0;
            char temp = player.mark;
            if(board[0] == temp) count++;
            if(board[2] == temp) count++;
            if(board[6] == temp) count++;
            if(board[8] == temp) count++;
            return count;
        }


        @Override
        public int evaluate() {
            // TODO
            if(wins(Player.ME)) {
                return 1;
            }
            else if(wins(Player.YOU)) {
                return -1;
            }
            return 0;
        }

        @Override
        public State next(Action action) {
            return new State(this, action);
        }

        @Override
        public Iterable<Action> moves() {
            return new Iterable<Action> () {
                @Override
                public Iterator<Action> iterator() {
                    return State.this.new MoveIterator();
                }
            };
        }

        private class MoveIterator implements Iterator<Action> {
            public ArrayList<Integer> empty = new ArrayList<>();
            public int current = 0;

            public MoveIterator(){
                for(int i = 0; i < 9; i++){
                    if(isEmpty(i)) empty.add(i);
                }
            }

            @Override
            public boolean hasNext() {
                // TODO
                return current < empty.size();
            }

            @Override
            public Action next() {
                // TODO
                int move = empty.get(current);
                current++;
                return new Action(move, player);
            }
        }

        public String print(boolean order) {
            String result = "";
            String separator = " ";
            for (int row = 0; row < N; row++) {
                if (row > 0) {
                    result += " \n";
                    result += "---+---+---\n";
                    separator = " ";
                }
                for (int col = 0; col < N; col++) {
                    result += separator;
                    if(order){
                        result += board[row*3 + col];
                    }
                    else{
                        char check = board[row*3 + col];
                        if(check == 'X') result += 'O';
                        else if(check == 'O')result += 'X';
                        else result += ' ';
                    }
                    separator = " | ";
                }
            }
            result += " \n";
            return result;
        }

    }

    public static void main(String[] args) {
        TicTacToe.State state = new TicTacToe.State(Player.YOU);
        Minimax<Action> minimax = new Minimax<>();
        Console console = System.console();

        int maxDepth = 5;
        boolean computer = false;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-plies") && i + 1 < args.length) {
                try {
                    maxDepth = Integer.parseInt(args[i + 1]);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid max depth value. Using the default value.");
                }
            }
            if(args[i].equals("-first")){
                state = new TicTacToe.State(Player.ME);
                computer = true;
            }
        }


        if(computer){
            while (state.emptySquares() > 0) {

                if (state.wins(Player.YOU)) {
                    System.out.println("You won !!!");
                    break;
                } else if (state.emptySquares() == 0) {
                    System.out.println("Tie !!!");
                    break;
                }

                state = state.next(minimax.bestMove(state, maxDepth));
                System.out.println(state.print(computer));

                if (state.wins(Player.ME)) {
                    System.out.println("I won !!!");
                    break;
                } else if (state.emptySquares() == 0) {
                    System.out.println("Tie !!!");
                    break;
                }

                int row = 0;
                int col = 0;
                String line = "";
                do {
                    do {
                        line = console.readLine("Row: ");
                        try {
                            row = Integer.parseInt(line) - 1;
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid row: " + line);
                        }
                        if (row >= 0 && row < 3) break;
                        System.out.println("Invalid row: " + line);
                    } while (true);

                    do {
                        line = console.readLine("Col: ");
                        try {
                            col = Integer.parseInt(line) - 1;
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid column: " + line);
                        }
                        if (col >= 0 && col < 3) break;
                        System.out.println("Invalid col: " + line);
                    } while (true);

                    if (state.isEmpty(row*3 + col)) break;
                    System.out.println("Square is not empty");
                } while (true);
                state = new State(state, new Action(row*3 + col, Player.YOU));
            }
        }
        else{
            while (state.emptySquares() > 0) {

                if (state.wins(Player.ME)) {
                    System.out.println("I won !!!");
                    break;
                } else if (state.emptySquares() == 0) {
                    System.out.println("Tie !!!");
                    break;
                }

                int row = 0;
                int col = 0;
                String line = "";
                do {
                    do {
                        line = console.readLine("Row: ");
                        try {
                            row = Integer.parseInt(line) - 1;
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid row: " + line);
                        }
                        if (row >= 0 && row < 3) break;
                        System.out.println("Invalid row: " + line);
                    } while (true);

                    do {
                        line = console.readLine("Col: ");
                        try {
                            col = Integer.parseInt(line) - 1;
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid column: " + line);
                        }
                        if (col >= 0 && col < 3) break;
                        System.out.println("Invalid col: " + line);
                    } while (true);

                    if (state.isEmpty(row*3 + col)) break;
                    System.out.println("Square is not empty");
                } while (true);
                state = new State(state, new Action(row*3 + col, Player.YOU));

                if (state.wins(Player.YOU)) {
                    System.out.println("You won !!!");
                    break;
                } else if (state.emptySquares() == 0) {
                    System.out.println("Tie !!!");
                    break;
                }

                state = state.next(minimax.bestMove(state, maxDepth));
                System.out.println(state.print(computer));
            }
        }
    }
}

