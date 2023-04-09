import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.io.FileWriter;
import java.io.IOException;

import javax.print.attribute.standard.Sides;
import javax.swing.plaf.synth.SynthEditorPaneUI;

public class ToMinDFA {

    public DFA minimizeDFA(DFA finalDFA) {
        // Step 1: Initialize the partition P1 with the set of final states and the set
        // of non-final states.
        char B='A';
        ArrayList<ArrayList<State>> P1 = new ArrayList<ArrayList<State>>();
        ArrayList<State> finalStates = new ArrayList<State>();
        ArrayList<State> nonFinalStates = new ArrayList<State>();
        //convert a string to an integer
        // int finalState = Integer.parseInt(finalDFA.finalStates.get(0).name);
       for(int l=0;l<finalDFA.entries.size();l++)
       {
            State newState=new State(Integer.toString(l),false);
           
            if(finalDFA.finalStates.contains(l))
            {
                newState.isAccepting=true;
            }
     
            finalDFA.updatedlist.add(newState);
       }
        for(int i=0;i<finalDFA.transitions.size();i++)
        {
            
            finalDFA.updatedlist.get(Integer.parseInt(finalDFA.transitions.get(i).from.name)).addTransition(finalDFA.transitions.get(i).to,finalDFA.transitions.get(i).symbol);
            
        }
        
       
        // Separate final and non-final states
        for (int i = 0; i < finalDFA.updatedlist.size(); i++) {
           
            if (finalDFA.updatedlist.get(i).isAccepting) {
                finalStates.add(finalDFA.updatedlist.get(i));
                
            } else {
                nonFinalStates.add(finalDFA.updatedlist.get(i));
               
            }
        }
        // for(int i=0;i<finalDFA.updatedlist.size();i++)
        // {
        //     System.out.println("state name:"+finalDFA.updatedlist.get(i).name);
        //     for(int j=0;j<finalDFA.updatedlist.get(i).transitions.size();j++)
        //     {
        //         System.out.println("transition:"+finalDFA.updatedlist.get(i).transitions.get(j).symbol);
        //         System.out.println("to state:"+finalDFA.updatedlist.get(i).transitions.get(j).to.name);
        //     }
        // }

        // Add final and non-final states to the partition
        P1.add(nonFinalStates);
        P1.add(finalStates);
     
      
        // Step 2: Repeat until the partition no longer changes.
        boolean partitionChanged = true;
        int breakCount=0;
        while (partitionChanged) { //while false
            partitionChanged = false;

            // Step 3: Iterate over each set in the current partition.
            ArrayList<ArrayList<State>> P2 = new ArrayList<ArrayList<State>>();

           
            for (int i=0;i< P1.size();i++) {
                ArrayList<State> set =P1.get(i);

               
                if (set.size() == 1) {
                    // If the set has only one state, add it to the new partition P2.
                  
                    
                    continue;
                }

                 int alphabetSize=getDFAinput(finalDFA).size();
            
                  

                    for (int j=0; j<set.size()-1;j++) {
                    
                        State state1 = set.get(0);
                        State state2 = set.get(j+1);
                        ArrayList<State> toStates = new ArrayList<State>();
                       


                        for(int ii=0;ii<alphabetSize;ii++){// Input a b c d
                         
                            for(int y=0;y<state1.transitions.size();y++){
                             
                                if(state1.transitions.get(y).symbol.equals(getDFAinput(finalDFA).get(ii)) )
                                {
                                   
                                    toStates.add(state1.transitions.get(y).to);
                                }
                           
                            }
                            for(int y=0;y<state2.transitions.size();y++){
                              
                                if( state2.transitions.get(y).symbol.equals(getDFAinput(finalDFA).get(ii)) )
                                {
                                    toStates.add(state2.transitions.get(y).to);
                                }
                            }
                        

                            if(toStates.size()==0)
                            {
                                
                                continue;
                            }
                            else if(toStates.size()==1){// it means state1 and state2 are not equivalent, now we need to remove state2 on group A, and look for a group that is EQ to state2 ,
                                                        // if theres no group create a new group for it and add the new group to P1
                             
                          
    
                                //make a function that will check
                                partitionChanged=true;
                             
                                set.remove(j+1);
                              
                                P1=checkifIkhonakumaGroup(state2,P1,finalDFA);
                                --j;
                                break;
    
                            }else if(toStates.size()>1){//
                          
                               //check if their equal
                               if(toStates.get(0).name.equals(toStates.get(1).name)){
                                continue;
                               }
                               else {
                           
                                Boolean status=false;
                                //loop through every set, check if theres a set that contains both tostates at 0 and 1
                                for(int s=0;s<P1.size();s++){

                                    ArrayList<State> sets=P1.get(s);
                                    
                                        if(sets.contains(toStates.get(0)) && sets.contains(toStates.get(1))){
                                            status=true;
                                            break;
                                        }
                                } 
                                if(status==false){
                                   
                                   
                                    set.remove(j+1);
                                    
                                    //make a function that will check
                                    partitionChanged=true;
    
                                    P1=checkifIkhonakumaGroup(state2,P1,finalDFA);
                                    --j;
                                    break;
                                }else{
                                   
                                    continue;
                                }
                               }
    
                            }
                  

                        }
                      
                        
                        
                    } 

            }
         
            
         
            breakCount++;
        }
        
        DFA newDFA=new DFA();
        newDFA.entries=P1;
        
        for(int i=0;i<P1.size();i++){
            ArrayList<State> set=P1.get(i);
            String name=Character.toString(B++);
            State newState=new State(name, set.get(0).isAccepting);
            newState.states=set;
            newDFA.statesforMinDFA.add(newState);
            
         }


 
         for(State statess: newDFA.statesforMinDFA){
            
                for(Transition transition: statess.states.get(0).transitions){
                    for(State Stateee2: newDFA.statesforMinDFA){
                        System.out.println("Stateee2.states.get(0).name: "+ Stateee2.states.get(0).name);
                        if(Stateee2.states.get(0).name.equals(transition.to.name)){
                            statess.addTransition(Stateee2, transition.symbol);
                            break;
                        }
                    }
                    
                
            }
            
            
        }

        try {
            FileWriter writer = new FileWriter("MinDFA.xml");

            writer.write("<MinDFA>\n");
            writer.write("<states>\n");
        for(State minDFAState: newDFA.statesforMinDFA){

            writer.write("<"+minDFAState.name+"");
            if(minDFAState.isAccepting){
                writer.write(" accepting=\"true\"");
            }
            writer.write("/>\n");
        }
            writer.write("</states>\n");
            writer.write("<transitions>\n");

            for(State minDFAState: newDFA.statesforMinDFA){
                for(Transition transition: minDFAState.transitions){
                    writer.write("\t<"+transition.from.name+">\n");
                    writer.write("\t\t<"+transition.to.name+">"+transition.symbol+"</"+transition.to.name+">\n");
                    writer.write("\t</"+transition.from.name+">\n");

                }
            }
            writer.write("</transitions>\n");
            writer.write("</MinDFA>\n");

            writer.close();




        } catch (IOException e) {
            
            e.printStackTrace();
        }
         // Step 7: Create a new DFA from the final partition.
        

        return newDFA;
    }
    private ArrayList<ArrayList<State>> checkifIkhonakumaGroup(State state2, ArrayList<ArrayList<State>> P1,DFA finalDFA) {
        Boolean hasGroup=true;
        for (int i=0;i< P1.size();i++) {
             hasGroup=true;
            ArrayList<State> set =P1.get(i);

    

             int alphabetSize=getDFAinput(finalDFA).size();
        
              

              
           

                    State state1 = set.get(0);
                   
                    ArrayList<State> toStates = new ArrayList<State>();
                   

           

                    for(int ii=0;ii<alphabetSize;ii++){// Input a b c d
                        
                      
                        for(int y=0;y<state1.transitions.size();y++){
                      
                           
                            if(state1.transitions.get(y).symbol.equals(getDFAinput(finalDFA).get(ii)) )
                            {
                               
                                toStates.add(state1.transitions.get(y).to);
                            }
                       
                        }
                        for(int y=0;y<state2.transitions.size();y++){
                       
                          
                            if( state2.transitions.get(y).symbol.equals(getDFAinput(finalDFA).get(ii)) )
                            {
                              
                                toStates.add(state2.transitions.get(y).to);
                            }
                        }
                  
                        if(toStates.size()==0)
                        {
                            
                            continue;
                        }
                        else if(toStates.size()==1){// it means state1 and state2 are not equivalent, now we need to remove state2 on group A, and look for a group that is EQ to state2 ,
                                                    // if theres no group create a new group for it and add the new group to P1
                             hasGroup=false;

                            break;

                        }else if(toStates.size()>1){//
                          
                           if(toStates.get(0).name.equals(toStates.get(1).name)){
                        
                            continue;
                           }
                           else {
                            Boolean status=false;
                            //loop through every set, check if theres a set that contains both tostates at 0 and 1
                            for(int s=0;s<P1.size();s++){

                                ArrayList<State> sets=P1.get(s);
                                
                                    if(sets.contains(toStates.get(0)) && sets.contains(toStates.get(1))){
                                        status=true;
                                        break;
                                    }
                            } 
                            if(status==false){
                                 hasGroup=false;
                                break;
                                

                            }else{
                                continue;

                            }
                           }

                        }

                    }
                    
                   if( hasGroup==true){
                    //print  hasGroup
                  
                    set.add(state2);
                    return P1;
                    
                   }
                       
                      
                   

                    
                
               


        }

        if( hasGroup==false){
           
            ArrayList<State> newSet=new ArrayList<State>();
            newSet.add(state2);
            P1.add(newSet);
        }
     
        return P1;
    }
   
   
    
    public  ArrayList<State> getStatesForName(String name, ArrayList<ArrayList<State>> partition) {
        for (ArrayList<State> set : partition) {
            for (State state : set) {
                if (state.name.equals(name)) {
                    return set;
                }
            }
        }
        return null;
    }
    public State getStatesForNameQ(String name, ArrayList<State> partition) {
        for (State set : partition) {
            
                if (set.name.equals(name)) {
                    return set;
                
            }
        }
        return null;
    }

    public  String getStateName(ArrayList<State> states) {
        String name = "";
        for (State state : states) {
            name += state.name + ",";
        }
        name = name.substring(0, name.length() - 1);
        return name;
    }


    public  ArrayList<Character>  getDFAinput(DFA finalDFA) {
        ArrayList<Character> inputs = new ArrayList<Character>();
        
        for(int i=0;i<finalDFA.transitions.size();i++)
        {   //System.out.println("finalDFA.transitions.get(i).symbol: "+finalDFA.transitions.get(i).symbol);
            if (!inputs.contains(finalDFA.transitions.get(i).symbol))/// I may want to include duplicates soon not sure
            {
                inputs.add(finalDFA.transitions.get(i).symbol);
            }
        }

        // Collections.sort(inputs);
        return inputs;
    }



}
