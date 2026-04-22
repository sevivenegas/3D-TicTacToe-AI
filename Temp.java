import java.util.*;
import java.io.Console;

public class Temp {

    public static void main(String[] args) {

        Console console = System.console();
        Board ticTacToeBoard = new Board();
        Minimax minimax = new Minimax();
        Player computer = Player.X;
        Player human = Player.O;

        String start;
        boolean eval = false;
        boolean second = false;

        int maxDepth = 3;


        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-plies") && i + 1 < args.length) {
                try {
                    maxDepth = Integer.parseInt(args[i + 1]);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid max depth value. Using the default value.");
                }
            }
            if(args[i].equals("-second")){
                second = true;

            }
            if(args[i].equals("-eval")){
                eval = true;
            }
            if (args[i].equals("-start") && i + 1 < args.length) {
                try {
                    start = args[i + 1];
                    ticTacToeBoard = new Board(start);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid start configuration.");
                }
            }

        }

        //add -eval
        int turn = 0;
            while (ticTacToeBoard.numberEmptySquares() > 0) {
                if(!second){
                    if (ticTacToeBoard.evaluation(Player.O) == Integer.MIN_VALUE) {
                        System.out.println("You won !!!");
                        break;
                    } else if (ticTacToeBoard.numberEmptySquares() == 0) {
                        System.out.println("Tie !!!");
                        break;
                    }

                    if(turn == 32) maxDepth++;
                    else if(turn == 48) maxDepth++;

                    try{
                        ticTacToeBoard = ticTacToeBoard.next(minimax.bestMove(ticTacToeBoard, maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE), computer);
                    }
                    catch(Exception e){
                        ArrayList<Coordinate> lastresort = new ArrayList<>();
                        for(Coordinate coordinate: ticTacToeBoard.emptySquares()){
                            lastresort.add(coordinate);
                        }

                        ticTacToeBoard = ticTacToeBoard.next(lastresort.get((int)(Math.random()*lastresort.size())), computer);
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

                    int y = 0; //row
                    int x = 0; //col
                    int z = 0; //layer
                    String line = "";
                    do {
                        do {
                            line = console.readLine("Layer: ");
                            try {
                                z = Integer.parseInt(line) - 1;
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid row: " + line);
                            }
                            if (z >= 0 && z < 4) break;
                            System.out.println("Invalid row: " + line);
                        } while (true);

                        do {
                            line = console.readLine("Column: ");
                            try {
                                x = Integer.parseInt(line) - 1;
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid column: " + line);
                            }
                            if (x >= 0 && x < 4) break;
                            System.out.println("Invalid col: " + line);
                        } while (true);

                        do {
                            line = console.readLine("Row: ");
                            try {
                                y = Integer.parseInt(line) - 1;
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid row: " + line);
                            }
                            if (y >= 0 && y < 4) break;
                            System.out.println("Invalid row: " + line);
                        } while (true);

                        if (ticTacToeBoard.isEmpty(x,y,z)) break; //edit this to make sure it fits my prompts *should be correct
                        System.out.println("Square is not empty");
                    } while (true);
                    //row*3 + col
                    //dsadasd
                    ticTacToeBoard = ticTacToeBoard.next(x,y,z, human);
                    turn++;

                    ticTacToeBoard.print();
                    System.out.println();
                }
                else{
                    if (ticTacToeBoard.evaluation(Player.X) == Integer.MAX_VALUE) {
                        System.out.println("I won !!!");
                        break;
                    } else if (ticTacToeBoard.numberEmptySquares() == 0) {
                        System.out.println("Tie !!!");
                        break;
                    }

                    int y = 0; //row
                    int x = 0; //col
                    int z = 0; //layer
                    String line = "";
                    do {
                        do {
                            line = console.readLine("Layer: ");
                            try {
                                z = Integer.parseInt(line) - 1;
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid row: " + line);
                            }
                            if (z >= 0 && z < 4) break;
                            System.out.println("Invalid row: " + line);
                        } while (true);

                        do {
                            line = console.readLine("Column: ");
                            try {
                                x = Integer.parseInt(line) - 1;
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid column: " + line);
                            }
                            if (x >= 0 && x < 4) break;
                            System.out.println("Invalid col: " + line);
                        } while (true);

                        do {
                            line = console.readLine("Row: ");
                            try {
                                y = Integer.parseInt(line) - 1;
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid row: " + line);
                            }
                            if (y >= 0 && y < 4) break;
                            System.out.println("Invalid row: " + line);
                        } while (true);

                        if (ticTacToeBoard.isEmpty(x,y,z)) break; //edit this to make sure it fits my prompts *should be correct
                        System.out.println("Square is not empty");
                    } while (true);
                    //row*3 + col
                    //dsadasd
                    ticTacToeBoard = ticTacToeBoard.next(x,y,z, human);
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

                    if(turn == 32) maxDepth++;
                    else if(turn == 48) maxDepth++;

                    try{
                        ticTacToeBoard = ticTacToeBoard.next(minimax.bestMove(ticTacToeBoard, maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE), computer);
                    }
                    catch(Exception e){
                        ArrayList<Coordinate> lastresort = new ArrayList<>();
                        for(Coordinate coordinate: ticTacToeBoard.emptySquares()){
                            lastresort.add(coordinate);
                        }

                        ticTacToeBoard = ticTacToeBoard.next(lastresort.get((int)(Math.random()*lastresort.size())), computer);
                    }
                    turn++;
                    ticTacToeBoard.print(second);
                    System.out.println();
                }


            }
    }

