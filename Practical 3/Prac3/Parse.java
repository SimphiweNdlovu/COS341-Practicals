import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.io.File;
import java.io.IOException;

public class Parse {
    static int nextTokenIndex = 0;
    static lexer.token currentToken = null;
    static ArrayList<lexer.token> input = new ArrayList<lexer.token>();
    static int pos = 0;
    static int checkIfTwoD = 0;
    static int idCounter = 0;
    static Document doc;
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    static int id;
    static node root;
    /*
     * PROGR ::= ALGO PROCDEFS
     * PROCDEFS ::= , PROC PROCDEFS
     * PROCDEFS ::= ε
     * 
     * PROC ::= pDIGITS{ PROGR } // Proc can possibly have further inner Proc-Defs!
     * 
     * DIGITS ::= D MORE
     * 
     * D ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
     * MORE ::= DIGITS
     * MORE ::= ε
     * 
     * ALGO ::= INSTR COMMENT SEQ
     * 
     * SEQ ::= ; ALGO
     * SEQ ::= ε
     * 
     * INSTR ::= INPUT // From console-user
     * INSTR ::= OUTPUT // To screen
     * INSTR ::= ASSIGN
     * INSTR ::= CALL
     * INSTR ::= LOOP
     * INSTR ::= BRANCH
     * INSTR ::= h // halt
     * 
     * CALL ::= c pDIGITS // The c stands for call (whereby p DIGITS is a Proc-Name)
     * 
     * ASSIGN ::= NUMVAR :=NUMEXPR // Type-correctness is hard-coded in the grammar
     * ASSIGN ::= BOOLVAR:=BOOLEXPR
     * ASSIGN ::= STRINGV :=STRI
     * LOOP ::= w(BOOLEXPR){ALGO} // The w stands for while
     * BRANCH ::= i(BOOLEXPR)t{ALGO}ELSE // The i stands for if, the t stands for
     * then
     * ELSE ::= e {ALGO} // The e stands for else
     * ELSE ::= ε
     * NUMVAR ::= nDIGITS // Type n, for numeric, is already hard-coded into the
     * syntax
     * BOOLVAR ::= bDIGITS // Type b, for boolean, is already hard-coded into the
     * syntax
     * STRINGV ::= sDIGITS // Type s, for stringish, is already hard-coded into the
     * syntax
     * NUMEXPR ::= a ( NUMEXPR , NUMEXPR ) // The a stands for addition
     * NUMEXPR ::= m (NUMEXPR , NUMEXPR) // The m stands for multiplication
     * NUMEXPR ::= d ( NUMEXPR , NUMEXPR ) // The d stands for division
     * NUMEXPR ::= NUMVAR
     * NUMEXPR ::= DECNUM // Decimal number with maximally two Digits behind the dot
     * 
     * DECNUM ::= 0.00 | NEG | POS
     * 
     * NEG ::= ‒POS
     * POS ::= INT.DD // Two Digits behind the dot: Digit D as defined above
     * INT ::= 1MORE | 2MORE | 3MORE | 4MORE | ... | 8MORE | 9MORE
     * BOOLEXPR ::= LOGIC
     * BOOLEXPR ::= CMPR // Comparisons of numbers (with true or false as outcomes)
     * LOGIC ::= BOOLVAR
     * LOGIC ::= T | F // True, False: the Boolean constants
     * LOGIC ::= ^ ( BOOLEXPR , BOOLEXPR ) // And-Conjunction
     * LOGIC ::= v ( BOOLEXPR , BOOLEXPR ) // Or-Disjunction
     * LOGIC ::= ! ( BOOLEXPR ) // Not-Negation
     * CMPR ::= E ( NUMEXPR , NUMEXPR ) // Equality-Comparison for Numbers
     * CMPR ::= < ( NUMEXPR , NUMEXPR ) // Lesser-Comparison for Numbers
     * CMPR ::= > ( NUMEXPR , NUMEXPR ) // Larger-Comparison for Numbers
     * 
     * STRI ::= "CCCCCCCCCCCCCCC" // Short string of constant length 15
     * C ::= all the usual ASCII keyboard characters, including the blank_space!
     * 
     * COMMENT ::= *CCCCCCCCCCCCCCC* // Short text of constant length 15
     * COMMENT ::= ε // optional
     * 
     * INPUT ::= g NUMVAR // The g stands for getting a number from the user
     * OUTPUT ::= TEXT | VALUE
     * VALUE ::= o NUMVAR // The o stands for output
     * TEXT ::= r STRINGV // The r stands for response (to the user)
     */
    node GoParse(ArrayList<lexer.token> arrayList) {
        input = arrayList;
        // print out contents of input
        // for(int i=0;i<input.size();i++){
        // System.out.println("index: "+i+" __________________"+ input.get(i).contents);
        // }
        // input.add
        // System.out.println("GoParse");
        try {
            // DocumentBuilder builder = factory.newDocumentBuilder();
            // doc = builder.newDocument();
            // Parse the program and build the XML tree
            node programNode = null;

            progr(programNode);
            if (pos != input.size()) {

                error("end of a file");
            }
            PRINTTREE(root);
            // Output the XML tree to a file
            // TransformerFactory transformerFactory = TransformerFactory.newInstance();
            // Transformer transformer = transformerFactory.newTransformer();
            // transformer.setOutputProperty(OutputKeys.INDENT, "yes"); // set indentation to yes
            // DOMSource source = new DOMSource(doc);
            // StreamResult result = new StreamResult(new File("output.xml"));
            // transformer.transform(source, result);
            System.out.println("Program successfully parsed!");
            return root;
        
        } catch (Exception e) {
            System.out.println("Parsing error: " + e.getMessage());
        }

        return null;
    }
private void PRINTTREE(node node) {
    if(node==null){
        return;
    }
    if(node.type.equals("Non-Terminal")){
        
        System.out.println("name: "+node.name+" id: "+node.id +" childrea: ");
        for(int i=0;i<node.children.size();i++){
            System.out.print(node.children.get(i).id +" ");
        }
        System.out.println();

        for(int ii=0;ii<node.children.size();ii++){
            PRINTTREE(node.children.get(ii));
        }


    }
    else if(node.type.equals("Terminal")){
        System.out.println(" terminal: "+ node.name);
        PRINTTREE(null);
    }


    }
    // To do must remove the hult function
    private static void progr(node parent) {
      
        if (pos >= input.size()) {
            error("g or  o  or n  or b or s or c or w or i or h");
        }
        if(parent==null){
            root=new node("PROGR");
            root.type="Non-Terminal";
            root.setId(id++);
            algo(root);
            procdefs(root);
          
        }else{
            node progr=createNode(parent, "PROGR");

            algo(progr);
            procdefs(progr);
        }
        // node progr = createNode(parent, "PROGR");

       
    }

