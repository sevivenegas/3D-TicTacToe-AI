import java.util.*;

public class Minimax{

    HashMap<Board, Integer> hash = new HashMap<>();

    // alpha is best value of maximizer and beta is max value of minimizer
    public Coordinate bestMove(Board state, int maxDepth, int alpha, int beta) {
        ArrayList<Coordinate> bestMoves = new ArrayList<>();
        Coordinate bestMove = null;
        int bestValue = Integer.MIN_VALUE;

        for (Coordinate current : state.emptySquares()) {
            Board nextState = state.next(current, Player.X);
            int value = minValue(nextState,0, maxDepth, alpha, beta);
            if(value == bestValue) bestMoves.add(current);
            else if (value > bestValue) {
                bestMoves.clear();
                bestValue = value;
                bestMoves.add(current);
            }

        }

        return bestMoves.get((int)(Math.random()*bestMoves.size()));
    }

    public int maxValue(Board state, int depth, int maxDepth, int alpha, int beta) {
//        if(hash.containsKey(state)) return hash.get(state);

        int val = state.evaluation(Player.X);
        if (val == Integer.MAX_VALUE || val == Integer.MIN_VALUE+1 || val == 0||  depth > maxDepth-1) {
            //utility and isTerminal two forloops minimize by only using isTerminal returned values
            //make it consider tie and other shit
            return val;
        }

        int bestValue = Integer.MIN_VALUE;
        for (Coordinate action : state.emptySquares()) {
            Board nextState = state.next(action, Player.X); // Assuming Player.X is the maximizing player
            int value = minValue(nextState, depth + 1, maxDepth, alpha, beta);
            bestValue = Math.max(bestValue, value);
            alpha = Math.max(alpha, bestValue);
            if(beta <= alpha) break;
        }

//        hash.put(state, bestValue);
        return bestValue;
    }

    public int minValue(Board state, int depth, int maxDepth, int alpha, int beta) {

//        if(hash.containsKey(state)) return hash.get(state);

        int val = state.evaluation(Player.O);

        if (val == Integer.MAX_VALUE || val == Integer.MIN_VALUE+1 || val == 0||  depth > maxDepth-1) {
            //utility and isTerminal two forloops minimize by only using isTerminal returned values
            //make it consider tie and other shit
            return val;
        }

        int bestValue = Integer.MAX_VALUE;

        for (Coordinate action : state.emptySquares()) {
            Board nextState = state.next(action, Player.O); // Assuming Player.O is the minimizing player
            int value = maxValue(nextState, depth + 1, maxDepth, alpha, beta);
            bestValue = Math.min(bestValue, value);
            beta = Math.min(beta, bestValue);
            if(beta <= alpha) break;
        }

//        hash.put(state, bestValue);
        return bestValue;
    }

}
