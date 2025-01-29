public class Game {

    public static interface State<Action> {
        public int evaluate();
        public boolean isTerminal();
        public State next(Action action);
        public Iterable<Action> moves();

        int utility();
    }
}