    private static void algo(node parent) {
        if (pos >= input.size()) {
            error("g or  o  or n  or b or s or c or w or i or h");
        }

        node algo = createNode(parent, "ALGO");

        instr(algo);
        comment(algo);
        seq(algo);
    }

    private static void instr(node parent) {
        if (pos >= input.size()) {
            error("g or  o  or n  or b or s or c or w or i or h");
        }

        node instr = createNode(parent, "INSTR");

        if (pos < input.size() && (input.get(pos).contents).equals("g")) {// g NUMVAR // The g stands for getting a
                                                                          // number from
            // the user
            input(instr); // g NUMVAR
        } else if (pos < input.size()
                && ((input.get(pos).contents).equals("o") || (input.get(pos).contents).equals("r"))) {// o
            // NUMVAR
            // , r
            // STRINGV
            output(instr);// TEXT | VALUE
        } else if (pos < input.size() && ((input.get(pos).contents).equals("n") || (input.get(pos).contents).equals("b")
                || (input.get(pos).contents).equals("s"))) {// n DIGITS
            assign(instr);// NUMVAR :=NUMEXPR
        } else if (pos < input.size() && (input.get(pos).contents).equals("c")) {
            call(instr);// c pDIGITS
        } else if (pos < input.size() && (input.get(pos).contents).equals("w")) {
            loop(instr);// w(BOOLEXPR){ALGO}
        } else if (pos < input.size() && (input.get(pos).contents).equals("i")) {
            branch(instr);// i(BOOLEXPR)t{ALGO}ELSE
        } else if (pos < input.size() && (input.get(pos).contents).equals("h")) {
           
            if (pos < input.size() && (input.get(pos).contents).equals("h")) {
            
                instr = addChildTerminalID(instr,input.get(pos).contents);
                nexttoken();
             
            }
        } else {
            error("g or  o  or n  or b or s or c or w or i or h");
        }
    }

    private static void output(node parent) {

        if (pos >= input.size()) {
            error(" o or r ");
        }
        node output = createNode(parent, "OUTPUT");

        if (pos < input.size() && input.get(pos).contents.equals("o")) { // value
            
            VALUE(output);
      
        } else if (pos < input.size() && input.get(pos).contents.equals("r")) {// text
            
            TEXT(output);
        } else {
            error(" o or r ");
        }
    }

    private static void TEXT(node parent) {
        if (pos >= input.size()) {
            error(" r ");
        }
        node TEXT = createNode(parent, "TEXT");
      
        TEXT = addChildTerminalID(TEXT,input.get(pos).contents);
        nexttoken();
            
        STRINGV(TEXT);
    }
    private static void VALUE(node parent) {
        if (pos >= input.size()) {
            error(" o ");
        }
        node VALUE = createNode(parent, "VALUE");
      
        VALUE = addChildTerminalID(VALUE,input.get(pos).contents);
        nexttoken();

        NUMVAR(VALUE);
    }
    private static void STRINGV(node parent) {
        if (pos >= input.size()) {
            error(" s ");
        }

        node STRINGV = createNode(parent, "STRINGV");

        if (pos < input.size() && input.get(pos).contents.equals("s")) {
  
            STRINGV = addChildTerminalID(STRINGV,input.get(pos).contents);
            nexttoken();
         
            digits(STRINGV);
        } else {
            error(" s ");
        }
    }

