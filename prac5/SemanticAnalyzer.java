import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SemanticAnalyzer {

    static int LineCounter = 10;
    public static String BasicCode = "";
    private int ScopeID = 0;
    private String ScopeName = "global";
    static ArrayList<SymbolTable> symbolTable = new ArrayList<SymbolTable>();
    static ArrayList<SymbolTable> symbolTableProc = new ArrayList<SymbolTable>();

    public void GOSemanticAnalyzer(node node) throws IOException {
        GOVarNames(node, 0, "global");
        ArrayList<SymbolTable> proc = GOProcedureNames(node, 1, "Main");
        // add what is in proc to symbol table
        for (int i = 0; i < proc.size(); i++) {
            symbolTable.add(proc.get(i));
        }
        // function that check if the function called is in the correct scope , ie:
        // - proc can only call their children but not their grand children
        // - proc can only their siblings
        // - proc can only itself
        callchecker(node, 1, "Main");
        String warnings = CheckifAllProcsAreCalled(symbolTable);
        // System.out.println("warnings: "+warnings);
        declarationsChecker(node, 1, "Main");
 

        // 
        crawlDown(node, 1, "Main");

        System.out.println(BasicCode);
        visualizeSymbolTable(symbolTable, "basicCode.txt", BasicCode);

    }

    private void declarationsChecker(node node, int scopeID, String scopeName) {
        boolean enteredProc = false;

        if (node.name.equals("PROC")) {

            node theNode = node.children.get(1);

            String digits = concatenateDigits(theNode);

            if (checkifProcNameIsSameAsParentSibling("p" + digits, scopeID, scopeName)) {

                System.exit(0);

            } else if (checkifSiblingsHaveTheSameName("p" + digits, scopeID, scopeName)) {

                System.exit(0);
            } else if (checkifParentandChildHaveTheSameName("p" + digits, scopeID, scopeName)) {

                System.exit(0);
            }

            scopeName = node.children.get(0).name + digits;
            enteredProc = true;

        }
        if (node.children.size() > 0) {

            for (node child : node.children) {
                if (enteredProc) {
                    declarationsChecker(child, node.id, scopeName);
                } else {
                    declarationsChecker(child, scopeID, scopeName);
                }

            }
        }
    }

    private boolean checkifParentandChildHaveTheSameName(String procName, int scopeID2, String scopeName2) {

        if (procName.equals(scopeName2)) {

            System.out.println("\u001B[31m" + "ERROR:  parent ( " + scopeName2 + " ) and child ( " + procName
                    + " ) can not have the same name" + "\u001B[0m");
            return true;
        }

        return false;
    }

    private boolean checkifSiblingsHaveTheSameName(String procName, int scopeID2, String scopeName2) {
        int count = 0;

        for (int i = 0; i < symbolTable.size(); i++) {
            if (symbolTable.get(i).NodeName.equals(procName) && (scopeID2) == symbolTable.get(i).ScopeId
                    && symbolTable.get(i).ScopeName.equals(scopeName2)) {
                count++;
                if (count > 1) {
                    System.out.println("\u001B[31m" + "ERROR:  siblings with procedure name of ( " + procName
                            + " )  with  parentscopeName: ( "
                            + symbolTable.get(i).ScopeName + " ) can not have the same name" + "\u001B[0m");
                    return true;
                }

            }
        }
        return false;
    }

    private boolean checkifProcNameIsSameAsParentSibling(String procName, int scopeID2, String scopeName2) {
        int x = 0;
        if (scopeName2 == "Main") {
            return false;
        }
        for (int i = 0; i < symbolTable.size(); i++) {
            if (symbolTable.get(i).NodeId == scopeID2) {
                scopeID2 = symbolTable.get(i).ScopeId;
                scopeName2 = symbolTable.get(i).ScopeName;
                // System.out.println("scopeID2 :" +symbolTable.get(i).NodeName );
                x = i;
                break;
            }
        }

        for (int j = 0; j < symbolTable.size(); j++) {
            if (x == j) {
                continue;
            }
            if (symbolTable.get(j).ScopeId == scopeID2 && symbolTable.get(j).ScopeName.equals(scopeName2)
                    && symbolTable.get(j).NodeName.equals(procName)) {
                System.out.println("\u001B[31m" + "ERROR: " + procName
                        + " can not have the same name as its parent sibling" + "("
                        + symbolTable.get(j).NodeName
                        + ")  No procedure can have a child and a sibling with the same name!"
                        + "\u001B[0m");
                return true;
            }
        }

        return false;
    }

    private String CheckifAllProcsAreCalled(ArrayList<SymbolTable> symbolTable2) {
        String warnings = "";
        for (int i = 0; i < symbolTable2.size(); i++) {
            if ((symbolTable2.get(i).NodeName).charAt(0) == 'p' && symbolTable2.get(i).called == false) {
                warnings += "<p style='color: orange'>" + "WARNING: The procedure ( " + symbolTable2.get(i).NodeName
                        + " id: " + symbolTable2.get(i).NodeId
                        + " ) declared here is not called from anywhere within the scope to which it belong!" + "</p>"
                        + "\n";

                System.out.println("\u001B[38;5;208m" + "WARNING: The procedure  ( " + symbolTable2.get(i).NodeName
                        + " id: " + symbolTable2.get(i).NodeId
                        + " ) is not called in this scope!" + "\u001B[0m");
            }
        }
        return warnings;
    }

    private void callchecker(node node, int scopeID, String scopeName) {
        boolean enteredProc = false;
        if (node.name.equals("PROC")) {
            node theNode = node.children.get(1);

            String digits = concatenateDigits(theNode);

            scopeName = node.children.get(0).name + digits;

            enteredProc = true;
        }
        if (node.children.size() > 0) {

            for (node child : node.children) {

                if (child.name.equals("CALL")) {
                    node theNode = child.children.get(2);

                    String digits = concatenateDigits(theNode);
                    String procName = "p" + digits;

                    // System.out.println("scope id: "+scopeID+" scope name: "+scopeName +" proc
                    // name: "+procName);
                    if (checkifProcIsDeclared(procName)) {
                        if (checkifProcIsCalledByParent(procName, scopeID, scopeName)) {

                        } else if (checkifProcIsCalledBySibling(procName, scopeID, scopeName)) {// check if proc is
                                                                                                // called

                        } else if (checkifProcIsCalledByItSelf(procName, scopeID, scopeName)) {// check if proc is
                                                                                               // called by

                        } else {

                            System.out.println("\u001B[31m" + "ERROR: The procedure  ( " + procName
                                    + " ) called here has no corresponding declaration in this scope!" + "\u001B[0m");
                            System.exit(0);
                        }
                    } else {
                        System.out.println("\u001B[31m" + "ERROR:" + " The procedure ( " + procName
                                + " )  called here is not declared" + "\u001B[0m");
                        System.exit(0);
                    }
                    callchecker(child, scopeID, scopeName);

                } else if (enteredProc) {
                    callchecker(child, node.id, scopeName);
                } else {
                    callchecker(child, scopeID, scopeName);
                }

            }
        }
    }

    private void setProcisCalledtoTrue(String procName, int NodeId) {
        for (int i = 0; i < symbolTable.size(); i++) {
            if (symbolTable.get(i).NodeName.equals(procName) && symbolTable.get(i).NodeId == NodeId) {
                symbolTable.get(i).called = true;
            }
        }
    }

    private boolean checkifProcIsCalledByItSelf(String procName, int scopeID2, String scopeName2) {
        if ((procName).equals(scopeName2)) {
            for (int i = 0; i < symbolTable.size(); i++) {
                if ((symbolTable.get(i).NodeId) == scopeID2) {
                    setProcisCalledtoTrue(procName, symbolTable.get(i).NodeId);
                    return true;
                }
            }

        }
        return false;
    }

    private boolean checkifProcIsCalledBySibling(String procName, int scopeID2, String scopeName2) {
        int x = 0;
        for (int i = 0; i < symbolTable.size(); i++) {
            if (symbolTable.get(i).NodeId == scopeID2 && symbolTable.get(i).NodeName.equals(scopeName2)) {
                scopeID2 = symbolTable.get(i).ScopeId;
                scopeName2 = symbolTable.get(i).ScopeName;
                x = i;
                break;
            }
        }

        for (int j = 0; j < symbolTable.size(); j++) {
            if (x == j) { // skip where its being called.
                continue;
            }
            if (symbolTable.get(j).NodeName.equals(procName) && (scopeID2) == symbolTable.get(j).ScopeId) {
                setProcisCalledtoTrue(procName, symbolTable.get(j).NodeId);
                return true;
            }
        }

        return false;
    }

    private boolean checkifProcIsDeclared(String procName) {
        for (int i = 0; i < symbolTable.size(); i++) {
            if (symbolTable.get(i).NodeName.equals(procName)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkifProcIsCalledByParent(String procName, int scopeID2, String scopeName2) {
        for (int i = 0; i < symbolTable.size(); i++) {
            if (symbolTable.get(i).NodeName.equals(procName)) {
                if (symbolTable.get(i).ScopeName.equals(scopeName2)) {
                    setProcisCalledtoTrue(procName, symbolTable.get(i).NodeId);
                    return true;
                }
            }
        }
        return false;
    }

    private static ArrayList<SymbolTable> GOProcedureNames(node node, int scopeID2, String parentProcName) {

        ArrayList<SymbolTable> STProc = new ArrayList<SymbolTable>();
        boolean enteredProc = false;

        if (node.name.equals("PROC")) {
            node theNode = node.children.get(1);

            String digits = concatenateDigits(theNode);

            STProc.add(new SymbolTable(node.id, node.children.get(0).name + digits, scopeID2, parentProcName));
            // System.out.println(node.id+" "+ node.children.get(0).name + digits+" "+
            // scopeID2+" "+ parentProcName);

            parentProcName = node.children.get(0).name + digits;

            enteredProc = true;

        }

        if (node.children.size() > 0) {

            for (node child : node.children) {

                if (enteredProc == true) {
                    STProc.addAll(GOProcedureNames(child, node.id, parentProcName));

                } else {
                    STProc.addAll(GOProcedureNames(child, scopeID2, parentProcName));
                }
            }
        }
        return STProc;

    }
    public static void GOVarNames(node node, int ScopeID, String ScopeName) {
        if (node == null) {
            return;
        }
        if (node.type.equals("Non-Terminal")) {

            for (int ii = 0; ii < node.children.size(); ii++) {
                if (node.children.get(ii).name.equals("NUMVAR") || node.children.get(ii).name.equals("BOOLVAR")
                        || node.children.get(ii).name.equals("STRINGV")) {
                    node theNode = node.children.get(ii).children.get(1);

                    String digits = concatenateDigits(theNode);
                    
                    // search for nodename in the symbol table if it is there then do not add it
                    if (SearchForNodeName(node.children.get(ii).children.get(0).name + digits)) {
                        symbolTable.add(new SymbolTable(node.children.get(ii).id,
                                node.children.get(ii).children.get(0).name + digits, 0, "global"));
                     
                    }

                    // ii=ii+1;
                    GOVarNames(node.children.get(ii), ScopeID, ScopeName);
                } else {
                    GOVarNames(node.children.get(ii), ScopeID, ScopeName);
                }

            }

        } else if (node.type.equals("Terminal")) {
            // System.out.println(" terminal: "+ node.name);
            GOVarNames(null, ScopeID, ScopeName);
        }

    }


    private static boolean SearchForNodeName(String string) {
        for (int i = 0; i < symbolTable.size(); i++) {
            if (symbolTable.get(i).NodeName.equals(string)) {
                return false;
            }
        }
        return true;
    }

    private static String concatenateDigits(node n) {
        StringBuilder sb = new StringBuilder();

        // Base case: node is a D node, append its name to the string
        if (n.name.equals("D")) {
            sb.append(n.children.get(0).name);
        }

        // Recursive case: node has children, traverse them and append their names to
        // the string
        else if (n.children.size() > 0) {
            for (node child : n.children) {
                sb.append(concatenateDigits(child));
            }
        }

        // Return the concatenated string
        return sb.toString();
    }

    public static void visualizeSymbolTable(ArrayList<SymbolTable> symbolT, String fileName, String string) {

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(string);

            writer.close();
            System.out.println("Symbol table written to file " + fileName);
        } catch (IOException e) {
            System.out.println("Error writing symbol table to file: " + e.getMessage());
        }
    }



    private void crawlDown(node node, int currentScope, String currentProc){
        if(node == null){
            return;
        }
            

        if(node.type.equals("Non-Terminal")){
           if(node.name.equals("PROGR")){
                if(node.children.size() ==2){
                    crawlDown(node.children.get(0), currentScope, currentProc);
                    BasicCode+= String.valueOf(LineCounter) + " END\n";
                    LineCounter+=10;
                    crawlDown(node.children.get(1), currentScope, currentProc);
                }
                else{
                    crawlDown(node.children.get(0), currentScope, currentProc);
                    BasicCode += String.valueOf(LineCounter) + " END\n";
                    LineCounter+=10;

                }
                return;
           }
           else if(node.name.equals("INPUT")){
         
                node theNode = node.children.get(1); // NUMVAR

                theNode = theNode.children.get(1);// DIGITS

                String digits = concatenateDigits(theNode);


                BasicCode += LineCounter + " INPUT \"\"; " + " n" + digits + "\n ";
                LineCounter += 10;
                // LineCounter INPUT “”; var_name

           }
           else if(node.name.equals("OUTPUT")){
            node theNode = node.children.get(0); // TEXT OR VALUE
            if (theNode.name.equals("TEXT")) {
                theNode = theNode.children.get(1); // STRINGV

                node theNode2 = theNode.children.get(1); // Digits
                String digits = concatenateDigits(theNode2);
                BasicCode += LineCounter + "  PRINT ; " + " s" + digits + "$\n ";
                LineCounter += 10;
               

            } else if (theNode.name.equals("VALUE")) {
                theNode = theNode.children.get(1); // NUMVAR

                theNode = theNode.children.get(1);// DIGITS

                String digits = concatenateDigits(theNode);
                BasicCode += LineCounter + "  PRINT ; " + " n" + digits + "\n ";
                LineCounter += 10;
               
            }
           }
           else if(node.name.equals("ASSIGN")){
                TransAssign(node);
           }
       
           else if(node.name.equals("BRANCH")){
               
                TransBra(node, currentScope, currentProc);
                
                return;
           }
           else if(node.name.equals("LOOP")){
          
                TransLP(node, currentScope, currentProc);
                return;
           }
   
            for(node child: node.children){
                crawlDown(child, currentScope, currentProc);
            }

        }else{
            if(node.name.equals("h")){
                BasicCode += LineCounter + " STOP" +"\n ";
                LineCounter += 10;
               
            }
            crawlDown(null, currentScope, currentProc);
        }
    }

    private void TransLP(node node, int currentScope, String currentProc) {
        String boolexpr = TransBoolExpr(node.children.get(2));
    
        int enL = LineCounter;
 
 
        BasicCode += String.valueOf(LineCounter) + " " + "IF " + boolexpr + " Then GOTO other"+    "\n";
        LineCounter += 10;
        BasicCode += String.valueOf(LineCounter) + " " + "GOTO exit" +   "\n";
        LineCounter += 10;
        int otherLineCounter = LineCounter;
        crawlDown(node.children.get(5), currentScope, currentProc);
        BasicCode+= String.valueOf(LineCounter) + " " + "GOTO "  +enL + "\n";
        LineCounter += 10;
        int exitLineCounter = LineCounter;
 
 
        BasicCode = BasicCode.replace("other" , String.valueOf(otherLineCounter));
        BasicCode = BasicCode.replace("exit" , String.valueOf(exitLineCounter));
 
 
 
    }


    private void TransBra(node node, int currentScope, String currentProc) {
      
            if(node.children.size() == 9){
                String boolexpr = TransBoolExpr(node.children.get(2));
                // int  = branchCount;
     
     
                BasicCode+= String.valueOf(LineCounter) + " " + "IF " + boolexpr + " Then GOTO thenbranch" + "\n";
                LineCounter += 10;
                crawlDown(node.children.get(8), currentScope, currentProc);
                BasicCode+= String.valueOf(LineCounter) + " " + "GOTO exit" +   "\n";
                LineCounter += 10;
                int thenLineCounter = LineCounter;
                crawlDown(node.children.get(6), currentScope, currentProc);
                int exitLineCounter = LineCounter;
     
         
                BasicCode = BasicCode.replace("thenbranch" , String.valueOf(thenLineCounter));
                BasicCode = BasicCode.replace("exit"  , String.valueOf(exitLineCounter));
               
     
     
            }else{
                String boolexpr = TransBoolExpr(node.children.get(2));
               
     
                BasicCode+= String.valueOf(LineCounter) + " " + "IF " + boolexpr + " Then GOTO thenbranch"  + "\n";
                LineCounter += 10;
                BasicCode+= String.valueOf(LineCounter) + " " + "GOTO exit"   + "\n";
                LineCounter += 10;
                int thenLineCounter = LineCounter;
                crawlDown(node.children.get(6), currentScope, currentProc);
                int exitLineCounter = LineCounter;
                System.out.println("thenLineCounter: "+thenLineCounter);
     
                BasicCode = BasicCode.replace("thenbranch" , String.valueOf(thenLineCounter));
                BasicCode = BasicCode.replace("exit"  , String.valueOf(exitLineCounter));

               
            }
     
    }

    private void TransAssign(node node) {
        String lefthandside="";
        String righthandside    = "";
        node theNode = node.children.get(0); // NUMVAR

        theNode = theNode.children.get(1);// DIGITS

        String digits = concatenateDigits(theNode);
        
        if(node.children.get(3).name.equals("NUMEXPR")){
            lefthandside = " n" + digits;
             righthandside = TransNumExpr(node.children.get(3));
             BasicCode += LineCounter  +" LET "+ lefthandside + " = " + righthandside + "\n ";
        }
        else if(node.children.get(3).name.equals("BOOLEXPR")){
            lefthandside = " b" + digits;
            righthandside = TransBoolExpr(node.children.get(3));
            BasicCode += LineCounter  +" LET "+ lefthandside + " = " + righthandside + "\n ";

        }else if(node.children.get(3).name.equals("STRI")){
            lefthandside = " s" + digits;
            lefthandside+="$";
            righthandside = node.children.get(3).children.get(0).name;
            BasicCode += LineCounter  +" LET "+ lefthandside + " = " + righthandside + "\n ";
            
        }
        



    }
    private String TransBoolExpr(node node) {
        if(node.children.get(0).name.equals("LOGIC")){
           
            

            return TransLogic(node.children.get(0));
        }
        else{
 
 
            return TransCmpr(node.children.get(0));
        }
    }
 
    

    private String TransCmpr(node node) {
        if(node.children.get(0).name.equals("E")){
            return TransNumExpr(node.children.get(2)) + " = " + TransNumExpr(node.children.get(4));
        }
        else if(node.children.get(0).name.equals("<")){
            return TransNumExpr(node.children.get(2)) + " < " + TransNumExpr(node.children.get(4));
        }
        else if(node.children.get(0).name.equals(">")){
            return TransNumExpr(node.children.get(2)) + " > " + TransNumExpr(node.children.get(4));
        }
        else{
            return "";
        }
 
    }

    private String TransLogic(node node) {
        if(node.children.get(0).name.equals("BOOLVAR")){
             node theNode = node.children.get(0).children.get(1);// DIGITS

            String digits = concatenateDigits(theNode);
            String boolv = "b" + digits;
            return boolv;
          
        }
        else if(node.children.get(0).name.equals("T")){
            return "1";
        }
        else if(node.children.get(0).name.equals("F")){
            return "0";
        }
        else if(node.children.get(0).name.equals("^")){
            TransAnd(node);
            return "P";
        }
        else if(node.children.get(0).name.equals("v")){
            TransOr(node);
            return "P";
 
 
        }
        else if(node.children.get(0).name.equals("!")){
            TransNot(node);
            return "P";
 
 
        }
        else{
            return "";
        
        } 
 
    }

    private void TransNot(node node) {
        String boolexpr1 = TransBoolExpr(node.children.get(2));


        BasicCode += String.valueOf(LineCounter) + " IF " + boolexpr1 + " THEN GOTO failed \n";
        LineCounter += 10;
       BasicCode += String.valueOf(LineCounter) + " LET P = 1 \n";
       LineCounter += 10;
       BasicCode += String.valueOf(LineCounter) + " GOTO exit \n";
       LineCounter += 10;
       int failedLineNumber = LineCounter;
       BasicCode += String.valueOf(LineCounter) + " LET P = 0 \n";
       LineCounter += 10;
       int exitLineNumber = LineCounter;


       BasicCode = BasicCode.replace("failed", String.valueOf(failedLineNumber));
       BasicCode = BasicCode.replace("exit", String.valueOf(exitLineNumber));

    }

    private void TransOr(node node) {

        String blexpr1 = TransBoolExpr(node.children.get(2));
        String blexpr2 = TransBoolExpr(node.children.get(4));
 
 
        BasicCode += String.valueOf(LineCounter) + " IF " + blexpr1 + " THEN GOTO success \n";
        LineCounter += 10;
        BasicCode += String.valueOf(LineCounter) + " IF " + blexpr2 + " THEN GOTO success \n";
        LineCounter += 10;
        BasicCode += String.valueOf(LineCounter) + " GOTO failed \n";
        LineCounter += 10;
        int successLineCounter = LineCounter;
        BasicCode += String.valueOf(LineCounter) + " LET P = 1 \n";
        LineCounter += 10;
        BasicCode += String.valueOf(LineCounter) + " GOTO exit \n";
        LineCounter += 10;
        int failedLineCounter = LineCounter;
        BasicCode += String.valueOf(LineCounter) + " LET P = 0 \n";
        LineCounter += 10;
        int exitLineCounter = LineCounter;
 
 
        BasicCode = BasicCode.replace("success", String.valueOf(successLineCounter));
        BasicCode = BasicCode.replace("failed", String.valueOf(failedLineCounter));
        BasicCode = BasicCode.replace("exit", String.valueOf(exitLineCounter));
 
 
 
    }

    private void TransAnd(node node) {
        String blexpr1 = TransBoolExpr(node.children.get(2));
        String blexpr2 = TransBoolExpr(node.children.get(4));
 
 
        BasicCode += String.valueOf(LineCounter) + " IF " + blexpr1 + " THEN GOTO otherCond \n";
        LineCounter += 10;
        BasicCode += String.valueOf(LineCounter) + " GOTO failed \n";
        LineCounter += 10;
        int otherCondLineCounter = LineCounter;
        BasicCode += String.valueOf(LineCounter) + " IF " + blexpr2 + " THEN GOTO success \n";
        LineCounter += 10;
        BasicCode += String.valueOf(LineCounter) + " GOTO failed \n";
        LineCounter += 10;
        int successLineCounter = LineCounter;
        BasicCode += String.valueOf(LineCounter) + " LET P = 1 \n";
        LineCounter += 10;
        BasicCode += String.valueOf(LineCounter) + " GOTO exit \n";
        LineCounter += 10;
        int failedLineCounter = LineCounter;
        BasicCode += String.valueOf(LineCounter) + " LET P = 0 \n";
        LineCounter += 10;
        int exitLineCounter = LineCounter;
 
 
        BasicCode = BasicCode.replace("otherCond", String.valueOf(otherCondLineCounter));
        BasicCode = BasicCode.replace("success", String.valueOf(successLineCounter));
        BasicCode = BasicCode.replace("failed", String.valueOf(failedLineCounter));
        BasicCode = BasicCode.replace("exit", String.valueOf(exitLineCounter));
 
 
 
        
    }

    String TransNumExpr(node node){
  
        if(node.children.get(0).name.equals("NUMVAR")){

           

            node theNode = node.children.get(0).children.get(1);// DIGITS

            String digits = concatenateDigits(theNode);

            return "n" + digits;

        }else if(node.children.get(0).name.equals("DECNUM")){
            if(node.children.get(0).children.get(0).name.equals("0")){
                return "0.00";
            }
            else if(node.children.get(0).children.get(0).name.equals("POS")){
                String pos=CollectPos(node.children.get(0).children.get(0));
                return pos;

            }else if(node.children.get(0).children.get(0).name.equals("-")){

               String pos2=CollectPos(node.children.get(0).children.get(1));
               return "-"+pos2;

            }
       
        }else if(node.children.get(0).name.equals("a")){
            return TransNumExpr(node.children.get(2)) + "+" + TransNumExpr(node.children.get(4));

        }else if(node.children.get(0).name.equals("m")){
            return TransNumExpr(node.children.get(2)) + "*" + TransNumExpr(node.children.get(4));

        }else if(node.children.get(0).name.equals("d")){
            return TransNumExpr(node.children.get(2)) + "/" + TransNumExpr(node.children.get(4));

        }
        return "";
    }

    private String CollectPos(node n) {
        StringBuilder sb = new StringBuilder();

        // Base case: node is a D node, append its name to the string
        if (n.name.equals("D")) {
            sb.append(n.children.get(0).name);
        }
        else if(n.name.equals("INT")){
            sb.append(n.children.get(0).name);
        }
        else if(n.name.equals(".")){
            sb.append(".");
        }
        

        // Recursive case: node has children, traverse them and append their names to
        // the string
        else if (n.children.size() > 0) {
            for (node child : n.children) {
                sb.append(CollectPos(child));
            }
        }

        // Return the concatenated string
        return sb.toString();
    }

        

}
// current scope is the pass in scope.
// 1for checking for sibling nodes, 2. if it calls it selfs, 3.if is a child
// 1. will go to the symbol table and check
