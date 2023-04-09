public class Transition {

    public State from;
    public State to;
    public Character symbol;

    public Transition(State from, State to, Character symbol){
        this.from = from;
        this.to = to;
        this.symbol = symbol;
    }
}