    private static void input(node parent) {
        if (pos >= input.size()) {
            error(" g ");
        }

        node INPUT = createNode(parent, "INPUT");

        if (pos < input.size() && input.get(pos).contents.equals("g")) {
          
            INPUT = addChildTerminalID(INPUT,input.get(pos).contents);
            nexttoken();
         

            NUMVAR(INPUT);
        } else {
            error(" g ");
        }
    }

    private static void NUMVAR(node parent) {

        if (pos >= input.size()) {
            error(" n ");
        }
        node NUMVAR = createNode(parent, "NUMVAR");

        if (pos < input.size() && input.get(pos).contents.equals("n")) {
         
            NUMVAR = addChildTerminalID(NUMVAR,input.get(pos).contents);
            nexttoken();
         
            digits(NUMVAR);
        } else {
            error(" n ");
        }
    }

    // { 0 to 9 .

    private static void digits(node parent) {

        if (pos >= input.size()) {
            error("0 ... 9");
        }
        node DIGITS = createNode(parent, "DIGITS");

        // any numbur from 0 to 9
        D(DIGITS);
        MORE(DIGITS);

    }

    private static void MORE(node parent) {
        if (pos >= input.size()) {
            return;
        }

        if (pos < input.size() && ((input.get(pos).contents).equals("{") || (input.get(pos).contents).equals(".")
                || (input.get(pos)._class).equals("Comment") || (input.get(pos).contents).equals(";")
                || (input.get(pos).contents).equals(",") || (input.get(pos).contents).equals("}")
                || (input.get(pos).contents).equals(":") || (input.get(pos).contents).equals(")"))) { // epsilon follow
                                                                                                       // of more = [{ .
                                                                                                       // 0 to 9 := ] //
                                                                                                       // := since
            // numvar calls digits
            return;

        }
        node MORE = createNode(parent, "MORE");

        if (pos < input.size() && isDigit(input.get(pos).contents) == true) {

            digits(MORE);
        } else {
            error(" 0 to 9   or { or . or :  or Comment  or ; or , or } or ) or end of file");
        }
    }

    private static void D(node parent) {
        if (pos >= input.size()) {
            error("0 ... 9");
        }

        node D = createNode(parent, "D");

        if (pos < input.size() && isDigit(input.get(pos).contents) == true) {
    
            D = addChildTerminalID(D,input.get(pos).contents);
            nexttoken();
         

        } else if (pos == input.size()) {
            return;
        } else {
            error("0 ... 9");
        }
    }

    // function that checks if the string any numbur from 0 to 9
    public static boolean isDigit(String str) {
        

        return str.length() == 1 && Character.isDigit(str.charAt(0));
    }

    private static void seq(node parent) {
        if (pos >= input.size()) {
            return;
        }

        if ((pos < input.size()
                && ((input.get(pos).contents).equals("}") || (input.get(pos).contents).equals(",")))) {
            return;

        }
        node SEQ = createNode(parent, "SEQ");

        if (pos < input.size() && (input.get(pos).contents).equals(";")) {
       
            SEQ = addChildTerminalID(SEQ,input.get(pos).contents);
            nexttoken();
         
            algo(SEQ);
        } else {
            error("; or } or ,");
        }
    }

    private static void procdefs(node parent) {
        if (pos >= input.size()) {
            return;
        }

        if ((pos < input.size() &&
                (input.get(pos).contents).equals("}"))) {

            return;

        }
        node PROCDEFS = createNode(parent, "PROCDEFS");

        if (pos < input.size() && ((input.get(pos).contents).equals(","))) {
       
            PROCDEFS = addChildTerminalID(PROCDEFS,input.get(pos).contents);
            nexttoken();
         
            proc(PROCDEFS);
            procdefs(PROCDEFS);
        } else {
            error(", or } or end of file");
        }
    }

    private static void comment(node parent) {
        if (pos >= input.size()) {
            return;
        }

        // System.out.println("comment---------------------------------------");
        if ((pos < input.size() && ((input.get(pos).contents).equals(";") // follow set of comment
                || (input.get(pos).contents).equals(",")
                || (input.get(pos).contents).equals("}")))) {

            return;

        }

        node COMMENT = createNode(parent, "COMMENT");

        if (pos < input.size() && (input.get(pos)._class).equals("Comment")) {
         
            COMMENT = addChildTerminalID(COMMENT,input.get(pos).contents);
            nexttoken();
         
        } else {
            error(" Comment or ; or , or }  or end of file");
        }
    }

