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
        ArrayList<SymbolTable> proc= GOProcedureNames(node, 0, "global");
        // add what is in proc to symbol table
        for(int i=0;i<proc.size();i++){
            symbolTable.add(proc.get(i));
        }
       
        visualizeSymbolTable(symbolTable, "symbolTable.html");
    }
    private static   ArrayList<SymbolTable>  GOProcedureNames(node node, int scopeID2, String parentProcName) {
        // if(node == null) {
        //     return null;
        // }
         ArrayList<SymbolTable> STProc = new ArrayList<SymbolTable>();
        // StringBuilder sb = new StringBuilder();

                if(node.name.equals("PROC") ){
                    node theNode = node.children.get(1);
    
                    String digits=concatenateDigits(theNode);
                    System.out.println();
                    System.out.println(node.children.get(0).name+" : "+digits);

                    
                    STProc.add(new SymbolTable(node.id, node.children.get(0).name+digits, scopeID2, parentProcName));
                    // sb.append(" node id: "+node.id+"    node name:       "+ node.children.get(0).name+digits+"        node scope :      "+ scopeID2+"      scopename:        "+ parentProcName +"\n");
                    System.out.println();
                    System.out.println();
                    
                  
                    parentProcName=node.children.get(0).name+digits;
                    scopeID2++;
                    // GOProcedureNames(node,scopeID2,parentProcName);
                  
                }
                 if (node.children.size() > 0) {
            
                    for (node child : node.children) {
                        STProc.addAll( GOProcedureNames(child,scopeID2,parentProcName));
                    }
                }
                // else{
                //     STProc.addAll(GOProcedureNames(null,scopeID2,parentProcName)) ;
                // }
            
                // Return the concatenated string
                return STProc;
 
 
    }
    public static void GOVarNames(node node,int ScopeID,String ScopeName) {
       if(node == null) {
           return;
       }
       if(node.type.equals("Non-Terminal")){
   
        for(int ii=0;ii<node.children.size();ii++){
            if(node.children.get(ii).name.equals("NUMVAR") || node.children.get(ii).name.equals("BOOLVAR" )|| node.children.get(ii).name.equals("STRINGV")){
                node theNode = node.children.get(ii).children.get(1);

                String digits=concatenateDigits(theNode);
                System.out.println();
                System.out.println(node.children.get(ii).children.get(0).name+" : "+digits);
                //search for nodename in the symbol table if it is there then do not add it
                if(SearchForNodeName(node.children.get(ii).children.get(0).name+digits)){
                    symbolTable.add(new SymbolTable(node.children.get(ii).id, node.children.get(ii).children.get(0).name+digits, 0, "global"));
                    System.out.println();
                    System.out.println();
                }
              
                // ii=ii+1;
                GOVarNames(node.children.get(ii) ,ScopeID,ScopeName);
            }
            else{
                GOVarNames(node.children.get(ii) ,ScopeID,ScopeName);
            }
          
        }


    }
    else if(node.type.equals("Terminal")){
        // System.out.println(" terminal: "+ node.name);
        GOVarNames(null ,ScopeID,ScopeName);
    }


    }
    private static boolean SearchForNodeName(String string) {
        for(int i=0;i<symbolTable.size();i++){
            if(symbolTable.get(i).NodeName.equals(string)){
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
    
        // Recursive case: node has children, traverse them and append their names to the string
        else if (n.children.size() > 0) {
            for (node child : n.children) {
                sb.append(concatenateDigits(child));
            }
        }
    
        // Return the concatenated string
        return sb.toString();
    }

    public static void visualizeSymbolTable(ArrayList<SymbolTable> symbolT, String fileName) {
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
            writer.write("</body>\n</html>");
            writer.close();
            System.out.println("Symbol table written to file "+fileName);
        } catch (IOException e) {
            System.out.println("Error writing symbol table to file: " + e.getMessage());
        }
    }
    
    
}
// current scope is the pass in scope.
// 1for checking for sibling nodes, 2. if it calls it selfs, 3.if is a child
// 1. will go to the symbol table and check 
