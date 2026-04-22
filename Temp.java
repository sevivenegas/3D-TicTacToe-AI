import java.util.*;
import java.io.Console;

/**
 * Main entry point for the 3D (4×4×4) TicTacToe game.
 *
 * The AI plays as X (maximizer) and the human plays as O (minimizer).
 * By default the AI moves first. Pass -second to let the human go first.
 *
 * CLI flags:
 *   -plies  <n>   Set the Minimax search depth (default: 3)
 *   -second       Human goes first
 *   -start  <s>   Load a board from a string (X/O/. characters)
 */
public class Temp {

    public static void main(String[] args) {

        Console console = System.console();
        Board ticTacToeBoard = new Board();
        Minimax minimax = new Minimax();
        Player computer = Player.X;
        Player human    = Player.O;

        int maxDepth = 3;
        boolean second = false;  // if true, human moves first

        // Parse command-line flags
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-plies") && i + 1 < args.length) {
                try {
                    maxDepth = Integer.parseInt(args[i + 1]);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid max depth value. Using the default value.");
                }
            }
            if (args[i].equals("-second")) {
                second = true;
            }
            if (args[i].equals("-start") && i + 1 < args.length) {
                try {
                    ticTacToeBoard = new Board(args[i + 1]);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid start configuration.");
                }
            }
        }

        int turn = 0;

        while (ticTacToeBoard.numberEmptySquares() > 0) {

            if (!second) {
                // ---- AI turn ----
                if (ticTacToeBoard.evaluation(Player.O) == Integer.MIN_VALUE) {
                    System.out.println("You won !!!");
                    break;
                } else if (ticTacToeBoard.numberEmptySquares() == 0) {
                    System.out.println("Tie !!!");
                    break;
                }

                // Increase search depth as the board fills up (fewer branches, so we can go deeper)
                if (turn == 32) maxDepth++;
                else if (turn == 48) maxDepth++;

                try {
                    ticTacToeBoard = ticTacToeBoard.next(
                        minimax.bestMove(ticTacToeBoard, maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE),
                        computer
                    );
                } catch (Exception e) {
                    // Fallback: if bestMove throws (e.g. no moves scored), pick randomly
                    ArrayList<Coordinate> fallback = new ArrayList<>();
                    for (Coordinate coordinate : ticTacToeBoard.emptySquares()) {
                        fallback.add(coordinate);
                    }
                    ticTacToeBoard = ticTacToeBoard.next(
                        fallback.get((int)(Math.random() * fallback.size())),
                        computer
                    );
                }
                turn++;

                ticTacToeBoard.print();
                System.out.println();

                if (ticTacToeBoard.evaluation(Player.X) == Integer.MAX_VALUE) {
                    System.out.println("I won !!!");
                    break;
                } else if (ticTacToeBoard.numberEmptySquares() == 0) {
                    System.out.println("Tie !!!");
                    break;
                }

                // ---- Human turn ----
                int y = 0, x = 0, z = 0;
                String line = "";

                do {
                    do {
                        line = console.readLine("Layer (1-4): ");
                        try { z = Integer.parseInt(line) - 1; }
                        catch (NumberFormatException e) { System.out.println("Invalid layer: " + line); }
                        if (z >= 0 && z < 4) break;
                        System.out.println("Invalid layer: " + line);
                    } while (true);

                    do {
                        line = console.readLine("Column (1-4): ");
                        try { x = Integer.parseInt(line) - 1; }
                        catch (NumberFormatException e) { System.out.println("Invalid column: " + line); }
                        if (x >= 0 && x < 4) break;
                        System.out.println("Invalid column: " + line);
                    } while (true);

                    do {
                        line = console.readLine("Row (1-4): ");
                        try { y = Integer.parseInt(line) - 1; }
                        catch (NumberFormatException e) { System.out.println("Invalid row: " + line); }
                        if (y >= 0 && y < 4) break;
                        System.out.println("Invalid row: " + line);
                    } while (true);

                    if (ticTacToeBoard.isEmpty(x, y, z)) break;
                    System.out.println("Square is not empty");
                } while (true);

                ticTacToeBoard = ticTacToeBoard.next(x, y, z, human);
                turn++;

                ticTacToeBoard.print();
                System.out.println();

            } else {
                // ---- Human goes first mode (-second flag) ----

                if (ticTacToeBoard.evaluation(Player.X) == Integer.MAX_VALUE) {
                    System.out.println("I won !!!");
                    break;
                } else if (ticTacToeBoard.numberEmptySquares() == 0) {
                    System.out.println("Tie !!!");
                    break;
                }

                // ---- Human turn ----
                int y = 0, x = 0, z = 0;
                String line = "";

                do {
                    do {
                        line = console.readLine("Layer (1-4): ");
                        try { z = Integer.parseInt(line) - 1; }
                        catch (NumberFormatException e) { System.out.println("Invalid layer: " + line); }
                        if (z >= 0 && z < 4) break;
                        System.out.println("Invalid layer: " + line);
                    } while (true);

                    do {
                        line = console.readLine("Column (1-4): ");
                        try { x = Integer.parseInt(line) - 1; }
                        catch (NumberFormatException e) { System.out.println("Invalid column: " + line); }
                        if (x >= 0 && x < 4) break;
                        System.out.println("Invalid column: " + line);
                    } while (true);

                    do {
                        line = console.readLine("Row (1-4): ");
                        try { y = Integer.parseInt(line) - 1; }
                        catch (NumberFormatException e) { System.out.println("Invalid row: " + line); }
                        if (y >= 0 && y < 4) break;
                        System.out.println("Invalid row: " + line);
                    } while (true);

                    if (ticTacToeBoard.isEmpty(x, y, z)) break;
                    System.out.println("Square is not empty");
                } while (true);

                ticTacToeBoard = ticTacToeBoard.next(x, y, z, human);
                turn++;
                ticTacToeBoard.print(second);
                System.out.println();

                if (ticTacToeBoard.evaluation(Player.O) == Integer.MIN_VALUE) {
                    System.out.println("You won !!!");
                    break;
                } else if (ticTacToeBoard.numberEmptySquares() == 0) {
                    System.out.println("Tie !!!");
                    break;
                }

                // ---- AI turn ----
                if (turn == 32) maxDepth++;
                else if (turn == 48) maxDepth++;

                try {
                    ticTacToeBoard = ticTacToeBoard.next(
                        minimax.bestMove(ticTacToeBoard, maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE),
                        computer
                    );
                } catch (Exception e) {
                    // Fallback: if bestMove throws, pick a random legal move
                    ArrayList<Coordinate> fallback = new ArrayList<>();
                    for (Coordinate coordinate : ticTacToeBoard.emptySquares()) {
                        fallback.add(coordinate);
                    }
                    ticTacToeBoard = ticTacToeBoard.next(
                        fallback.get((int)(Math.random() * fallback.size())),
                        computer
                    );
                }
                turn++;
                ticTacToeBoard.print(second);
                System.out.println();
            }
        }
    }
}
