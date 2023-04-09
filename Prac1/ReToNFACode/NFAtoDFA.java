import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
//Integer.parseInt
public class NFAtoDFA {
 
    DFA NFAtoDFA(NFA nfa)
    {
        

        DFA dfa = new DFA();
        ArrayList<State> start = new ArrayList<State>();
        start.add(nfa.startState);
        ArrayList<State> q0 = nfa.eclosure(start);  // find the epsilon-closure of the start state
        
        int state_from = dfa.addEntry(q0);//the index of the new vertex is stored in vertex_from.

        ArrayList<Boolean> marked = new ArrayList<Boolean>();
        marked.add(false);

        while (state_from != -1000)
        {
            ArrayList<State> T = dfa.entries.get(state_from);
            dfa.marked.set(state_from, true);
            marked.set(state_from, true);
            ArrayList<Character> inputs = getInputs(nfa, T);
            for(int a=0;a<inputs.size();a++)
            {
                char input = inputs.get(a);
                ArrayList<State> epsilonClosureOfMoveResult = nfa.eclosure(nfa.move(T, input));

                int state_to = dfa.find_entry(epsilonClosureOfMoveResult);
              
                //(ab*)aa*(a|b)
                if (state_to == -1) { 
                        state_to = dfa.addEntry(epsilonClosureOfMoveResult);
                        marked.add(false);
                   
                }
                boolean transitionExists = false;
                for(int i=0;i<dfa.transitions.size();i++)
                {
                    if(dfa.transitions.get(i).from.name.equals(Integer.toString(state_from)) && dfa.transitions.get(i).to.name.equals(Integer.toString(state_to)) && dfa.transitions.get(i).symbol == input)
                    {
                        transitionExists = true;
                        break;
                    }
                }

                if (!transitionExists) {
                    State from = new State(Integer.toString(state_from), false);
                    State to = new State(Integer.toString(state_to), false);
        
                    dfa.setTransition(from, to, input);

                }

              
            }
            state_from = getNextUnmarkedStateIndex(marked);
            
        }
        dfa.startState=dfa.transitions.get(0).from; //Start state is the first state in the DFA
        dfa.setFinalState(nfa.endState);


        
        // for(int i=0;i<this.transitions.size();i++)
        // {
        //     System.out.print("q: "+this.transitions.get(i).from.name+ "   {");
        //     for(int ii=0;ii<this.entries.get(Integer.parseInt(this.transitions.get(i).from.name)).size();ii++)
        //     {
        //         System.out.print(this.entries.get(Integer.parseInt(this.transitions.get(i).from.name)).get(ii).name+",");
        //     }
        //     System.out.print(" }  to    ");

        //     System.out.print("q: "+this.transitions.get(i).to.name+ "  {");

        //     for(int x=0;x<this.entries.get(Integer.parseInt(this.transitions.get(i).to.name)).size();x++)
        //     {
        //         System.out.print(this.entries.get(Integer.parseInt(this.transitions.get(i).to.name)).get(x).name+",");
        //     }
        //     System.out.print(" }     with input::   "+this.transitions.get(i).symbol);
        //     System.out.println();
            

        // }

        return dfa;
    }
    int getNextUnmarkedStateIndex(ArrayList<Boolean> marked) 
    {
        for (int i = 0; i < marked.size(); i++)
        {
            if (!marked.get(i)) 
            {
                return i;
            }
        }
        return -1000;
    }
    // DFA NFAtoDFA(NFA nfa)
    // {
        

    //     DFA dfa = new DFA();
    //     ArrayList<State> start = new ArrayList<State>();
    //     start.add(nfa.startState);
    //     ArrayList<State> q0 = nfa.eclosure(start);  // find the epsilon-closure of the start state

    //     // for (State i : q0)
    //     // {
    //     //     System.out.println(i.name + ", ");
    //     //     System.out.println("SIze of start: " + q0.size() );
    //     // }
    //     // System.out.println();

    //     int state_from = dfa.addEntry(q0);//the index of the new vertex is stored in vertex_from.

    //     while(state_from !=-1){
       
    //     ArrayList<State> T = dfa.entries.get(state_from);
    //     ArrayList<Character> inputs = getInputs(nfa, T);
    //     dfa.mark_entry(state_from);