//                if (ticTacToeBoard.evaluation() == Integer.MAX_VALUE) {
//                    System.out.println("I won !!!");
//                    break;
//                } else if (ticTacToeBoard.numberEmptySquares() == 0) {
//                    System.out.println("Tie !!!");
//                    break;
//                }
//
//                int y = 0; //row
//                int x = 0; //col
//                int z = 0; //layer
//                String line = "";
//                do {
//                    do {
//                        line = console.readLine("Layer: ");
//                        try {
//                            z = Integer.parseInt(line) - 1;
//                        } catch (NumberFormatException e) {
//                            System.out.println("Invalid row: " + line);
//                        }
//                        if (z >= 0 && z < 4) break;
//                        System.out.println("Invalid row: " + line);
//                    } while (true);
//
//                    do {
//                        line = console.readLine("Column: ");
//                        try {
//                            x = Integer.parseInt(line) - 1;
//                        } catch (NumberFormatException e) {
//                            System.out.println("Invalid column: " + line);
//                        }
//                        if (x >= 0 && x < 4) break;
//                        System.out.println("Invalid col: " + line);
//                    } while (true);
//
//                    do {
//                        line = console.readLine("Row: ");
//                        try {
//                            y = Integer.parseInt(line) - 1;
//                        } catch (NumberFormatException e) {
//                            System.out.println("Invalid row: " + line);
//                        }
//                        if (y >= 0 && y < 4) break;
//                        System.out.println("Invalid row: " + line);
//                    } while (true);
//
//                    if (ticTacToeBoard.isEmpty(x,y,z)) break; //edit this to make sure it fits my prompts *should be correct
//                    System.out.println("Square is not empty");
//                } while (true);
//                //row*3 + col
//                //dsadasd
//                ticTacToeBoard = ticTacToeBoard.next(x,y,z, human);
//
//                if (ticTacToeBoard.evaluation() == Integer.MIN_VALUE) {
//                    System.out.println("You won !!!");
//                    break;
//                } else if (ticTacToeBoard.numberEmptySquares() == 0) {
//                    System.out.println("Tie !!!");
//                    break;
//                }
//
//                ticTacToeBoard = ticTacToeBoard.next(minimax.bestMove(ticTacToeBoard, maxDepth), computer);
//                System.out.println("KAKAKA");
//                ticTacToeBoard.print();
//            }
}