    private static void proc(node parent) {
        if (pos >= input.size()) {
           error("p");
        }

        node PROC = createNode(parent, "PROC");

        if (pos < input.size() && (input.get(pos).contents).equals("p")) {
          
            PROC = addChildTerminalID(PROC,input.get(pos).contents);
            nexttoken();
         

            digits(PROC);
            if (pos < input.size() && (input.get(pos).contents).equals("{")) {
             
                PROC = addChildTerminalID(PROC,input.get(pos).contents);
                nexttoken();
               
                progr(PROC);
                if (pos < input.size() && (input.get(pos).contents).equals("}")) {
                 
                    PROC = addChildTerminalID(PROC,input.get(pos).contents);
                    nexttoken();
                  
                } else {
                    error("}");
                }
            } else {
                error("{");
            }
        } else {
            error("p");
        }
    }

    private static void assign(node parent) {
        if (pos >= input.size()) {
            error(" n or b or s");
        }

        node ASSIGN = createNode(parent, "ASSIGN");

        if (pos < input.size() && (input.get(pos).contents).equals("n")) {// n DIGITS
          
            NUMVAR(ASSIGN);
        
            if (pos < input.size() && (input.get(pos).contents.equals(":"))) {
         
                ASSIGN = addChildTerminalID(ASSIGN,input.get(pos).contents);
                nexttoken();
               if(pos < input.size() && (input.get(pos).contents.equals("="))) {
             
                ASSIGN = addChildTerminalID(ASSIGN,input.get(pos).contents);
                nexttoken();
                NUMEXPR(ASSIGN);
               }else {
                   error("=");
               }
             
            } else {
                error(" : ");
            }

        } else if (pos < input.size() && (input.get(pos).contents.equals("b"))) {
            BOOLVAR(ASSIGN);
            if (pos < input.size() && (input.get(pos).contents.equals(":"))) {
            
                ASSIGN = addChildTerminalID(ASSIGN,input.get(pos).contents);
                nexttoken();
              if(pos < input.size() && (input.get(pos).contents.equals("="))) {
               
                ASSIGN = addChildTerminalID(ASSIGN,input.get(pos).contents);
                nexttoken();
                BOOLEXPR(ASSIGN);
              }else {
                  error("=");
              }

               
            } else {
                error(" :");
            }

        } else if (pos < input.size() && (input.get(pos).contents.equals("s"))) {
            STRINGV(ASSIGN);

            if (pos < input.size() && (input.get(pos).contents.equals(":"))) {
             
                ASSIGN = addChildTerminalID(ASSIGN,input.get(pos).contents);
                nexttoken();
                if(pos < input.size() && (input.get(pos).contents.equals("="))) {
              
                ASSIGN = addChildTerminalID(ASSIGN,input.get(pos).contents);
                nexttoken();
                    STRI(ASSIGN);
                }
                else {
                    error("=");
                }

                
            } else {
                error(" : ");
            }

        } else {
            error(" n or b or s");
        }
    }

    private static void STRI(node parent) {
        if (pos >= input.size()) {
            error(" Shortstring");
        }
        node STRI = createNode(parent, "STRI");

        if (pos < input.size() && (input.get(pos)._class.equals("Shotstring"))) {
           
            STRI = addChildTerminalID(STRI,input.get(pos).contents);
            nexttoken();
         
        } else {
            error(" Shortstring");
        }
    }

    private static void BOOLEXPR(node parent) {
        if (pos >= input.size()) {
            error(" b or T or F or ^ or v or ! or E or < or >");
        }

        node BOOLEXPR = createNode(parent, "BOOLEXPR");

        if (pos < input.size() && ((input.get(pos).contents.equals("b")) || (input.get(pos).contents.equals("T"))
                || (input.get(pos).contents.equals("F")) || (input.get(pos).contents.equals("^"))
                || (input.get(pos).contents.equals("v")) || (input.get(pos).contents.equals("!")))) {
            LOGIC(BOOLEXPR);

        } else if (pos < input.size() && ((input.get(pos).contents.equals("E")) || (input.get(pos).contents.equals("<"))
                || (input.get(pos).contents.equals(">")))) {
            CMPR(BOOLEXPR);

        } else {
            error(" b or T or F or ^ or v or ! or E or < or >");
        }

    }