    //     // System.out.println("Inputs: " + inputs.size());
    //     for (Character c : inputs)
    //     {
    //         System.out.println("The input: "+c);
    //         ArrayList<State> temp = new ArrayList<State>();
    //         for (State i : T )
    //         {
    //             for(int x=0;x<nfa.states.size();x++)
    //             {
    //                 if(nfa.states.get(x).name.equals(i.name))
    //                 {
    //                     for(int y=0;y<nfa.states.get(x).transitions.size();y++)
    //                     {
    //                         if(nfa.states.get(x).transitions.get(y).symbol.equals(c))
    //                         { //  System.out.println("from : "+i.name+"    The transition: to "+nfa.states.get(x).transitions.get(y).to.name + "    with input: "+nfa.states.get(x).transitions.get(y).symbol);
    //                             temp.add(nfa.states.get(x).transitions.get(y).to);
    //                         }
    //                     }
    //                 }
    //             }
    //         }
    //         // for(int i=0;i<temp.size();i++)
    //         // {
    //         //     System.out.println("temp before eclosure: " + temp.get(i).name);
    //         // }
            
    //         temp = nfa.eclosure(nfa.move(T, c));
    //         // temp = removeDuplicates(temp);
    //         int state_to = dfa.find_entry(temp);
    //         if (state_to == -1) { // U not already in S'
    //             // state_to = dfa.addEntry(U);
    //              state_to = dfa.addEntry(temp);
    //         }
           

    //         State from=new State(Integer.toString(state_from), false);
    //         State to=new State(Integer.toString(state_to), false);
        
    //         dfa.setTransition(from, to, c);
            
    //     }
    //     state_from = dfa.next_unmarked_entry_idx();

    // }
    //     dfa.setFinalState(nfa.endState);
    //     System.out.println("NFAtoDFA DONE::::::::::::");
       
    //     return dfa;
    // }


    //Search if the array list of states exsisit on the entries list
    public Boolean exisit(ArrayList<State> eclosurelist, ArrayList<ArrayList<State>> entries) {
        for (ArrayList<State> entry : entries) {
          
            if (entry.equals(eclosurelist)) {
                return true;
            }
        }
        return false;
    }



    private ArrayList<ArrayList<State>> removeDuplicates(ArrayList<ArrayList<State>> temp) {
        System.out.println("removeDuplicates@");
        ArrayList<ArrayList<State>> result = new ArrayList<ArrayList<State>>();
        for (ArrayList<State> i : temp)
        {
            if (!result.contains(i))
            {
                result.add(i);
            }
        }
        System.out.println("removeDuplicates DONE::::::::::::");
        return result;
    }
    public ArrayList<Character> getInputs(NFA nfa, ArrayList<State> entry)
    {  
        //  System.out.println("getInputs@");
        // System.out.println("size of entry: " + entry.size());

        // //print all the states names in the entry
        // for (State i : entry)
        // {
        //     System.out.println(i.name + "       , ");
        // }

        ArrayList<Character> inputs = new ArrayList<Character>();
        

        for (State i : entry)
        {
            // for (Transition t : nfa.states.get(counter++).transitions)
            // {   System.out.println("t.from: " + t.from.name);
            //     if ((t.from) == i && !inputs.contains(t.symbol))
            //     {
            //         inputs.add(t.symbol);
            //     }
            // }
            for(int x=0;x<nfa.states.size();x++)
            {
                for(int xx=0;xx<nfa.states.get(x).transitions.size();xx++)
                {
                    if(nfa.states.get(x).transitions.get(xx).from==i && nfa.states.get(x).transitions.get(xx).symbol!='#')
                    {   System.out.println(nfa.states.get(x).transitions.get(xx).symbol);
                        inputs.add(nfa.states.get(x).transitions.get(xx).symbol);
                    }
                }
            }//((a.b*).(a.a*).(a|b))
        }
        // print all the inputs
        // for(int i=0;i<inputs.size();i++)
        // {
        //     System.out.println("inputs: " + inputs.get(i));
        // }
        Collections.sort(inputs);
        return inputs;
    }
    
} 

