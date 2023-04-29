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
        ArrayList<SymbolTable> proc = GOProcedureNames(node, 0, "global");
        // add what is in proc to symbol table
        for (int i = 0; i < proc.size(); i++) {
            symbolTable.add(proc.get(i));
        }
        // function that check if the function called is in the correct scope , ie:
        // - proc can only call their children but not their grand children
        // - proc can only their siblings
        // - proc can only itself
        callchecker(node, 0, "global");
        String warnings=CheckifAllProcsAreCalled(symbolTable);
        // System.out.println("warnings: "+warnings);
        declarationsChecker(node, 0, "global");
        visualizeSymbolTable(symbolTable, "symbolTable.html",warnings);
      
    }

    private void declarationsChecker(node node, int scopeID, String scopeName) {
        if (node.name.equals("PROC")) {
            node theNode = node.children.get(1);

            String digits = concatenateDigits(theNode);

            scopeName = node.children.get(0).name + digits;
            scopeID++;

        }
        if (node.children.size() > 0) {

            for (node child : node.children) {
                if (child.name.equals("CALL")) {
                    node theNode = child.children.get(2);

                    String digits = concatenateDigits(theNode);
                    String procName = "p" + digits;
                    System.out.println();
                    System.out.println("CALL : " + procName);
                    System.out.println("scopeID: " + scopeID + "   ScopeName: " + scopeName);

           
                    if (checkifProcIsDeclared(procName)) {
                        System.out.println("procName: " + procName + " is declared");
                    } else {
                        System.out.println("\u001B[31m" + "ERROR:" + " The procedure ( " + procName
                                + " )  called here is not declared" + "\u001B[0m");
                        System.exit(0);
                    }

                    if (checkifProcIsCalledByParent(procName, scopeID, scopeName)) {
                        System.out.println("procName: " + procName + " is in scope");
                        setProcisCalledtoTrue(procName);
                    } else if (checkifProcIsCalledBySibling(procName, scopeID, scopeName)) {// check if proc is called
                        setProcisCalledtoTrue(procName);                                                              // by sibling
                        System.out.println("procName: " + procName + " is in scope");
                    } else if (checkifProcIsCalledByItSelf(procName, scopeID, scopeName)) {// check if proc is called by
                        setProcisCalledtoTrue(procName);                                                  // itself
                        System.out.println("procName: " + procName + " is in scope");

                    } else {
                        System.out.println("procName: " + procName + " is not in scope");
                 
                        System.out.println("\u001B[31m" + "ERROR: The procedure  ( " + procName
                                + " ) called here has no corresponding declaration in this scope!" + "\u001B[0m");
                        System.exit(0);
                    }
                }
                callchecker(child, scopeID, scopeName);
            }
        }
    }

    private String CheckifAllProcsAreCalled(ArrayList<SymbolTable> symbolTable2) {
        String warnings="";
        for (int i = 0; i < symbolTable2.size(); i++) {
            if (symbolTable2.get(i).called == false) {
                warnings+= "<p style='color: orange'>" + "WARNING: The procedure ( "+symbolTable2.get(i).NodeName+" ) declared here is not called from anywhere within the scope to which it belong!" + "</p>" +"\n";

                        System.out.println("\u001B[38;5;208m" + "WARNING: The procedure  ( " + symbolTable2.get(i).NodeName
                        + " ) is not called in this scope!" + "\u001B[0m");
            }
        }
        return warnings;
    }

    private void callchecker(node node, int scopeID, String scopeName) {

        if (node.name.equals("PROC")) {
            node theNode = node.children.get(1);

            String digits = concatenateDigits(theNode);

            scopeName = node.children.get(0).name + digits;
            scopeID++;

        }
        if (node.children.size() > 0) {

            for (node child : node.children) {
                if (child.name.equals("CALL")) {
                    node theNode = child.children.get(2);

                    String digits = concatenateDigits(theNode);
                    String procName = "p" + digits;
                    System.out.println();
                    System.out.println("CALL : " + procName);
                    System.out.println("scopeID: " + scopeID + "   ScopeName: " + scopeName);

           
                    if (checkifProcIsDeclared(procName)) {
                        System.out.println("procName: " + procName + " is declared");
                    } else {
                        System.out.println("\u001B[31m" + "ERROR:" + " The procedure ( " + procName
                                + " )  called here is not declared" + "\u001B[0m");
                        System.exit(0);
                    }

                    if (checkifProcIsCalledByParent(procName, scopeID, scopeName)) {
                        System.out.println("procName: " + procName + " is in scope");
                        setProcisCalledtoTrue(procName);
                    } else if (checkifProcIsCalledBySibling(procName, scopeID, scopeName)) {// check if proc is called
                        setProcisCalledtoTrue(procName);                                                              // by sibling
                        System.out.println("procName: " + procName + " is in scope");
                    } else if (checkifProcIsCalledByItSelf(procName, scopeID, scopeName)) {// check if proc is called by
                        setProcisCalledtoTrue(procName);                                                  // itself
                        System.out.println("procName: " + procName + " is in scope");

                    } else {
                        System.out.println("procName: " + procName + " is not in scope");
                 
                        System.out.println("\u001B[31m" + "ERROR: The procedure  ( " + procName
                                + " ) called here has no corresponding declaration in this scope!" + "\u001B[0m");
                        System.exit(0);
                    }
                }
                callchecker(child, scopeID, scopeName);
            }
        }
    }

    private void setProcisCalledtoTrue(String procName) {
        for (int i = 0; i < symbolTable.size(); i++) {
            if (symbolTable.get(i).NodeName.equals(procName)) {
                symbolTable.get(i).called = true;
            }
        }
    }

    private boolean checkifProcIsCalledByItSelf(String procName, int scopeID2, String scopeName2) {
        for (int i = 0; i < symbolTable.size(); i++) {
            if (symbolTable.get(i).NodeName.equals(procName)) {
                if (symbolTable.get(i).ScopeName.equals(scopeName2)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkifProcIsCalledBySibling(String procName, int scopeID2, String scopeName2) {
        for (int i = 0; i < symbolTable.size(); i++) {
            if (symbolTable.get(i).NodeName.equals(scopeName2)) {
                String parentName = symbolTable.get(i).ScopeName;
                for (int j = 0; j < symbolTable.size(); j++) {
                    if (symbolTable.get(j).NodeName.equals(procName)) {
                        if (symbolTable.get(j).ScopeName.equals(parentName)) {
                            return true;
                        }
                    }
                }
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
                    return true;
                }
            }
        }
        return false;
    }

    private static ArrayList<SymbolTable> GOProcedureNames(node node, int scopeID2, String parentProcName) {

        ArrayList<SymbolTable> STProc = new ArrayList<SymbolTable>();

        if (node.name.equals("PROC")) {
            node theNode = node.children.get(1);

            String digits = concatenateDigits(theNode);
            System.out.println();
            System.out.println(node.children.get(0).name + " : " + digits);

            STProc.add(new SymbolTable(node.id, node.children.get(0).name + digits, scopeID2, parentProcName));

            System.out.println();
            System.out.println();

            parentProcName = node.children.get(0).name + digits;
            scopeID2++;

        }
        if (node.children.size() > 0) {

            for (node child : node.children) {
                STProc.addAll(GOProcedureNames(child, scopeID2, parentProcName));
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
                    System.out.println();
                    System.out.println(node.children.get(ii).children.get(0).name + " : " + digits);
                    // search for nodename in the symbol table if it is there then do not add it
                    if (SearchForNodeName(node.children.get(ii).children.get(0).name + digits)) {
                        symbolTable.add(new SymbolTable(node.children.get(ii).id,
                                node.children.get(ii).children.get(0).name + digits, 0, "global"));
                        System.out.println();
                        System.out.println();
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
            if(!string.equals("")){//warning 
           

                writer.write( string);
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