    private static void CMPR(node parent) {
        if (pos >= input.size()) {
            error("E or < or >");
        }

        node CMPR = createNode(parent, "CMPR");

        if (pos < input.size() && ((input.get(pos).contents.equals("E")))) {
      
            CMPR = addChildTerminalID(CMPR,input.get(pos).contents);
            nexttoken();
         
            if (pos < input.size() && (input.get(pos).contents.equals("("))) {
            
                CMPR = addChildTerminalID(CMPR,input.get(pos).contents);
                nexttoken();
             
                NUMEXPR(CMPR);

                if (pos < input.size() && (input.get(pos).contents.equals(","))) {
                  
                    CMPR = addChildTerminalID(CMPR,input.get(pos).contents);
                    nexttoken();
               
                    NUMEXPR(CMPR);
                    if (pos < input.size() && (input.get(pos).contents.equals(")"))) {
                    
                        CMPR = addChildTerminalID(CMPR,input.get(pos).contents);
                        nexttoken();
                      
                    } else {
                        error(" ) ");
                    }

                } else {
                    error(" , ");
                }

            } else {
                error(" ( ");
            }

        } else if (pos < input.size() && (input.get(pos).contents.equals("<"))) {

            CMPR = addChildTerminalID(CMPR,input.get(pos).contents);
            nexttoken();
         
            if (pos < input.size() && (input.get(pos).contents.equals("("))) {
       
                CMPR = addChildTerminalID(CMPR,input.get(pos).contents);
                nexttoken();
            
                NUMEXPR(CMPR);

          
                if (pos < input.size() && (input.get(pos).contents.equals(","))) {
                 
                    CMPR = addChildTerminalID(CMPR,input.get(pos).contents);
                    nexttoken();
                    
                    NUMEXPR(CMPR);
                    if (pos < input.size() && (input.get(pos).contents.equals(")"))) {
                    
                        CMPR = addChildTerminalID(CMPR,input.get(pos).contents);
                        nexttoken();
                       
                    } else {
                        error(" ) ");
                    }

                } else {
                    error(" , ");
                }

            } else {
                error(" ( ");
            }

        } else if (pos < input.size() && (input.get(pos).contents.equals(">"))) {
       
            CMPR = addChildTerminalID(CMPR,input.get(pos).contents);
            nexttoken();
         
            if (pos < input.size() && (input.get(pos).contents.equals("("))) {
     
                CMPR = addChildTerminalID(CMPR,input.get(pos).contents);
                nexttoken();
             
                NUMEXPR(CMPR);

         
                if (pos < input.size() && (input.get(pos).contents.equals(","))) {
                   
                    CMPR = addChildTerminalID(CMPR,input.get(pos).contents);
                    nexttoken();
             
                    NUMEXPR(CMPR);
                    if (pos < input.size() && (input.get(pos).contents.equals(")"))) {
                     
                        CMPR = addChildTerminalID(CMPR,input.get(pos).contents);
                        nexttoken();
                     
                    } else {
                        error(" ) ");
                    }

                } else {
                    error(" , ");
                }

            } else {
                error(" ( ");
            }

        } else {
            error("E or < or >");
        }
    }

    private static void LOGIC(node parent) {
        if (pos >= input.size()) {
            error("b or T or F or ^ or v or !");
        }

        node LOGIC = createNode(parent, "LOGIC");

        if (pos < input.size() && ((input.get(pos).contents.equals("b")))) {
            BOOLVAR(LOGIC);

        } else if ((input.get(pos).contents.equals("T"))) {
        
            LOGIC = addChildTerminalID(LOGIC,input.get(pos).contents);
            nexttoken();
         

        } else if (pos < input.size() && (input.get(pos).contents.equals("F"))) {
         
            LOGIC = addChildTerminalID(LOGIC,input.get(pos).contents);
            nexttoken();
         

        } else if (pos < input.size() && (input.get(pos).contents.equals("^"))) {
           
            LOGIC = addChildTerminalID(LOGIC,input.get(pos).contents);
            nexttoken();
         
            if (pos < input.size() && (input.get(pos).contents.equals("("))) {
              
                LOGIC = addChildTerminalID(LOGIC,input.get(pos).contents);
                nexttoken();
           
                BOOLEXPR(LOGIC);

                if (pos < input.size() && (input.get(pos).contents.equals(","))) {
        
                    LOGIC = addChildTerminalID(LOGIC,input.get(pos).contents);
                    nexttoken();
                
                    BOOLEXPR(LOGIC);
                    if (pos < input.size() && (input.get(pos).contents.equals(")"))) {
                   
                        LOGIC = addChildTerminalID(LOGIC,input.get(pos).contents);
                        nexttoken();
                   
                    } else {
                        error(" ) ");
                    }

                } else {
                    error(" , ");
                }

            } else {
                error(" ( ");
            }

        } else if (pos < input.size() && (input.get(pos).contents.equals("v"))) {

            LOGIC = addChildTerminalID(LOGIC,input.get(pos).contents);
            nexttoken();
         
            if (pos < input.size() && (input.get(pos).contents.equals("("))) {
 
                LOGIC = addChildTerminalID(LOGIC,input.get(pos).contents);
                nexttoken();
         
                BOOLEXPR(LOGIC);

                if (pos < input.size() && (input.get(pos).contents.equals(","))) {
      
                    LOGIC = addChildTerminalID(LOGIC,input.get(pos).contents);
                    nexttoken();
              
                    BOOLEXPR(LOGIC);
                    if (pos < input.size() && (input.get(pos).contents.equals(")"))) {
                    
                        LOGIC = addChildTerminalID(LOGIC,input.get(pos).contents);
                        nexttoken();
                    
                    } else {
                        error(" ) ");
                    }

                } else {
                    error(" , ");
                }

            } else {
                error(" ( ");
            }

        } else if (pos < input.size() && (input.get(pos).contents.equals("!"))) {

            LOGIC = addChildTerminalID(LOGIC,input.get(pos).contents);
            nexttoken();
         
            if (pos < input.size() && (input.get(pos).contents.equals("("))) {
    
                LOGIC = addChildTerminalID(LOGIC,input.get(pos).contents);
                nexttoken();
              
                BOOLEXPR(LOGIC);

                if (pos < input.size() && (input.get(pos).contents.equals(")"))) {
                 
                    LOGIC = addChildTerminalID(LOGIC,input.get(pos).contents);
                    nexttoken();
               
                } else {
                    error(" ) ");
                }

            } else {
                error(" ( ");
            }

        } else {
            error("b or T or F or ^ or v or !");
        }
    }

