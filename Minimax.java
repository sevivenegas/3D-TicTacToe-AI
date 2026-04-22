import java.util.*;

/**
 * Minimax search with alpha-beta pruning for the 3D TicTacToe AI.
 *
 * Player X is the maximizing player (AI); Player O is the minimizing player (human).
 *
 * Alpha-beta pruning skips branches that cannot affect the final decision,
 * significantly reducing the number of nodes evaluated compared to plain Minimax.
 *
 * When multiple moves share the same best score, one is chosen at random to
 * prevent the AI from always playing identically.
 */
public class Minimax {

    /**
     * Returns the best move for Player X from the given board state.
     *
     * @param state    the current board
     * @param maxDepth maximum search depth (plies)
     * @param alpha    initial alpha (pass Integer.MIN_VALUE)
     * @param beta     initial beta  (pass Integer.MAX_VALUE)
     */
    public Coordinate bestMove(Board state, int maxDepth, int alpha, int beta) {
        ArrayList<Coordinate> bestMoves = new ArrayList<>();
        int bestValue = Integer.MIN_VALUE;

        for (Coordinate current : state.emptySquares()) {
            Board nextState = state.next(current, Player.X);
            int value = minValue(nextState, 0, maxDepth, alpha, beta);

            if (value == bestValue) {
                bestMoves.add(current);
            } else if (value > bestValue) {
                bestMoves.clear();
                bestValue = value;
                bestMoves.add(current);
            }
        }

        // Break ties randomly so the AI doesn't always play the same game
        return bestMoves.get((int)(Math.random() * bestMoves.size()));
    }

    /**
     * Maximizing player's turn (X). Returns the highest achievable score
     * from this state, pruning branches via alpha-beta.
     */
    public int maxValue(Board state, int depth, int maxDepth, int alpha, int beta) {
        int val = state.evaluation(Player.X);

        // Terminal check: win, loss, draw, or depth limit reached
        if (val == Integer.MAX_VALUE || val == Integer.MIN_VALUE + 1 || val == 0 || depth > maxDepth - 1) {
            return val;
        }

        int bestValue = Integer.MIN_VALUE;
        for (Coordinate action : state.emptySquares()) {
            Board nextState = state.next(action, Player.X);
            int value = minValue(nextState, depth + 1, maxDepth, alpha, beta);
            bestValue = Math.max(bestValue, value);
            alpha = Math.max(alpha, bestValue);
            if (beta <= alpha) break;   // beta cut-off: minimizer won't allow this branch
        }

        return bestValue;
    }

    /**
     * Minimizing player's turn (O). Returns the lowest achievable score
     * from this state, pruning branches via alpha-beta.
     */
    public int minValue(Board state, int depth, int maxDepth, int alpha, int beta) {
        int val = state.evaluation(Player.O);

        // Terminal check: win, loss, draw, or depth limit reached
        if (val == Integer.MAX_VALUE || val == Integer.MIN_VALUE + 1 || val == 0 || depth > maxDepth - 1) {
            return val;
        }

        int bestValue = Integer.MAX_VALUE;
        for (Coordinate action : state.emptySquares()) {
            Board nextState = state.next(action, Player.O);
            int value = maxValue(nextState, depth + 1, maxDepth, alpha, beta);
            bestValue = Math.min(bestValue, value);
            beta = Math.min(beta, bestValue);
            if (beta <= alpha) break;   // alpha cut-off: maximizer won't allow this branch
        }

        return bestValue;
    }
}
