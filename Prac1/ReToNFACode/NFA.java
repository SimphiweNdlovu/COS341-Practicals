import java.util.*;

public class NFA {
    
    public State startState;
    public State endState;
    public List<State> states;
    public boolean isUnion;

    public NFA(){
        this.startState = null;
        this.endState = null;
        isUnion = false;
        states = new ArrayList<State>();
    }

    public void addEpsilonTransition(State state, State desState){
       Transition outTransition = new Transition(state, desState, '#');
       state.transitions.add(outTransition);
    }

    public NFA unionNFA(List<NFA> nfaList, int count){
        // integer to character
        String label = Integer.toString(count);
        this.startState = new State(label, false);
        count++;
        String label2 = Integer.toString(count);
        this.endState = new State(label2, false);
        count++;
        this.states.add(startState);
        this.states.add(endState);
        for(NFA nfa : nfaList){
            if(!nfa.isUnion){
                addEpsilonTransition(startState, nfa.startState);
                nfa.endState.addTransition(endState, '#');
                for(State state :nfa.states){
                    this.states.add(state);
                }
            }
        }
        return this;
    }

    public NFA kleeNfa(NFA nfa, int count){
        String label = Integer.toString(count);
        this.startState = new State(label, false);
        count++;
        String label2 = Integer.toString(count);
        this.endState = new State(label2, false);
        count++;
        this.states.add(startState);
        this.states.add(endState);
        addEpsilonTransition(startState,nfa.startState);
        nfa.endState.addTransition(startState, '#');
        addEpsilonTransition(startState, endState);
        for(State state :nfa.states){
            this.states.add(state);
        }
        
        return this;
    }

    public NFA plus(NFA nfa, int count){
        String label = Integer.toString(count);
        this.startState = new State(label, false);
        count++;
        String label2 = Integer.toString(count);
        this.endState = new State(label2, false);
        count++;
        this.states.add(startState);
        this.states.add(endState);
        startState.addTransition(nfa.startState, '#');
        nfa.endState.addTransition(endState, '#');
        endState.addTransition(startState, '#');
        for(State state :nfa.states){
            this.states.add(state);
        }
        return this;
    }

    public NFA optional(NFA nfa, int count){
        String label = Integer.toString(count);
        this.startState = new State(label, false);
        count++;
        String label2 = Integer.toString(count);
        this.endState = new State(label2, false);
        count++;
        startState.addTransition(endState, '#');
        startState.addTransition(nfa.startState, '#');
        nfa.endState.addTransition(endState, '#');
        this.states.add(startState);
        this.states.add(endState);
        for(State state :nfa.states){
            this.states.add(state);
        }
        return this;
    }


    ArrayList<State> eclosure(ArrayList<State> thestates) 
    {   
       
        ArrayList<State> result=new ArrayList<>();
        boolean visited[]=new boolean[states.size()];
        for (int i = 0; i < thestates.size(); i++) {
            eclosure(thestates.get(i), result, visited);
        }
        
        Collections.sort(result, new Comparator<State>() {
            @Override
            public int compare(State o1, State o2) {
                return o1.name.compareTo(o2.name);
            }
        });

        ArrayList<State> uniqueList = new ArrayList<State>();
        for (int i = 0; i < result.size(); i++)
         {
            if (!uniqueList.contains(result.get(i))) {
                uniqueList.add(result.get(i));
            }
        }
        result= uniqueList;

        return (result);
    }
    void eclosure(State cur, ArrayList<State> result, boolean visited[]) 
    {
        
        result.add(cur);

       
        for(int x=0;x<states.size();x++)
        {
            for(int xx=0;xx<states.get(x).transitions.size();xx++)
            {
               
                if(states.get(x).transitions.get(xx).from==cur   && states.get(x).transitions.get(xx).symbol=='#')
                {
                    State TostateWepsilon=states.get(x).transitions.get(xx).to;
                    if(!visited[Integer.parseInt(TostateWepsilon.name)]){
                        visited[Integer.parseInt(TostateWepsilon.name)]=true;
                        eclosure(TostateWepsilon, result,visited);
                    }
                    
                }
            }
        }
       
    }
    


    ArrayList<State> move(ArrayList<State> T, char input) 
    {
        ArrayList<State> result=new ArrayList<>();
        for (int j = 0; j < T.size(); j++) {
            State t = T.get(j);
        
            for(int x=0;x<states.size();x++)
            {
                for(int xx=0;xx<states.get(x).transitions.size();xx++)
                {
                    if(states.get(x).transitions.get(xx).from==t   && states.get(x).transitions.get(xx).symbol==input)
                    {
                        result.add(states.get(x).transitions.get(xx).to);
                        
                    }
                }
            }
        }
       
        Collections.sort(result, new Comparator<State>() {
            @Override
            public int compare(State o1, State o2) {
                return o1.name.compareTo(o2.name);
            }
        });

        ArrayList<State> uniqueList = new ArrayList<State>();
        for (int i = 0; i < result.size(); i++)
         {
            if (!uniqueList.contains(result.get(i))) {
                uniqueList.add(result.get(i));
            }
        }
        result= uniqueList;
        return result;
    }
   
}
