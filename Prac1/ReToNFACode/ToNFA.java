import java.util.*;

public class ToNFA {

    Stack<NFA> stack = new Stack<NFA>();
    int stateCount = 0;
    public static boolean isRegexCorrect(String input){
        int numoOpenBrackets = 0;
        int numClosedBrackets = 0;
        for(int i = 0;i<input.length();i++){
            if(numoOpenBrackets < numClosedBrackets){
                return false;
            }
            if(input.charAt(i) == '('){
                numoOpenBrackets++;
            }
            else if(input.charAt(i) == ')'){
                numClosedBrackets++;
            }
        }
        if(numoOpenBrackets != numClosedBrackets){
            return false;
        }

        if(input.charAt(0) == '*' || input.charAt(0) == '|' || input.charAt(0) == '?' || input.charAt(0) == '+'){
            return false;
        }

        for(int i = 0;i<input.length();i++){
            if(input.charAt(i) != '(' && input.charAt(i) != ')' && input.charAt(i) != '*' && input.charAt(i) != '|' && input.charAt(i) != '?' && input.charAt(i) != '+' && !Character.isLetterOrDigit(input.charAt(i)) ){
                return false;
            }

            if(input.charAt(i) == '('){
                if(i != input.length()-1){
                    if(input.charAt(i+1) == '*' || input.charAt(i+1) == '|' || input.charAt(i+1) == '?' || input.charAt(i+1) == '+'){
                        return false;
                    }
                    if(input.charAt(i+1) == ')'){
                        return false;
                    }
                }
                else{
                    return false;
                }
                
            }
            if(input.charAt(i) == '|'){
                if(i == 0 || i == input.length()-1){
                    return false;
                }
                if(input.charAt(i-1) == '(' || input.charAt(i+1) == ')' || input.charAt(i-1) == '|' || input.charAt(i+1) == '*' || input.charAt(i+1) == '|' || input.charAt(i+1) == '?' || input.charAt(i+1) == '+'){
                    return false;
                }
            }

        }
        return true;
      
    }
    