    private static void BOOLVAR(node parent) {
        if (pos >= input.size()) {
            error(" b ");
        }
        node BOOLVAR = createNode(parent, "BOOLVAR");

        if (pos < input.size() && input.get(pos).contents.equals("b")) {

            BOOLVAR = addChildTerminalID(BOOLVAR,input.get(pos).contents);
            nexttoken();
         
            digits(BOOLVAR);
        } else {
            error(" b ");
        }
    }

    private static void NUMEXPR(node parent) {
        if (pos >= input.size()) {
            error(" a or m or d or n or  0.00 or - or 1 to 9");
        }

        node NUMEXPR = createNode(parent, "NUMEXPR");

        if (pos < input.size() && (input.get(pos).contents).equals("a")) {// n DIGITS
         
            NUMEXPR = addChildTerminalID(NUMEXPR,input.get(pos).contents);
            nexttoken();
         
            if (pos < input.size() && (input.get(pos).contents.equals("("))) {
    
                NUMEXPR = addChildTerminalID(NUMEXPR,input.get(pos).contents);
                nexttoken();
            
                NUMEXPR(NUMEXPR);

          
                if (pos < input.size() && (input.get(pos).contents.equals(","))) {
                 
                    NUMEXPR = addChildTerminalID(NUMEXPR,input.get(pos).contents);
                    nexttoken();
              
                    NUMEXPR(NUMEXPR);
                    if (pos < input.size() && (input.get(pos).contents.equals(")"))) {
                
                        NUMEXPR = addChildTerminalID(NUMEXPR,input.get(pos).contents);
                        nexttoken();
                      
                    } else {
                        error(" ) ");
                    }

                } else {
                    error(" , ");
                }

            } else {
                error(" ( ");
            }

        } else if (pos < input.size() && (input.get(pos).contents.equals("m"))) {
         
            NUMEXPR = addChildTerminalID(NUMEXPR,input.get(pos).contents);

            nexttoken();
         
            if (pos < input.size() && (input.get(pos).contents.equals("("))) {
            
                NUMEXPR = addChildTerminalID(NUMEXPR,input.get(pos).contents);
                nexttoken();
             
                NUMEXPR(NUMEXPR);


                if (pos < input.size() && (input.get(pos).contents.equals(","))) {
               
                    NUMEXPR = addChildTerminalID(NUMEXPR,input.get(pos).contents);
                    nexttoken();
                  
                    NUMEXPR(NUMEXPR);
                    if (pos < input.size() && (input.get(pos).contents.equals(")"))) {
                      
                        NUMEXPR = addChildTerminalID(NUMEXPR,input.get(pos).contents);
                        nexttoken();
                       
                    } else {
                        error(" ) ");
                    }

                } else {
                    error(" , ");
                }

            } else {
                error(" ( ");
            }

        } else if (pos < input.size() && (input.get(pos).contents.equals("d"))) {
        
            NUMEXPR = addChildTerminalID(NUMEXPR,input.get(pos).contents);

            nexttoken();
         
            if (pos < input.size() && (input.get(pos).contents.equals("("))) {
           
                NUMEXPR = addChildTerminalID(NUMEXPR,input.get(pos).contents);
                nexttoken();
               
                NUMEXPR(NUMEXPR);

               
                if (pos < input.size() && (input.get(pos).contents.equals(","))) {
            
                    NUMEXPR = addChildTerminalID(NUMEXPR,input.get(pos).contents);
                    nexttoken();
                   
                    NUMEXPR(NUMEXPR);
                    if (pos < input.size() && (input.get(pos).contents.equals(")"))) {
                      
                        NUMEXPR = addChildTerminalID(NUMEXPR,input.get(pos).contents);
                        nexttoken();
                       
                    } else {
                        error(" ) ");
                    }

                } else {
                    error(" , ");
                }

            } else {
                error(" ( ");
            }

        } else if (pos < input.size() && (input.get(pos).contents.equals("n"))) {// NUMVAR

            // NUMEXPR = addChildTerminalID(NUMEXPR,input.get(pos).contents);
            // nexttoken();
         
            // digits(NUMEXPR);
            NUMVAR(NUMEXPR);

        } else if (pos < input.size() && ( (input.get(pos).contents.equals("-")) || (Character.isDigit(input.get(pos).contents.charAt(0))))) {
            DECNUM(NUMEXPR);

        } else {
            error(" a or m or d or n or  0.00 or - or 1 to 9");
        }
    }

