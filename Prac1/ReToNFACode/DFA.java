import java.util.ArrayList;

public class DFA {

    public  ArrayList<State> updatedlist;
    public ArrayList<Transition> transitions;
    public ArrayList<Integer> finalStates;
    public ArrayList<Boolean> marked;
    public ArrayList<ArrayList<State>> entries;
    public ArrayList<State> statesforMinDFA;
    
    State startState;
    public DFA() {
        transitions = new ArrayList<Transition>();
        finalStates = new ArrayList<Integer>();
        entries=new ArrayList<>();
        marked = new ArrayList<Boolean>();
        statesforMinDFA=new ArrayList<State>();
        updatedlist=new ArrayList<State>();
    }
    public int addEntry(ArrayList<State> entry)
    {
        entries.add(entry);
        marked.add(false);
        return entries.size() - 1;
    }
    
    // setFinalState(int state, boolean isFinal)
    // sets the final state status of the given state.
    public void setFinalState(State state)
    {
        // finalStates.add(state);
        for (int i = 0; i < entries.size(); i++) 
        {
            ArrayList<State> entry = entries.get(i);   
            for (int j = 0; j < entry.size(); j++) 
            {
                State vertex = entry.get(j);
                if (vertex == state) {
                    finalStates.add(i);
                }
            }
        }

        ArrayList<Integer> uniqueList = new ArrayList<Integer>();
        for (int i = 0; i < finalStates.size(); i++)
         {
            if (!uniqueList.contains(finalStates.get(i))) {
                uniqueList.add(finalStates.get(i));
            }
        }
        finalStates=uniqueList;
    }
    
    public boolean isFinalState(Integer state) {
        return finalStates.contains(state);
    
    }
    public void markEntry(int state) {
        marked.set(state, true);
    }

    public boolean isMarked(int state) {
        return marked.get(state);
    }

    public int nextUnmarkedEntryIndex() {
        for (int i = 0; i < marked.size(); i++) {
            if (!marked.get(i)) {
                return i;
            }
        }
        return -1;
    }
    


    //setTransition(from, to, input) sets the transition from state from to state to on input symbol input.
    public void setTransition(State from, State to, char input)
    {
        transitions.add(new Transition(from, to, input));
    }

    //getTransition(from, input): returns the state that from transitions to on input symbol input.
    public State getTransition(State from, char input)
    {
        for (Transition t : transitions)
        {
            if (t.from == from && t.symbol == input)
            {
                return t.to;
            }
        }
        return null;
    }
    public void setTransition(ArrayList<State> arrayList, ArrayList<State> arrayList2, char c) {
        for (State i : arrayList)
        {
            for (State j : arrayList2)
            {
                setTransition(i, j, c);
            }
        }
    }
    
    void displayDFA() 
    {
        //Display the DFA
        System.out.println("DFA");
        System.out.println("size of entries"+this.entries.size());
        System.out.println("size of transitions"+this.transitions.size());
        for(int i=0;i<this.transitions.size();i++)
        {
            System.out.print("q: "+this.transitions.get(i).from.name+ "   {");
            for(int ii=0;ii<this.entries.get(Integer.parseInt(this.transitions.get(i).from.name)).size();ii++)
            {
                System.out.print(this.entries.get(Integer.parseInt(this.transitions.get(i).from.name)).get(ii).name+",");
            }
            System.out.print(" }  to    ");

            System.out.print("q: "+this.transitions.get(i).to.name+ "  {");

            for(int x=0;x<this.entries.get(Integer.parseInt(this.transitions.get(i).to.name)).size();x++)
            {
                System.out.print(this.entries.get(Integer.parseInt(this.transitions.get(i).to.name)).get(x).name+",");
            }
            System.out.print(" }     with input::   "+this.transitions.get(i).symbol);
            System.out.println();
            

        }
  

        System.out.print("final states are  q: ");
        for(int w=0;w<this.finalStates.size();w++)
        {
            System.out.print(this.finalStates.get(w)+" , ");
        }

        System.out.println();
    }

    String join(ArrayList<State> v, String delim)//Show the formatting of NFA properly 
    {
        StringBuilder ss=new StringBuilder();
        for(int i = 0; i < v.size(); ++i) {
            if(i != 0)
                ss.append(delim);
            ss.append(v.get(i));
        }
        return ss.toString();
    }

    int find_entry(ArrayList<State> entry) // MUST CHECK THIS FUNCTION
    {
        for (int i = 0; i < entries.size(); i++) {
           ArrayList<State> it = entries.get(i);
           if (it.equals(entry)) {
               return i;
           }
        }
        return -1;
    }
   

}
