import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SemanticAnalyzer {

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
        visualizeSymbolTable(symbolTable, "symbolTable.html", warnings);

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
                if(enteredProc){
                    declarationsChecker(child, node.id, scopeName);
                }
                else{
                    declarationsChecker(child, scopeID, scopeName);
                }
              
            }
        }
    }

    private boolean checkifParentandChildHaveTheSameName(String procName, int scopeID2, String scopeName2) {
    
            if (procName.equals(scopeName2) ) {

                System.out.println("\u001B[31m" + "ERROR:  parent ( " + scopeName2 + " ) and child ( " + procName
                        + " ) can not have the same name" + "\u001B[0m");
                return true;
            }

    
        return false;
    }

    private boolean checkifSiblingsHaveTheSameName(String procName, int scopeID2, String scopeName2) {
        int count = 0;

        
        for (int i = 0; i < symbolTable.size(); i++) {
            if (symbolTable.get(i).NodeName.equals(procName) && (scopeID2) == symbolTable.get(i).ScopeId &&symbolTable.get(i).ScopeName.equals(scopeName2)) {
                count++;
                if (count > 1) {
                    System.out.println("\u001B[31m" + "ERROR:  siblings with procedure name of ( " + procName + " )  with  parentscopeName: ( "
                            + symbolTable.get(i).ScopeName + " ) can not have the same name" + "\u001B[0m");
                    return true;
                }

            }
        }
        return false;
    }

    private boolean checkifProcNameIsSameAsParentSibling(String procName, int scopeID2, String scopeName2) {
        int x=0;
        if(scopeName2=="Main"){
            return false;
        }
        for(int i=0;i<symbolTable.size(); i++){
            if(symbolTable.get(i).NodeId==scopeID2 ){
                scopeID2=symbolTable.get(i).ScopeId;
                scopeName2=symbolTable.get(i).ScopeName;
                // System.out.println("scopeID2 :" +symbolTable.get(i).NodeName );
                x=i;
                break;
            }
        }
      
                for (int j = 0; j < symbolTable.size(); j++) {
                    if (x == j) {
                        continue;
                    }
                    if (symbolTable.get(j).ScopeId == scopeID2&&symbolTable.get(j).ScopeName.equals(scopeName2)
                            && symbolTable.get(j).NodeName.equals(procName)) {
                        System.out.println("\u001B[31m" + "ERROR: " + procName
                                + " can not have the same name as its parent sibling" + "("
                                + symbolTable.get(j).NodeName + ")  No procedure can have a child and a sibling with the same name!" 
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
                warnings += "<p style='color: orange'>" + "WARNING: The procedure ( " + symbolTable2.get(i).NodeName +" id: "+symbolTable2.get(i).NodeId
                        + " ) declared here is not called from anywhere within the scope to which it belong!" + "</p>"
                        + "\n";

                System.out.println("\u001B[38;5;208m" + "WARNING: The procedure  ( " + symbolTable2.get(i).NodeName+" id: "+symbolTable2.get(i).NodeId
                        + " ) is not called in this scope!" + "\u001B[0m");
            }
        }
        return warnings;
    }

    private void callchecker(node node, int scopeID, String scopeName) {
        boolean enteredProc=false;
        if (node.name.equals("PROC")) {
            node theNode = node.children.get(1);

            String digits = concatenateDigits(theNode);

            scopeName = node.children.get(0).name + digits;
           
            enteredProc=true;
        }
        if (node.children.size() > 0) {

            for (node child : node.children) {

               
                if (child.name.equals("CALL")) {
                    node theNode = child.children.get(2);

                    String digits = concatenateDigits(theNode);
                    String procName = "p" + digits;
                 
                    // System.out.println("scope id: "+scopeID+" scope name: "+scopeName +" proc name: "+procName);
                    if (checkifProcIsDeclared(procName)) {
                        if (checkifProcIsCalledByParent(procName, scopeID, scopeName)) {
                       
                        
                        } else if (checkifProcIsCalledBySibling(procName, scopeID, scopeName)) {// check if proc is called
                         
                          
                        } else if (checkifProcIsCalledByItSelf(procName, scopeID, scopeName)) {// check if proc is called by
                           
                           
    
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
                   
                }else if(enteredProc){
                    callchecker(child, node.id, scopeName);
                }else{
                    callchecker(child, scopeID, scopeName);
                }
               
            }
        }
    }

    private void setProcisCalledtoTrue(String procName,int NodeId) {
        for (int i = 0; i < symbolTable.size(); i++) {
            if (symbolTable.get(i).NodeName.equals(procName) && symbolTable.get(i).NodeId==NodeId) {
                symbolTable.get(i).called = true;
            }
        }
    }

    private boolean checkifProcIsCalledByItSelf(String procName, int scopeID2, String scopeName2) {
        if((procName).equals(scopeName2)){
            for(int i=0;i<symbolTable.size(); i++){
                if((symbolTable.get(i).NodeId)==scopeID2){
                    setProcisCalledtoTrue(procName,symbolTable.get(i).NodeId);
                    return true;
                }
            }
           
        }
        return false;
    }

    private boolean checkifProcIsCalledBySibling(String procName, int scopeID2, String scopeName2) {
        int x=0;
        for(int i=0;i<symbolTable.size(); i++){
            if(symbolTable.get(i).NodeId==scopeID2 && symbolTable.get(i).NodeName.equals(scopeName2)){
                scopeID2=symbolTable.get(i).ScopeId;
                scopeName2=symbolTable.get(i).ScopeName;
                x=i;
                break;
            }
        }

      
        
                for(int j=0;j<symbolTable.size(); j++){
                    if(x==j){  // skip where its being called.
                        continue;
                    }
                    if(symbolTable.get(j).NodeName.equals(procName) && (scopeID2)==symbolTable.get(j).ScopeId){
                        setProcisCalledtoTrue(procName,symbolTable.get(j).NodeId);
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
                    setProcisCalledtoTrue(procName,symbolTable.get(i).NodeId);
                    return true;
                }
            }
        }
        return false;
    }

    private static ArrayList<SymbolTable> GOProcedureNames(node node, int scopeID2, String parentProcName) {

        ArrayList<SymbolTable> STProc = new ArrayList<SymbolTable>();
        boolean enteredProc=false;

        if (node.name.equals("PROC")) {
            node theNode = node.children.get(1);

            String digits = concatenateDigits(theNode);
         

            STProc.add(new SymbolTable(node.id, node.children.get(0).name + digits, scopeID2, parentProcName));
            // System.out.println(node.id+"                "+ node.children.get(0).name + digits+"                   "+ scopeID2+"                "+ parentProcName);
        
            parentProcName = node.children.get(0).name + digits;
          
            enteredProc=true;


        }

         if (node.children.size() > 0) {

            for (node child : node.children) {

               if(enteredProc==true){
                STProc.addAll(GOProcedureNames(child, node.id, parentProcName));
                
                }else{
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
        // Create a table header
        String table = "<table>\n";
        table += "    <tr>\n";
        table += "        <th>Node ID</th>\n";
        table += "        <th>Node Name</th>\n";
        table += "        <th>Scope ID</th>\n";
        table += "        <th>Scope Name</th>\n";
        table += "    </tr>\n";

        // Iterate over all nodes in the AST and create a row for each node
        for (SymbolTable n : symbolT) {
            String row = "    <tr>\n";
            row += "        <td>" + n.NodeId + "</td>\n";
            row += "        <td>" + n.NodeName + "</td>\n";
            row += "        <td>" + n.ScopeId + "</td>\n";
            row += "        <td>" + n.ScopeName + "</td>\n";
            row += "    </tr>\n";
            table += row;
        }

        // Close the table
        table += "</table>";

        // Write the table to an HTML file with proper indentation
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write("<!DOCTYPE html>\n<html>\n<head>\n<title>Symbol Table</title>\n</head>\n<body>\n");
            writer.write(table + "\n");
            if (!string.equals("")) {// warning

                writer.write(string);
            }
            writer.write("</body>\n</html>");
            writer.close();
            System.out.println("Symbol table written to file " + fileName);
        } catch (IOException e) {
            System.out.println("Error writing symbol table to file: " + e.getMessage());
        }
    }

}
// current scope is the pass in scope.
// 1for checking for sibling nodes, 2. if it calls it selfs, 3.if is a child
// 1. will go to the symbol table and check
