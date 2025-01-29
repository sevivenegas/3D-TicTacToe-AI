import java.util.ArrayList;
import java.util.List;

public class Minimax<Action> {

    public Action bestMove(Game.State<Action> state, int maxDepth) {
        Action bestMove = null;
        int bestValue = Integer.MIN_VALUE;

        for (Action action : state.moves()) {
            int value = minValue(state.next(action),0, maxDepth);
            if (value > bestValue) {
                bestValue = value;
                bestMove = action;
            }
        }

        return bestMove;
    }

    public int maxValue(Game.State<Action> state, int depth, int maxDepth) {
        if (state.isTerminal() || depth > maxDepth-1) {
            int x =  state.utility();
            return x;
        }

        int bestValue = Integer.MIN_VALUE;
        for (Action action : state.moves()) {
            int value = minValue(state.next(action), depth + 1, maxDepth);
            bestValue = Math.max(bestValue, value);
        }

        return bestValue;
    }

    public int minValue(Game.State<Action> state, int depth, int maxDepth) {
        if (state.isTerminal() || depth > maxDepth-1) {
            int x =  state.utility();
            return x;
        }

        int bestValue = Integer.MAX_VALUE;
        for (Action action : state.moves()) {
            int value = maxValue(state.next(action), depth + 1, maxDepth);
            bestValue = Math.min(bestValue, value);
        }

        return bestValue;
    }

}