    private static void DECNUM(node parent) {
        if (pos >= input.size()) {
            error("0.00 or - or 1 to 9");
        }

        node DECNUM = createNode(parent, "DECNUM");

        if (pos < input.size() && ((input.get(pos).contents.equals("0")))) {
        
            DECNUM = addChildTerminalID(DECNUM,input.get(pos).contents);
            nexttoken();
            if(pos < input.size() && ((input.get(pos).contents.equals(".")))){
            
                DECNUM = addChildTerminalID(DECNUM,input.get(pos).contents);
                nexttoken();
               
                if(pos < input.size() && ((input.get(pos).contents.equals("0")))){
              
                    DECNUM = addChildTerminalID(DECNUM,input.get(pos).contents);
                    nexttoken();
                    if(pos < input.size() && ((input.get(pos).contents.equals("0")))){
           
                        DECNUM = addChildTerminalID(DECNUM,input.get(pos).contents);
                        nexttoken();
                    }
                    else{
                        error("0");
                    }
                }
                else{
                    error("0");
                }
            }
            else{
                error(" . ");
            }
         

        } else if (pos < input.size() && ((input.get(pos).contents.equals("-")))) {
           
            NEG(DECNUM); 
         

        } else if (pos < input.size() && ((isOneorNine(input.get(pos).contents)))) {

            POS(DECNUM);
        }
        else{
            error("0.00 or - or 1 to 9");
        }
    }

    private static void NEG(node parent) {
        if (pos >= input.size()) {
            error(" - ");
        }


        node NEG = createNode(parent, "NEG");
      
        NEG = addChildTerminalID(NEG,input.get(pos).contents);
        nexttoken();
        POS(NEG);
    }
    private static void POS(node parent) {
        if (pos >= input.size()) {
            error(" 1 ... 9");
        }

        node POS = createNode(parent, "POS");

        INT(POS);
        
        if (pos < input.size() && ((input.get(pos).contents.equals(".")))) {

            POS = addChildTerminalID(POS,input.get(pos).contents);
            nexttoken();
         

            D(POS);
            D(POS);

        } else {
            error(".");
        }

    }

    private static void INT(node parent) {
        if (pos >= input.size()) {
            error(" 1 ... 9");
        }

        node INT = createNode(parent, "INT");

        if (pos < input.size() && ((isOneorNine(input.get(pos).contents)))) {


            INT = addChildTerminalID(INT,input.get(pos).contents);
            nexttoken();
         
            MORE(INT);

        } else {
            error(" 1 ... 9");
        }
    }

    private static boolean isOneorNine(String contents) {
        

        boolean status = false;
        if (contents.length() == 1 && Character.isDigit(contents.charAt(0)) && !contents.equals("0")) {
            status = true;
        }
        return status;
    }

    private static void call(node parent) {
        if (pos >= input.size()) {
            error(" c ");
        }

        node CALL = createNode(parent, "CALL");

        if (pos < input.size() && (((input.get(pos).contents).equals("c")))) {
   
            CALL = addChildTerminalID(CALL,input.get(pos).contents);
            nexttoken();
         
            if (pos < input.size() && (((input.get(pos).contents).equals("p")))) {
           
                CALL = addChildTerminalID(CALL,input.get(pos).contents);
                nexttoken();
                
                digits(CALL);

            } else {
                error(" p ");
            }

        } else {
            error(" c ");
        }
    }

    private static void loop(node parent) {
        if (pos >= input.size()) {
            error("w");
        }

        node LOOP = createNode(parent, "LOOP");

        if (pos < input.size() && (input.get(pos).contents).equals("w")) {

            LOOP = addChildTerminalID(LOOP,input.get(pos).contents);
            nexttoken();
         
            if (pos < input.size() && (input.get(pos).contents.equals("("))) {
             
                LOOP = addChildTerminalID(LOOP,input.get(pos).contents);
                nexttoken();
               
                BOOLEXPR(LOOP);

                if (pos < input.size() && (input.get(pos).contents.equals(")"))) {
             
                    LOOP = addChildTerminalID(LOOP,input.get(pos).contents);
                    nexttoken();
                   
                    if (pos < input.size() && (input.get(pos).contents.equals("{"))) {
                     
                        LOOP = addChildTerminalID(LOOP,input.get(pos).contents);
                        nexttoken();
                       
                        algo(LOOP);

                        if (pos < input.size() && (input.get(pos).contents.equals("}"))) {
                         
                            LOOP = addChildTerminalID(LOOP,input.get(pos).contents);
                            nexttoken();
                            
                        } else {
                            error(" } ");
                        }

                    } else {
                        error(" { ");
                    }

                } else {
                    error(" ) ");
                }

            } else {
                error(" ( ");
            }
        } else {
            error("w");
        }
    }