    public NFA ConvertToNFA(String input){

        for(int i = 0;i<input.length();i++){
            if(input.charAt(i) == '('){
                stack.push(null);
            }
            else if(input.charAt(i) == ')'){

                
                List<NFA> nfaList = new ArrayList<NFA>();
                while(stack.peek() != null){
                    nfaList.add(stack.pop());
                }
                stack.pop();

                NFA unionNFA = new NFA();

                boolean isUnion = false;

                for(NFA nfas : nfaList){
                    if(nfas.isUnion){
                        isUnion = true;
                    }
                }

                nfaList = reverseList(nfaList);
                
                if(isUnion){
                    List<NFA> nfaList2 = new ArrayList<NFA>();
                    List<NFA> newList = new ArrayList<NFA>();
                    for(NFA nfa : nfaList){
                        if(!nfa.isUnion){
                            nfaList2.add(nfa);
                            if(nfaList.get(nfaList.size()-1) == nfa){
                                if(nfaList2.size()>1){
                                    NFA subNFA = new NFA();
        
                                    for(int j =0 ;j<nfaList2.size()-1;j++){
                                        nfaList2.get(j).addEpsilonTransition(nfaList2.get(j).endState, nfaList2.get(j+1).startState);
                                    }
        
                                    for(int j =0 ;j<nfaList2.size();j++){
                                        for(State state : nfaList2.get(j).states){
                                            subNFA.states.add(state);
                                        }
                                    }
        
                                    subNFA.startState = nfaList2.get(0).startState;
                                    subNFA.endState = nfaList2.get(nfaList2.size()-1).endState;
        
                                    newList.add(subNFA);
                                    
        
                                }
                                else{
                                    newList.add(nfaList2.get(0));
                                }
                                nfaList2.clear();
                            }
                        }
                        else{
                            if(nfaList2.size()>1){
                                NFA subNFA = new NFA();
    
                                for(int j =0 ;j<nfaList2.size()-1;j++){
                                    nfaList2.get(j).addEpsilonTransition(nfaList2.get(j).endState, nfaList2.get(j+1).startState);
                                }
    
                                for(int j =0 ;j<nfaList2.size();j++){
                                    for(State state : nfaList2.get(j).states){
                                        subNFA.states.add(state);
                                    }
                                }
    
                                subNFA.startState = nfaList2.get(0).startState;
                                subNFA.endState = nfaList2.get(nfaList2.size()-1).endState;
    
                                newList.add(subNFA);
                                newList.add(nfa);
    
                            }
                            else{
                                newList.add(nfaList2.get(0));
                                newList.add(nfa);
                            }
                            nfaList2.clear();
                           
                        }
                        
                    }
                    unionNFA.unionNFA(newList, stateCount);
                    stateCount += 2;
                    stack.push(unionNFA);
                }
                else{
                    if(nfaList.size()==1){
                        stack.push(nfaList.get(0));
                    }
                    else if(nfaList.size()>1){
                        NFA subNFA = new NFA();
    
                        for(int j =0 ;j<nfaList.size()-1;j++){
                            nfaList.get(j).addEpsilonTransition(nfaList.get(j).endState, nfaList.get(j+1).startState);
                        }

                        for(int j =0 ;j<nfaList.size();j++){
                            for(State state : nfaList.get(j).states){
                                subNFA.states.add(state);
                            }
                        }

                        subNFA.startState = nfaList.get(0).startState;
                        subNFA.endState = nfaList.get(nfaList.size()-1).endState;

                        stack.push(subNFA);
                    }
                }
            }
            else if(input.charAt(i) == '*'){
                NFA nfa = new NFA();
                nfa.kleeNfa(stack.pop(), stateCount);
                stateCount += 2;
                stack.push(nfa);
            }
            else if(input.charAt(i) == '+'){
                NFA nfa = new NFA();
                nfa.plus(stack.pop(), stateCount);
                stateCount += 2;
                stack.push(nfa);
            }
            else if(input.charAt(i) == '?'){
                NFA nfa = new NFA();
                nfa.optional(stack.pop(), stateCount);
                stateCount += 2;
                stack.push(nfa);
            }
            else if(input.charAt(i) == '|'){
                NFA nfa = new NFA();
                nfa.isUnion = true;
                stack.push(nfa);
            }
            else{
                NFA nfa = new NFA();
                String label = Integer.toString(stateCount);
                nfa.startState = new State(label, false);
                stateCount++;
                String label2 = Integer.toString(stateCount);
                nfa.endState = new State(label2, false);
                stateCount++;
                nfa.startState.addTransition(nfa.endState, input.charAt(i));
                nfa.states.add(nfa.startState);
                nfa.states.add(nfa.endState);

                if(!stack.empty() && stack.peek() !=null && !stack.peek().isUnion && ( i == input.length()-1 || (input.charAt(i+1) != '*' && input.charAt(i+1) != '?' && input.charAt(i+1) != '+'))){
                    NFA newNFA = new NFA();
                    NFA prevNFA = stack.pop();
                    newNFA.startState = prevNFA.startState;

                    newNFA.endState = nfa.endState;


                    newNFA.addEpsilonTransition(prevNFA.endState, nfa.startState);

                    for(State state : prevNFA.states){
                        newNFA.states.add(state);
                    }
                   
                    for(State state : nfa.states){
                        newNFA.states.add(state);
                    }


                    stack.push(newNFA);
                    
                }
                else{
                    stack.push(nfa);
                }
               
            }
        }

        if(stack.size()>1){

            List<NFA> nfaList = new ArrayList<NFA>();

            while(!stack.empty()){
                if(stack.peek() != null){
                    nfaList.add(stack.pop());
                }
            }

            nfaList = reverseList(nfaList);

            Boolean hasUnion  = false;

            for(NFA nfa : nfaList){
                if(nfa.isUnion){
                    hasUnion = true;
                    break;
                }
            }

            if(hasUnion){
                List<NFA> nfaList2 = new ArrayList<NFA>();
                List<NFA> newList = new ArrayList<NFA>();
                for(NFA nfa : nfaList){
                    if(!nfa.isUnion){
                        nfaList2.add(nfa);
                        if(nfaList.get(nfaList.size()-1) == nfa){
                            if(nfaList2.size()>1){
                                NFA subNFA = new NFA();
    
                                for(int i =0 ;i<nfaList2.size()-1;i++){
                                    nfaList2.get(i).addEpsilonTransition(nfaList2.get(i).endState, nfaList2.get(i+1).startState);
                                }
    
                                for(int i =0 ;i<nfaList2.size();i++){
                                    for(State state : nfaList2.get(i).states){
                                        subNFA.states.add(state);
                                    }
                                }
    
                                subNFA.startState = nfaList2.get(0).startState;
                                subNFA.endState = nfaList2.get(nfaList2.size()-1).endState;
    
                                newList.add(subNFA);
    
                            }
                            else{
                                newList.add(nfaList2.get(0));
                            }
                            nfaList2.clear();
                        }
                    }
                    else{
                        if(nfaList2.size()>1){
                            NFA subNFA = new NFA();

                            for(int i =0 ;i<nfaList2.size()-1;i++){
                                nfaList2.get(i).addEpsilonTransition(nfaList2.get(i).endState, nfaList2.get(i+1).startState);
                            }

                            for(int i =0 ;i<nfaList2.size();i++){
                                for(State state : nfaList2.get(i).states){
                                    subNFA.states.add(state);
                                }
                            }

                            subNFA.startState = nfaList2.get(0).startState;
                            subNFA.endState = nfaList2.get(nfaList2.size()-1).endState;

                            newList.add(subNFA);
                            newList.add(nfa);

                        }
                        else{
                            newList.add(nfaList2.get(0));
                            newList.add(nfa);
                        }
                        nfaList2.clear();
                       
                    }
                    
                }
                NFA finalNFA = new NFA();
                finalNFA.unionNFA(newList, stateCount);
                stack.push(finalNFA);



            }
            else{
                NFA nfa = new NFA();

                for(int i =0 ;i<nfaList.size()-1;i++){
                    nfaList.get(i).addEpsilonTransition(nfaList.get(i).endState, nfaList.get(i+1).startState);
                }
    
                for(int i =0 ;i<nfaList.size();i++){
                    for(State state : nfaList.get(i).states){
                        nfa.states.add(state);
                    }
                }
    
                nfa.startState = nfaList.get(0).startState;
                nfa.endState = nfaList.get(nfaList.size()-1).endState;
    
                stack.push(nfa);
            }
        }
        stack.peek().endState.isAccepting = true;
        return stack.pop();

    }

   

    public List<NFA> reverseList(List<NFA> list){
        for(int i = 0;i<list.size()/2;i++){
            NFA temp = list.get(i);
            list.set(i, list.get(list.size()-1-i));
            list.set(list.size()-1-i, temp);
        }

        return list;
    }

    public void printNFA(NFA nfa){
            System.out.println("Printing NFA");
            
            System.out.println("States: ");

            for(State state : nfa.states){
                System.out.print("State: " + state.name + " ");
                if(state.isAccepting){
                    System.out.println("Accepting");
                }
                else{
                    System.out.println();
                }

                // System.out.println("Is start state: " + state.transitions.size());
            }

            System.out.println("Transitions: ");


            for(State state : nfa.states){
                for(Transition transition : state.transitions){
                    System.out.println("Transition: " +"from " + transition.from.name +" " + transition.symbol + " to " + transition.to.name);
                }
            }
    }
}
