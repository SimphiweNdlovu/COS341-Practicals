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
import org.w3c.dom.Element;
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
    void GoParse(ArrayList<lexer.token> arrayList) {
        input = arrayList;
        // print out contents of input
        // for(int i=0;i<input.size();i++){
        // System.out.println("index: "+i+" __________________"+ input.get(i).contents);
        // }
        // input.add
        // System.out.println("GoParse");
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.newDocument();
            // Parse the program and build the XML tree
            Element programNode = null;

            progr(programNode);
            if (pos != input.size()) {

                error("end of a file");
            }

            // Output the XML tree to a file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); // set indentation to yes
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("output.xml"));
            transformer.transform(source, result);

            System.out.println("Program successfully parsed!");
        } catch (Exception e) {
            System.out.println("Parsing error: " + e.getMessage());
        }

    }
// To do must remove the hult function
    private static void progr(Element parent) {
        Element progr=null;
        if (pos >= input.size()) {
            error("g or  o  or n  or b or s or c or w or i or h");
        }
        if(parent==null){
            progr=doc.createElement("PROGR");
            progr.setAttribute("id", "" + idCounter++);
            doc.appendChild(progr);
        }else{
            progr=createElement(parent, "PROGR");
        }
        // Element progr = createElement(parent, "PROGR");

        algo(progr);
        procdefs(progr);
    }

    private static void algo(Element parent) {
        if (pos >= input.size()) {
            error("g or  o  or n  or b or s or c or w or i or h");
        }

        Element algo = createElement(parent, "ALGO");

        instr(algo);
        comment(algo);
        seq(algo);
    }

    private static void instr(Element parent) {
        if (pos >= input.size()) {
            error("g or  o  or n  or b or s or c or w or i or h");
        }

        Element instr = createElement(parent, "INSTR");

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
                instr.appendChild(doc.createTextNode(input.get(pos).contents));
                instr = addChildTerminalID(instr);
                nexttoken();
             
            }
        } else {
            error("g or  o  or n  or b or s or c or w or i or h");
        }
    }

    private static void output(Element parent) {

        if (pos >= input.size()) {
            error(" o or r ");
        }
        Element output = createElement(parent, "OUTPUT");

        if (pos < input.size() && input.get(pos).contents.equals("o")) { // value
            
            VALUE(output);
      
        } else if (pos < input.size() && input.get(pos).contents.equals("r")) {// text
            
            TEXT(output);
        } else {
            error(" o or r ");
        }
    }

    private static void TEXT(Element parent) {
        if (pos >= input.size()) {
            error(" r ");
        }
        Element TEXT = createElement(parent, "TEXT");
        TEXT.appendChild(doc.createTextNode("r"));
        TEXT = addChildTerminalID(TEXT);
        nexttoken();
            
        STRINGV(TEXT);
    }
    private static void VALUE(Element parent) {
        if (pos >= input.size()) {
            error(" o ");
        }
        Element VALUE = createElement(parent, "VALUE");
        VALUE.appendChild(doc.createTextNode("o"));
        VALUE = addChildTerminalID(VALUE);
        nexttoken();

        NUMVAR(VALUE);
    }
    private static void STRINGV(Element parent) {
        if (pos >= input.size()) {
            error(" s ");
        }

        Element STRINGV = createElement(parent, "STRINGV");

        if (pos < input.size() && input.get(pos).contents.equals("s")) {
            STRINGV.appendChild(doc.createTextNode("s"));
            STRINGV = addChildTerminalID(STRINGV);
            nexttoken();
         
            digits(STRINGV);
        } else {
            error(" s ");
        }
    }

    private static void input(Element parent) {
        if (pos >= input.size()) {
            error(" g ");
        }

        Element INPUT = createElement(parent, "INPUT");

        if (pos < input.size() && input.get(pos).contents.equals("g")) {
            INPUT.appendChild(doc.createTextNode("g"));
            INPUT = addChildTerminalID(INPUT);
            nexttoken();
         

            NUMVAR(INPUT);
        } else {
            error(" g ");
        }
    }

    private static void NUMVAR(Element parent) {

        if (pos >= input.size()) {
            error(" n ");
        }
        Element NUMVAR = createElement(parent, "NUMVAR");

        if (pos < input.size() && input.get(pos).contents.equals("n")) {
            NUMVAR.appendChild(doc.createTextNode("n"));
            NUMVAR = addChildTerminalID(NUMVAR);
            nexttoken();
         
            digits(NUMVAR);
        } else {
            error(" n ");
        }
    }

    // { 0 to 9 .

    private static void digits(Element parent) {

        if (pos >= input.size()) {
            error("0 ... 9");
        }
        Element DIGITS = createElement(parent, "DIGITS");

        // any numbur from 0 to 9
        D(DIGITS);
        MORE(DIGITS);

    }

    private static void MORE(Element parent) {
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
        Element MORE = createElement(parent, "MORE");

        if (pos < input.size() && isDigit(input.get(pos).contents) == true) {

            digits(MORE);
        } else {
            error(" 0 to 9   or { or . or :  or Comment  or ; or , or } or ) or end of file");
        }
    }

    private static void D(Element parent) {
        if (pos >= input.size()) {
            error("0 ... 9");
        }

        Element D = createElement(parent, "D");

        if (pos < input.size() && isDigit(input.get(pos).contents) == true) {
            D.appendChild(doc.createTextNode(input.get(pos).contents));
            D = addChildTerminalID(D);
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

    private static void seq(Element parent) {
        if (pos >= input.size()) {
            return;
        }

        if ((pos < input.size()
                && ((input.get(pos).contents).equals("}") || (input.get(pos).contents).equals(",")))) {
            return;

        }
        Element SEQ = createElement(parent, "SEQ");

        if (pos < input.size() && (input.get(pos).contents).equals(";")) {
            SEQ.appendChild(doc.createTextNode(input.get(pos).contents));
            SEQ = addChildTerminalID(SEQ);
            nexttoken();
         
            algo(SEQ);
        } else {
            error("; or } or ,");
        }
    }

    private static void procdefs(Element parent) {
        if (pos >= input.size()) {
            return;
        }

        if ((pos < input.size() &&
                (input.get(pos).contents).equals("}"))) {

            return;

        }
        Element PROCDEFS = createElement(parent, "PROCDEFS");

        if (pos < input.size() && ((input.get(pos).contents).equals(","))) {
            PROCDEFS.appendChild(doc.createTextNode(input.get(pos).contents));
            PROCDEFS = addChildTerminalID(PROCDEFS);
            nexttoken();
         
            proc(PROCDEFS);
            procdefs(PROCDEFS);
        } else {
            error(", or } or end of file");
        }
    }

    private static void comment(Element parent) {
        if (pos >= input.size()) {
            return;
        }

        // System.out.println("comment---------------------------------------");
        if ((pos < input.size() && ((input.get(pos).contents).equals(";") // follow set of comment
                || (input.get(pos).contents).equals(",")
                || (input.get(pos).contents).equals("}")))) {

            return;

        }

        Element COMMENT = createElement(parent, "COMMENT");

        if (pos < input.size() && (input.get(pos)._class).equals("Comment")) {
            COMMENT.appendChild(doc.createTextNode(input.get(pos).contents));
            COMMENT = addChildTerminalID(COMMENT);
            nexttoken();
         
        } else {
            error(" Comment or ; or , or }  or end of file");
        }
    }

    private static void proc(Element parent) {
        if (pos >= input.size()) {
           error("p");
        }

        Element PROC = createElement(parent, "PROC");

        if (pos < input.size() && (input.get(pos).contents).equals("p")) {
            PROC.appendChild(doc.createTextNode(input.get(pos).contents));
            PROC = addChildTerminalID(PROC);
            nexttoken();
         

            digits(PROC);
            if (pos < input.size() && (input.get(pos).contents).equals("{")) {
                PROC.appendChild(doc.createTextNode(input.get(pos).contents));
                PROC = addChildTerminalID(PROC);
                nexttoken();
               
                progr(PROC);
                if (pos < input.size() && (input.get(pos).contents).equals("}")) {
                    PROC.appendChild(doc.createTextNode(input.get(pos).contents));
                    PROC = addChildTerminalID(PROC);
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

    private static void assign(Element parent) {
        if (pos >= input.size()) {
            error(" n or b or s");
        }

        Element ASSIGN = createElement(parent, "ASSIGN");

        if (pos < input.size() && (input.get(pos).contents).equals("n")) {// n DIGITS
          
            NUMVAR(ASSIGN);
        
            if (pos < input.size() && (input.get(pos).contents.equals(":"))) {
                ASSIGN.appendChild(doc.createTextNode(input.get(pos).contents));
                ASSIGN = addChildTerminalID(ASSIGN);
                nexttoken();
               if(pos < input.size() && (input.get(pos).contents.equals("="))) {
                ASSIGN.appendChild(doc.createTextNode(input.get(pos).contents));
                ASSIGN = addChildTerminalID(ASSIGN);
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
                ASSIGN.appendChild(doc.createTextNode(input.get(pos).contents));
                ASSIGN = addChildTerminalID(ASSIGN);
                nexttoken();
              if(pos < input.size() && (input.get(pos).contents.equals("="))) {
                ASSIGN.appendChild(doc.createTextNode(input.get(pos).contents));
                ASSIGN = addChildTerminalID(ASSIGN);
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
                ASSIGN.appendChild(doc.createTextNode(input.get(pos).contents));
                ASSIGN = addChildTerminalID(ASSIGN);
                nexttoken();
                if(pos < input.size() && (input.get(pos).contents.equals("="))) {
                ASSIGN.appendChild(doc.createTextNode(input.get(pos).contents));
                ASSIGN = addChildTerminalID(ASSIGN);
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

    private static void STRI(Element parent) {
        if (pos >= input.size()) {
            error(" Shortstring");
        }
        Element STRI = createElement(parent, "STRI");

        if (pos < input.size() && (input.get(pos)._class.equals("Shotstring"))) {
            STRI.appendChild(doc.createTextNode(input.get(pos).contents));
            STRI = addChildTerminalID(STRI);
            nexttoken();
         
        } else {
            error(" Shortstring");
        }
    }

    private static void BOOLEXPR(Element parent) {
        if (pos >= input.size()) {
            error(" b or T or F or ^ or v or ! or E or < or >");
        }

        Element BOOLEXPR = createElement(parent, "BOOLEXPR");

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

    private static void CMPR(Element parent) {
        if (pos >= input.size()) {
            error("E or < or >");
        }

        Element CMPR = createElement(parent, "CMPR");

        if (pos < input.size() && ((input.get(pos).contents.equals("E")))) {
            CMPR.appendChild(doc.createTextNode(input.get(pos).contents));
            CMPR = addChildTerminalID(CMPR);
            nexttoken();
         
            if (pos < input.size() && (input.get(pos).contents.equals("("))) {
                CMPR.appendChild(doc.createTextNode(input.get(pos).contents));
                CMPR = addChildTerminalID(CMPR);
                nexttoken();
             
                NUMEXPR(CMPR);

                if (pos < input.size() && (input.get(pos).contents.equals(","))) {
                    CMPR.appendChild(doc.createTextNode(input.get(pos).contents));
                    CMPR = addChildTerminalID(CMPR);
                    nexttoken();
               
                    NUMEXPR(CMPR);
                    if (pos < input.size() && (input.get(pos).contents.equals(")"))) {
                        CMPR.appendChild(doc.createTextNode(input.get(pos).contents));
                        CMPR = addChildTerminalID(CMPR);
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
            CMPR.appendChild(doc.createTextNode(input.get(pos).contents));
            CMPR = addChildTerminalID(CMPR);
            nexttoken();
         
            if (pos < input.size() && (input.get(pos).contents.equals("("))) {
                CMPR.appendChild(doc.createTextNode(input.get(pos).contents));
                CMPR = addChildTerminalID(CMPR);
                nexttoken();
            
                NUMEXPR(CMPR);

          
                if (pos < input.size() && (input.get(pos).contents.equals(","))) {
                    CMPR.appendChild(doc.createTextNode(input.get(pos).contents));
                    CMPR = addChildTerminalID(CMPR);
                    nexttoken();
                    
                    NUMEXPR(CMPR);
                    if (pos < input.size() && (input.get(pos).contents.equals(")"))) {
                        CMPR.appendChild(doc.createTextNode(input.get(pos).contents));
                        CMPR = addChildTerminalID(CMPR);
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
            CMPR.appendChild(doc.createTextNode(input.get(pos).contents));
            CMPR = addChildTerminalID(CMPR);
            nexttoken();
         
            if (pos < input.size() && (input.get(pos).contents.equals("("))) {
                CMPR.appendChild(doc.createTextNode(input.get(pos).contents));
                CMPR = addChildTerminalID(CMPR);
                nexttoken();
             
                NUMEXPR(CMPR);

         
                if (pos < input.size() && (input.get(pos).contents.equals(","))) {
                    CMPR.appendChild(doc.createTextNode(input.get(pos).contents));
                    CMPR = addChildTerminalID(CMPR);
                    nexttoken();
             
                    NUMEXPR(CMPR);
                    if (pos < input.size() && (input.get(pos).contents.equals(")"))) {
                        CMPR.appendChild(doc.createTextNode(input.get(pos).contents));
                        CMPR = addChildTerminalID(CMPR);
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

    private static void LOGIC(Element parent) {
        if (pos >= input.size()) {
            error("b or T or F or ^ or v or !");
        }

        Element LOGIC = createElement(parent, "LOGIC");

        if (pos < input.size() && ((input.get(pos).contents.equals("b")))) {
            BOOLVAR(LOGIC);

        } else if ((input.get(pos).contents.equals("T"))) {
            LOGIC.appendChild(doc.createTextNode(input.get(pos).contents));
            LOGIC = addChildTerminalID(LOGIC);
            nexttoken();
         

        } else if (pos < input.size() && (input.get(pos).contents.equals("F"))) {
            LOGIC.appendChild(doc.createTextNode(input.get(pos).contents));
            LOGIC = addChildTerminalID(LOGIC);
            nexttoken();
         

        } else if (pos < input.size() && (input.get(pos).contents.equals("^"))) {
            LOGIC.appendChild(doc.createTextNode(input.get(pos).contents));
            LOGIC = addChildTerminalID(LOGIC);
            nexttoken();
         
            if (pos < input.size() && (input.get(pos).contents.equals("("))) {
                LOGIC.appendChild(doc.createTextNode(input.get(pos).contents));
                LOGIC = addChildTerminalID(LOGIC);
                nexttoken();
           
                BOOLEXPR(LOGIC);

                if (pos < input.size() && (input.get(pos).contents.equals(","))) {
                    LOGIC.appendChild(doc.createTextNode(input.get(pos).contents));
                    LOGIC = addChildTerminalID(LOGIC);
                    nexttoken();
                
                    BOOLEXPR(LOGIC);
                    if (pos < input.size() && (input.get(pos).contents.equals(")"))) {
                        LOGIC.appendChild(doc.createTextNode(input.get(pos).contents));
                        LOGIC = addChildTerminalID(LOGIC);
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
            LOGIC.appendChild(doc.createTextNode(input.get(pos).contents));
            LOGIC = addChildTerminalID(LOGIC);
            nexttoken();
         
            if (pos < input.size() && (input.get(pos).contents.equals("("))) {
                LOGIC.appendChild(doc.createTextNode(input.get(pos).contents));
                LOGIC = addChildTerminalID(LOGIC);
                nexttoken();
         
                BOOLEXPR(LOGIC);

                if (pos < input.size() && (input.get(pos).contents.equals(","))) {
                    LOGIC.appendChild(doc.createTextNode(input.get(pos).contents));
                    LOGIC = addChildTerminalID(LOGIC);
                    nexttoken();
              
                    BOOLEXPR(LOGIC);
                    if (pos < input.size() && (input.get(pos).contents.equals(")"))) {
                        LOGIC.appendChild(doc.createTextNode(input.get(pos).contents));
                        LOGIC = addChildTerminalID(LOGIC);
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
            LOGIC.appendChild(doc.createTextNode(input.get(pos).contents));
            LOGIC = addChildTerminalID(LOGIC);
            nexttoken();
         
            if (pos < input.size() && (input.get(pos).contents.equals("("))) {
                LOGIC.appendChild(doc.createTextNode(input.get(pos).contents));
                LOGIC = addChildTerminalID(LOGIC);
                nexttoken();
              
                BOOLEXPR(LOGIC);

                if (pos < input.size() && (input.get(pos).contents.equals(")"))) {
                    LOGIC.appendChild(doc.createTextNode(input.get(pos).contents));
                    LOGIC = addChildTerminalID(LOGIC);
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

    private static void BOOLVAR(Element parent) {
        if (pos >= input.size()) {
            error(" b ");
        }
        Element BOOLVAR = createElement(parent, "BOOLVAR");

        if (pos < input.size() && input.get(pos).contents.equals("b")) {
            BOOLVAR.appendChild(doc.createTextNode(input.get(pos).contents));
            BOOLVAR = addChildTerminalID(BOOLVAR);
            nexttoken();
         
            digits(BOOLVAR);
        } else {
            error(" b ");
        }
    }

    private static void NUMEXPR(Element parent) {
        if (pos >= input.size()) {
            error(" a or m or d or n or  0.00 or - or 1 to 9");
        }

        Element NUMEXPR = createElement(parent, "NUMEXPR");

        if (pos < input.size() && (input.get(pos).contents).equals("a")) {// n DIGITS
            NUMEXPR.appendChild(doc.createTextNode(input.get(pos).contents));
            NUMEXPR = addChildTerminalID(NUMEXPR);
            nexttoken();
         
            if (pos < input.size() && (input.get(pos).contents.equals("("))) {
                NUMEXPR.appendChild(doc.createTextNode(input.get(pos).contents));
                NUMEXPR = addChildTerminalID(NUMEXPR);
                nexttoken();
            
                NUMEXPR(NUMEXPR);

          
                if (pos < input.size() && (input.get(pos).contents.equals(","))) {
                    NUMEXPR.appendChild(doc.createTextNode(input.get(pos).contents));
                    NUMEXPR = addChildTerminalID(NUMEXPR);
                    nexttoken();
              
                    NUMEXPR(NUMEXPR);
                    if (pos < input.size() && (input.get(pos).contents.equals(")"))) {
                        NUMEXPR.appendChild(doc.createTextNode(input.get(pos).contents));
                        NUMEXPR = addChildTerminalID(NUMEXPR);
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
            NUMEXPR.appendChild(doc.createTextNode(input.get(pos).contents));
            NUMEXPR = addChildTerminalID(NUMEXPR);

            nexttoken();
         
            if (pos < input.size() && (input.get(pos).contents.equals("("))) {
                NUMEXPR.appendChild(doc.createTextNode(input.get(pos).contents));
                NUMEXPR = addChildTerminalID(NUMEXPR);
                nexttoken();
             
                NUMEXPR(NUMEXPR);


                if (pos < input.size() && (input.get(pos).contents.equals(","))) {
                    NUMEXPR.appendChild(doc.createTextNode(input.get(pos).contents));
                    NUMEXPR = addChildTerminalID(NUMEXPR);
                    nexttoken();
                  
                    NUMEXPR(NUMEXPR);
                    if (pos < input.size() && (input.get(pos).contents.equals(")"))) {
                        NUMEXPR.appendChild(doc.createTextNode(input.get(pos).contents));
                        NUMEXPR = addChildTerminalID(NUMEXPR);
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
            NUMEXPR.appendChild(doc.createTextNode(input.get(pos).contents));
            NUMEXPR = addChildTerminalID(NUMEXPR);

            nexttoken();
         
            if (pos < input.size() && (input.get(pos).contents.equals("("))) {
                NUMEXPR.appendChild(doc.createTextNode(input.get(pos).contents));
                NUMEXPR = addChildTerminalID(NUMEXPR);
                nexttoken();
               
                NUMEXPR(NUMEXPR);

               
                if (pos < input.size() && (input.get(pos).contents.equals(","))) {
                    NUMEXPR.appendChild(doc.createTextNode(input.get(pos).contents));
                    NUMEXPR = addChildTerminalID(NUMEXPR);
                    nexttoken();
                   
                    NUMEXPR(NUMEXPR);
                    if (pos < input.size() && (input.get(pos).contents.equals(")"))) {
                        NUMEXPR.appendChild(doc.createTextNode(input.get(pos).contents));
                        NUMEXPR = addChildTerminalID(NUMEXPR);
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
            // NUMEXPR.appendChild(doc.createTextNode(input.get(pos).contents));
            // NUMEXPR = addChildTerminalID(NUMEXPR);
            // nexttoken();
         
            // digits(NUMEXPR);
            NUMVAR(NUMEXPR);

        } else if (pos < input.size() && ( (input.get(pos).contents.equals("-")) || (Character.isDigit(input.get(pos).contents.charAt(0))))) {
            DECNUM(NUMEXPR);

        } else {
            error(" a or m or d or n or  0.00 or - or 1 to 9");
        }
    }

    private static void DECNUM(Element parent) {
        if (pos >= input.size()) {
            error("0.00 or - or 1 to 9");
        }

        Element DECNUM = createElement(parent, "DECNUM");

        if (pos < input.size() && ((input.get(pos).contents.equals("0")))) {
            DECNUM.appendChild(doc.createTextNode(input.get(pos).contents));
            DECNUM = addChildTerminalID(DECNUM);
            nexttoken();
            if(pos < input.size() && ((input.get(pos).contents.equals(".")))){
                DECNUM.appendChild(doc.createTextNode(input.get(pos).contents));
                DECNUM = addChildTerminalID(DECNUM);
                nexttoken();
               
                if(pos < input.size() && ((input.get(pos).contents.equals("0")))){
                    DECNUM.appendChild(doc.createTextNode(input.get(pos).contents));
                    DECNUM = addChildTerminalID(DECNUM);
                    nexttoken();
                    if(pos < input.size() && ((input.get(pos).contents.equals("0")))){
                        DECNUM.appendChild(doc.createTextNode(input.get(pos).contents));
                        DECNUM = addChildTerminalID(DECNUM);
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

    private static void NEG(Element parent) {
        if (pos >= input.size()) {
            error(" - ");
        }


        Element NEG = createElement(parent, "NEG");
        NEG.appendChild(doc.createTextNode(input.get(pos).contents));
        NEG = addChildTerminalID(NEG);
        nexttoken();
        POS(NEG);
    }
    private static void POS(Element parent) {
        if (pos >= input.size()) {
            error(" 1 ... 9");
        }

        Element POS = createElement(parent, "POS");

        INT(POS);
        
        if (pos < input.size() && ((input.get(pos).contents.equals(".")))) {
            POS.appendChild(doc.createTextNode(input.get(pos).contents));
            POS = addChildTerminalID(POS);
            nexttoken();
         

            D(POS);
            D(POS);

        } else {
            error(".");
        }

    }

    private static void INT(Element parent) {
        if (pos >= input.size()) {
            error(" 1 ... 9");
        }

        Element INT = createElement(parent, "INT");

        if (pos < input.size() && ((isOneorNine(input.get(pos).contents)))) {

            INT.appendChild(doc.createTextNode(input.get(pos).contents));
            INT = addChildTerminalID(INT);
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

    private static void call(Element parent) {
        if (pos >= input.size()) {
            error(" c ");
        }

        Element CALL = createElement(parent, "CALL");

        if (pos < input.size() && (((input.get(pos).contents).equals("c")))) {
            CALL.appendChild(doc.createTextNode(input.get(pos).contents));
            CALL = addChildTerminalID(CALL);
            nexttoken();
         
            if (pos < input.size() && (((input.get(pos).contents).equals("p")))) {
                CALL.appendChild(doc.createTextNode(input.get(pos).contents));
                CALL = addChildTerminalID(CALL);
                nexttoken();
                
                digits(CALL);

            } else {
                error(" p ");
            }

        } else {
            error(" c ");
        }
    }

    private static void loop(Element parent) {
        if (pos >= input.size()) {
            error("w");
        }

        Element LOOP = createElement(parent, "LOOP");

        if (pos < input.size() && (input.get(pos).contents).equals("w")) {
            LOOP.appendChild(doc.createTextNode(input.get(pos).contents));
            LOOP = addChildTerminalID(LOOP);
            nexttoken();
         
            if (pos < input.size() && (input.get(pos).contents.equals("("))) {
                LOOP.appendChild(doc.createTextNode(input.get(pos).contents));
                LOOP = addChildTerminalID(LOOP);
                nexttoken();
               
                BOOLEXPR(LOOP);

                if (pos < input.size() && (input.get(pos).contents.equals(")"))) {
                    LOOP.appendChild(doc.createTextNode(input.get(pos).contents));
                    LOOP = addChildTerminalID(LOOP);
                    nexttoken();
                   
                    if (pos < input.size() && (input.get(pos).contents.equals("{"))) {
                        LOOP.appendChild(doc.createTextNode(input.get(pos).contents));
                        LOOP = addChildTerminalID(LOOP);
                        nexttoken();
                       
                        algo(LOOP);

                        if (pos < input.size() && (input.get(pos).contents.equals("}"))) {
                            LOOP.appendChild(doc.createTextNode(input.get(pos).contents));
                            LOOP = addChildTerminalID(LOOP);
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

    private static void branch(Element parent) {
        if (pos >= input.size()) {
            error("i");
        }
        Element BRANCH = createElement(parent, "BRANCH");

        if (pos < input.size() && (input.get(pos).contents).equals("i")) {
            BRANCH.appendChild(doc.createTextNode(input.get(pos).contents));
            BRANCH = addChildTerminalID(BRANCH);
            nexttoken();
         
            if (pos < input.size() && (input.get(pos).contents.equals("("))) {
                BRANCH.appendChild(doc.createTextNode(input.get(pos).contents));
                BRANCH = addChildTerminalID(BRANCH);
                nexttoken();
                
                BOOLEXPR(BRANCH);

                if (pos < input.size() && (input.get(pos).contents.equals(")"))) {
                    BRANCH.appendChild(doc.createTextNode(input.get(pos).contents));
                    BRANCH = addChildTerminalID(BRANCH);
                    nexttoken();
                   
                    if (pos < input.size() && (input.get(pos).contents.equals("t"))) {
                        BRANCH.appendChild(doc.createTextNode(input.get(pos).contents));
                        BRANCH = addChildTerminalID(BRANCH);
                        nexttoken();
                        
                        if (pos < input.size() && (input.get(pos).contents.equals("{"))) {
                            BRANCH.appendChild(doc.createTextNode(input.get(pos).contents));
                            BRANCH = addChildTerminalID(BRANCH);
                            nexttoken();
                           
                            algo(BRANCH);

                            if (pos < input.size() && (input.get(pos).contents.equals("}"))) {
                                BRANCH.appendChild(doc.createTextNode(input.get(pos).contents));
                                BRANCH = addChildTerminalID(BRANCH);
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

    private static void ELSE(Element parent) {
        if (pos >= input.size()) {
            return;
        }

        if (pos < input.size() && ((input.get(pos)._class).equals("Comment") || (input.get(pos).contents).equals(";")
                || (input.get(pos).contents).equals(",")
                || (input.get(pos).contents).equals("}"))) {// follow of else [ * ; , } $]
            return;

        }
        Element ELSE = createElement(parent, "ELSE");

        if (pos < input.size() && (input.get(pos).contents).equals("e")) {
            ELSE.appendChild(doc.createTextNode(input.get(pos).contents));
            ELSE = addChildTerminalID(ELSE);

            nexttoken();
         

            if (pos < input.size() && (input.get(pos).contents.equals("{"))) {
                ELSE.appendChild(doc.createTextNode(input.get(pos).contents));
                ELSE = addChildTerminalID(ELSE);
                nexttoken();
                
                algo(ELSE);

                if (pos < input.size() && (input.get(pos).contents.equals("}"))) {
                    ELSE.appendChild(doc.createTextNode(input.get(pos).contents));
                    ELSE = addChildTerminalID(ELSE);
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

    private static void halt(Element parent) {
        if (pos >= input.size()) {
            error("h");
        }

        Element HALT = createElement(parent, "HALT");

        if (pos < input.size() && (input.get(pos).contents).equals("h")) {
            HALT.appendChild(doc.createTextNode(input.get(pos).contents));
            HALT = addChildTerminalID(HALT);
            nexttoken();
         
        } else {
            error("h");
        }
    }

    private static void nexttoken() {

        if (pos < (input.size())) {
            // System.out.println("___________________________" + input.get(pos).contents);
            pos++;
        }

    }

    private static Element addChildTerminalID(Element parent) {
        
        // If the parent has children, append the new child ID to the children attribute
        String parentChildren = parent.getAttribute("children");
        if (!parentChildren.isEmpty()) {
            parentChildren += " ";
        }
        parentChildren += idCounter++;
        parent.setAttribute("children", parentChildren);
        return parent;
    }

    private static Element createElement(Element parent, String name) {
        Element elem = doc.createElement(name);

        elem.setAttribute("id", "" + idCounter++);

        // If the parent has children, append the new child ID to the children attribute
        String parentChildren = parent.getAttribute("children");
        if (!parentChildren.isEmpty()) {
            parentChildren += " ";
        }
        parentChildren += elem.getAttribute("id");
        parent.setAttribute("children", parentChildren);

        parent.appendChild(elem);
        return elem;
    }

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