    private static void branch(node parent) {
        if (pos >= input.size()) {
            error("i");
        }
        node BRANCH = createNode(parent, "BRANCH");

        if (pos < input.size() && (input.get(pos).contents).equals("i")) {
          
            BRANCH = addChildTerminalID(BRANCH,input.get(pos).contents);
            nexttoken();
         
            if (pos < input.size() && (input.get(pos).contents.equals("("))) {

                BRANCH = addChildTerminalID(BRANCH,input.get(pos).contents);
                nexttoken();
                
                BOOLEXPR(BRANCH);

                if (pos < input.size() && (input.get(pos).contents.equals(")"))) {
             
                    BRANCH = addChildTerminalID(BRANCH,input.get(pos).contents);
                    nexttoken();
                   
                    if (pos < input.size() && (input.get(pos).contents.equals("t"))) {
                
                        BRANCH = addChildTerminalID(BRANCH,input.get(pos).contents);
                        nexttoken();
                        
                        if (pos < input.size() && (input.get(pos).contents.equals("{"))) {
                          
                            BRANCH = addChildTerminalID(BRANCH,input.get(pos).contents);
                            nexttoken();
                           
                            algo(BRANCH);

                            if (pos < input.size() && (input.get(pos).contents.equals("}"))) {
                             
                                BRANCH = addChildTerminalID(BRANCH,input.get(pos).contents);
                                nexttoken();
                               

                                ELSE(BRANCH);
                            } else {
                                error(" } ");
                            }

                        } else {
                            error(" { ");
                        }

                    } else {
                        error("t");
                    }

                } else {
                    error(" ) ");
                }

            } else {
                error(" ( ");
            }
        } else {
            error("i");
        }
    }

    private static void ELSE(node parent) {
        if (pos >= input.size()) {
            return;
        }

        if (pos < input.size() && ((input.get(pos)._class).equals("Comment") || (input.get(pos).contents).equals(";")
                || (input.get(pos).contents).equals(",")
                || (input.get(pos).contents).equals("}"))) {// follow of else [ * ; , } $]
            return;

        }
        node ELSE = createNode(parent, "ELSE");

        if (pos < input.size() && (input.get(pos).contents).equals("e")) {
          
            ELSE = addChildTerminalID(ELSE,input.get(pos).contents);

            nexttoken();
         

            if (pos < input.size() && (input.get(pos).contents.equals("{"))) {
           
                ELSE = addChildTerminalID(ELSE,input.get(pos).contents);
                nexttoken();
                
                algo(ELSE);

                if (pos < input.size() && (input.get(pos).contents.equals("}"))) {
                
                    ELSE = addChildTerminalID(ELSE,input.get(pos).contents);
                    nexttoken();
                   

                } else {
                    error(" } ");
                }

            } else {
                error(" { ");
            }
        } else {
            error("e or Comment or ; or , or } or end of file");
        }
    }



    private static void nexttoken() {

        if (pos < (input.size())) {
            // System.out.println("___________________________" + input.get(pos).contents);
            pos++;
        }

    }

    // private static node addChildTerminalID(node parent) {
        
    //     // If the parent has children, append the new child ID to the children attribute
    //     String parentChildren = parent.getAttribute("children");
    //     if (!parentChildren.isEmpty()) {
    //         parentChildren += " ";
    //     }
    //     parentChildren += idCounter++;
    //     parent.setAttribute("children", parentChildren);
    //     return parent;
    // }
    private static node addChildTerminalID(node parent,String name) {
        
        node terminal = new node(name,"Terminal");
        terminal.setId(id++);
        parent.addChild(terminal);
       
        return parent;
    }
    private static node createNode(node parent, String name) {
        node elem = new node(name,"Non-Terminal");
        elem.setId(id++);
        parent.addChild(elem);
        return elem;
    }


    // private static node createNode(node parent, String name) {
    //     node elem = doc.createNode(name);

    //     elem.setAttribute("id", "" + idCounter++);

    //     // If the parent has children, append the new child ID to the children attribute
    //     String parentChildren = parent.getAttribute("children");
    //     if (!parentChildren.isEmpty()) {
    //         parentChildren += " ";
    //     }
    //     parentChildren += elem.getAttribute("id");
    //     parent.setAttribute("children", parentChildren);

    //     parent.appendChild(elem);
    //     return elem;
    // }

    private static void error(String expected) {
        if (pos >= input.size()) {
            System.out.println("Syntax error expected : " + expected + " but got end of a file");
            System.exit(1);
         }
        System.out.println("Syntax error at line: " + input.get(pos).lineNumber + " expected: " + expected
                + " but got token: " + input.get(pos).contents);
        System.exit(1);
    }
}
