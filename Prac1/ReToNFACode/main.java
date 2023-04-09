import java.util.Scanner;
//Pure Regex to DFA is From Musa Mabasa
public class main {

    public static void main(String[] args) {
        Scanner regex = new Scanner(System.in);
        System.out.println("Enter a RE: ");
        String input = regex.nextLine();
        if(isRegexCorrect(input)){
            System.out.println("\u001B[32mRegex is correct\u001B[0m");
            ToNFA nfa = new ToNFA();

          
        

            NFA finalNFA = nfa.ConvertToNFA(input);
    
            nfa.printNFA(finalNFA);
            System.out.println("DFA: ");
            NFAtoDFA dfa = new NFAtoDFA();
            DFA finalDFA = dfa.NFAtoDFA(finalNFA);
            System.out.println("DFA: ");
            finalDFA.displayDFA();
            System.out.println("Minimized DFA: ");
            ToMinDFA minDFA = new ToMinDFA();
            DFA minDFA2 = minDFA.minimizeDFA(finalDFA);
             // nfa.printNFA(convertedNFA);
     
             // System.out.println("=================================================================");
             // System.out.println();

         }
        else{
            System.out.println("\u001B[31mRegex is incorrect\u001B[0m");
        }

      
        

        
    }
    
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
}
