import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Parse {
    static int nextTokenIndex = 0;
    static lexer.token currentToken = null;
    static ArrayList<lexer.token> input = new ArrayList<lexer.token>();
    static int pos = 0;
    static int checkIfTwoD = 0;

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
        // input.add
        // System.out.println("GoParse");
        try {
            progr();

            System.out.println("Program successfully parsed!");
        } catch (Exception e) {
            System.out.println("Parsing error: " + e.getMessage());
        }
    }

    private static void progr() {
        if(pos==input.size()){
            return;
        } 
        algo();
        procdefs();
    }

    private static void algo() {
        if(pos==input.size()){
            return;
        } 
        instr();
        comment();
        seq();
    }

    private static void instr() {
        if(pos==input.size()){
            return;
        } 
        if (pos < input.size() && (input.get(pos).contents).equals("g")) {// g NUMVAR // The g stands for getting a
                                                                          // number from
            // the user
            input(); // g NUMVAR
        } else if (pos < input.size()
                && ((input.get(pos).contents).equals("o") || (input.get(pos).contents).equals("r"))) {// o
            // NUMVAR
            // , r
            // STRINGV
            output();// TEXT | VALUE
        } else if (pos < input.size() && ((input.get(pos).contents).equals("n") || (input.get(pos).contents).equals("b")
                || (input.get(pos).contents).equals("s"))) {// n DIGITS
            assign();// NUMVAR :=NUMEXPR
        } else if (pos < input.size() && (input.get(pos).contents).equals("c")) {
            call();// c pDIGITS
        } else if (pos < input.size() && (input.get(pos).contents).equals("w")) {
            loop();// w(BOOLEXPR){ALGO}
        } else if (pos < input.size() && (input.get(pos).contents).equals("i")) {
            branch();// i(BOOLEXPR)t{ALGO}ELSE
        } else if (pos < input.size() && (input.get(pos).contents).equals("h")) {
            halt();
        } else {
            error("g or  o  or n  or b or s or c or w or i or h");
        }
    }

    private static void output() {
        if(pos==input.size()){
            return;
        } 
        if (pos < input.size() && input.get(pos).contents.equals("o")) { // value
            nexttoken();
            NUMVAR();
        } else if (pos < input.size() && input.get(pos).contents.equals("r")) {// text
            nexttoken();
            STRINGV();
        } else {
            error(" o or r ");
        }
    }

    private static void STRINGV() {
        if(pos==input.size()){
            return;
        } 
        if (pos < input.size() && input.get(pos).contents.equals("s")) {
            nexttoken();
            digits();
        } else {
            error(" s ");
        }
    }

    private static void input() {
        if(pos==input.size()){
            return;
        } 
        if (pos < input.size() && input.get(pos).contents.equals("g")) {
            nexttoken();
            NUMVAR();
        } else {
            error(" g ");
        }
    }

    private static void NUMVAR() {
        if(pos==input.size()){
            return;
        } 
        if (pos < input.size() && input.get(pos).contents.equals("n")) {
            nexttoken();
            digits();
        } else {
            error(" n ");
        }
    }

    // { 0 to 9 .

    private static void digits() {
        if(pos==input.size()){
            return;
        } 
        // any numbur from 0 to 9
        D();
        MORE();

    }

    private static void MORE() {
        if(pos==input.size()){
            return;
        } 
        if (pos < input.size() && isDigit(input.get(pos).contents) == true) {

            digits();
        } else if (pos < input.size() && ((input.get(pos).contents).equals("{") || (input.get(pos).contents).equals(".")
                || (input.get(pos).contents).equals("*") || (input.get(pos).contents).equals(";")
                || (input.get(pos).contents).equals(",") || (input.get(pos).contents).equals("}")
                || (input.get(pos).contents).equals(":=") || (input.get(pos).contents).equals(")"))) { // epsilon follow
                                                                                                       // of more = [{ .
                                                                                                       // 0 to 9 := ] //
                                                                                                       // := since
            // numvar calls digits
            return;

        }
        else {
            error(" 0 to 9   or { or . or :=  ");
        }
    }

    private static void D() {
        if(pos==input.size()){
            return;
        } 
        if (pos < input.size() && isDigit(input.get(pos).contents) == true) {
            nexttoken();

        }else if(pos==input.size()){
            return;
        }  
        else 
        {
            error("0 ... 9");
        }
    }

    // function that checks if the string any numbur from 0 to 9
    public static boolean isDigit(String str) {
        
        return str.length() == 1 && Character.isDigit(str.charAt(0));
    }

    private static void seq() {
        if(pos==input.size()){
            return;
        } 
        if (pos < input.size() && (input.get(pos).contents).equals(";")) {
            nexttoken();
            algo();
        } else if ((pos < input.size()
                && ((input.get(pos).contents).equals("}") || (input.get(pos).contents).equals(",")))) {

        } else {
            error(";");
        }
    }

    private static void procdefs() {
        if(pos==input.size()){
            return;
        } 
        if (pos < input.size() && ((input.get(pos).contents).equals(","))) {
            nexttoken();
            proc();
            procdefs();
        }
    }

    private static void comment() {
        if(pos==input.size()){
            return;
        } 
        if (pos < input.size() && (input.get(pos)._class).equals("Comment")) {
            nexttoken();
        } else if ((pos < input.size() && ((input.get(pos).contents).equals(";")
                || (input.get(pos).contents).equals(",")
                || (input.get(pos).contents).equals("}")))) {

        } else {
            error(" Comment");
        }
    }

    private static void proc() {
        if(pos==input.size()){
            return;
        } 
        if (pos < input.size() && (input.get(pos).contents).equals("p")) {
            nexttoken();
            digits();
            if (pos < input.size() && (input.get(pos).contents).equals("{")) {
                nexttoken();
                progr();
                if (pos < input.size() && (input.get(pos).contents).equals("}")) {
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

    private static void assign() {
        if(pos==input.size()){
            return;
        } 
        if (pos < input.size() && (input.get(pos).contents).equals("n")) {// n DIGITS
            NUMVAR();

            if (pos < input.size() && (input.get(pos).contents.equals(":="))) {
                nexttoken();
                NUMEXPR();
            } else {
                error(" := ");
            }

        } else if (pos < input.size() && (input.get(pos).contents.equals("b"))) {
            BOOLVAR();
            if (pos < input.size() && (input.get(pos).contents.equals(":="))) {
                nexttoken();
                BOOLEXPR();
            } else {
                error(" := ");
            }

        } else if (pos < input.size() && (input.get(pos).contents.equals("s"))) {
            STRINGV();

            if (pos < input.size() && (input.get(pos).contents.equals(":="))) {
                nexttoken();
                STRI();
            } else {
                error(" := ");
            }

        } else {
            error(" n or b or s");
        }
    }

    private static void STRI() {
        if(pos==input.size()){
            return;
        } 
        if (pos < input.size() && (input.get(pos)._class.equals("Shotstring"))) {
            nexttoken();
        } else {
            error(" Shortstring");
        }
    }

    private static void BOOLEXPR() {
        if(pos==input.size()){
            return;
        } 
        if (pos < input.size() && ((input.get(pos).contents.equals("b")) || (input.get(pos).contents.equals("T"))
                || (input.get(pos).contents.equals("F")) || (input.get(pos).contents.equals("^"))
                || (input.get(pos).contents.equals("v")) || (input.get(pos).contents.equals("!")))) {
            LOGIC();

        } else if (pos < input.size() && ((input.get(pos).contents.equals("E")) || (input.get(pos).contents.equals("<"))
                || (input.get(pos).contents.equals(">")))) {
            CMPR();

        } else {
            error(" b or T or F or ^ or v or ! or E or < or >");
        }

    }

    private static void CMPR() {
        if(pos==input.size()){
            return;
        } 

        if (pos < input.size() && ((input.get(pos).contents.equals("E")))) {
            nexttoken();
            if (pos < input.size() && (input.get(pos).contents.equals("("))) {
                nexttoken();
                NUMEXPR();

                // nexttoken();
                if (pos < input.size() && (input.get(pos).contents.equals(","))) {
                    nexttoken();
                    NUMEXPR();
                    if (pos < input.size() && (input.get(pos).contents.equals(")"))) {
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
            nexttoken();
            if (pos < input.size() && (input.get(pos).contents.equals("("))) {
                nexttoken();
                NUMEXPR();

                // nexttoken();
                if (pos < input.size() && (input.get(pos).contents.equals(","))) {
                    nexttoken();
                    NUMEXPR();
                    if (pos < input.size() && (input.get(pos).contents.equals(")"))) {
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
            nexttoken();
            if (pos < input.size() && (input.get(pos).contents.equals("("))) {
                nexttoken();
                NUMEXPR();

                // nexttoken();
                if (pos < input.size() && (input.get(pos).contents.equals(","))) {
                    nexttoken();
                    NUMEXPR();
                    if (pos < input.size() && (input.get(pos).contents.equals(")"))) {
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

    private static void LOGIC() {
        if(pos==input.size()){
            return;
        } 
        if (pos < input.size() && ((input.get(pos).contents.equals("b")))) {
            BOOLVAR();

        } else if ((input.get(pos).contents.equals("T"))) {
            nexttoken();

        } else if (pos < input.size() && (input.get(pos).contents.equals("F"))) {
            nexttoken();

        } else if (pos < input.size() && (input.get(pos).contents.equals("^"))) {
            nexttoken();
            if (pos < input.size() && (input.get(pos).contents.equals("("))) {
                nexttoken();
                BOOLEXPR();

                if (pos < input.size() && (input.get(pos).contents.equals(","))) {
                    nexttoken();
                    BOOLEXPR();
                    if (pos < input.size() && (input.get(pos).contents.equals(")"))) {
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
            nexttoken();
            if (pos < input.size() && (input.get(pos).contents.equals("("))) {
                nexttoken();
                BOOLEXPR();

                if (pos < input.size() && (input.get(pos).contents.equals(","))) {
                    nexttoken();
                    BOOLEXPR();
                    if (pos < input.size() && (input.get(pos).contents.equals(")"))) {
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
            nexttoken();
            if (pos < input.size() && (input.get(pos).contents.equals("("))) {
                nexttoken();
                BOOLEXPR();

                if (pos < input.size() && (input.get(pos).contents.equals(")"))) {
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

    private static void BOOLVAR() {
        if(pos==input.size()){
            return;
        } 
        if (pos < input.size() && input.get(pos).contents.equals("b")) {
            nexttoken();
            digits();
        } else {
            error(" b ");
        }
    }

    private static void NUMEXPR() {
        if(pos==input.size()){
            return;
        } 
        if (pos < input.size() && (input.get(pos).contents).equals("a")) {// n DIGITS
            nexttoken();
            if (pos < input.size() && (input.get(pos).contents.equals("("))) {
                nexttoken();
                NUMEXPR();

                // nexttoken();
                if (pos < input.size() && (input.get(pos).contents.equals(","))) {
                    nexttoken();
                    NUMEXPR();
                    if (pos < input.size() && (input.get(pos).contents.equals(")"))) {
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

            nexttoken();
            if (pos < input.size() && (input.get(pos).contents.equals("("))) {
                nexttoken();
                NUMEXPR();

                // nexttoken();
                if (pos < input.size() && (input.get(pos).contents.equals(","))) {
                    nexttoken();
                    NUMEXPR();
                    if (pos < input.size() && (input.get(pos).contents.equals(")"))) {
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

            nexttoken();
            if (pos < input.size() && (input.get(pos).contents.equals("("))) {
                nexttoken();
                NUMEXPR();

                // nexttoken();
                if (pos < input.size() && (input.get(pos).contents.equals(","))) {
                    nexttoken();
                    NUMEXPR();
                    if (pos < input.size() && (input.get(pos).contents.equals(")"))) {
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
            nexttoken();
            digits();

        } else if (pos < input.size() && ((input.get(pos).contents.equals("0.00"))
                || (input.get(pos).contents.equals("-")) || (isOneorNine(input.get(pos).contents)))) {
            DECNUM();

        } else {
            error(" a or m or d or n or  0.00 or - or 1 to 9");
        }
    }

    private static void DECNUM() {
        if(pos==input.size()){
            return;
        } 
        if (pos < input.size() && ((input.get(pos).contents.equals("0.00")))) {
            nexttoken();

        } else if (pos < input.size() && ((input.get(pos).contents.equals("-")))) {
            nexttoken();
            POS();

        } else if (pos < input.size() && ((isOneorNine(input.get(pos).contents)))) {

            POS();
        }
    }

    private static void POS() {
        if(pos==input.size()){
            return;
        } 
        INT();
        if (pos < input.size() && ((input.get(pos).contents.equals(".")))) {
            nexttoken();

            D();
            D();

        } else {
            error(".");
        }

    }

    private static void INT() {
        if(pos==input.size()){
            return;
        } 
        if (pos < input.size() && ((isOneorNine(input.get(pos).contents)))) {
            nexttoken();
            MORE();

        } else {
            error(" 1 ... 9");
        }
    }

    private static boolean isOneorNine(String contents) {
        boolean status = false;
        if (contents.length() == 1 && Character.isDigit(contents.charAt(0)) && contents != "0") {
            status = true;
        }
        return status;
    }

    private static void call() {
        if(pos==input.size()){
            return;
        } 
        if (pos < input.size() && (((input.get(pos).contents).equals("c")))) {
            nexttoken();
            if (pos < input.size() && (((input.get(pos).contents).equals("p")))) {
                nexttoken();
                digits();

            } else {
                error(" p ");
            }

        } else {
            error(" c ");
        }
    }

    private static void loop() {
        if(pos==input.size()){
            return;
        } 
        if (pos < input.size() && (input.get(pos).contents).equals("w")) {
            nexttoken();
            if (pos < input.size() && (input.get(pos).contents.equals("("))) {
                nexttoken();
                BOOLEXPR();

                if (pos < input.size() && (input.get(pos).contents.equals(")"))) {
                    nexttoken();
                    if (pos < input.size() && (input.get(pos).contents.equals("{"))) {
                        nexttoken();
                        algo();

                        if (pos < input.size() && (input.get(pos).contents.equals("}"))) {
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

    private static void branch() {
        if(pos==input.size()){
            return;
        } 
        if (pos < input.size() && (input.get(pos).contents).equals("i")) {
            nexttoken();
            if (pos < input.size() && (input.get(pos).contents.equals("("))) {
                nexttoken();
                BOOLEXPR();

                if (pos < input.size() && (input.get(pos).contents.equals(")"))) {
                    nexttoken();
                    if (pos < input.size() && (input.get(pos).contents.equals("t"))) {
                        nexttoken();
                        if (pos < input.size() && (input.get(pos).contents.equals("{"))) {
                            nexttoken();
                            algo();

                            if (pos < input.size() && (input.get(pos).contents.equals("}"))) {
                                nexttoken();

                                ELSE();
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

    private static void ELSE() {
        if(pos==input.size()){
            return;
        } 
        if (pos < input.size() && (input.get(pos).contents).equals("e")) {
            nexttoken();

            if (pos < input.size() && (input.get(pos).contents.equals("{"))) {
                nexttoken();
                algo();

                if (pos < input.size() && (input.get(pos).contents.equals("}"))) {
                    nexttoken();

                } else {
                    error(" } ");
                }

            } else {
                error(" { ");
            }
        } else if (pos < input.size() && ((input.get(pos).contents).equals("*") || (input.get(pos).contents).equals(";")
                || (input.get(pos).contents).equals(",")
                || (input.get(pos).contents).equals("}"))) {// follow of else [ * ; , } $]
            return;

        } else {
            error("e");
        }
    }

    private static void halt() {
        if(pos==input.size()){
            return;
        } 
        if (pos < input.size() && (input.get(pos).contents).equals("h")) {
            nexttoken();
        } else {
            error("h");
        }
    }
    private static void nexttoken(){
        if(pos<(input.size())){
            // currentToken=input.get(pos);
            pos++;
        }
       
    }
    private static void error(String expected) {

        System.out.println("Syntax error at line: " + input.get(pos).lineNumber + " expected: " + expected
                + " but got token: " + input.get(pos).contents);
        System.exit(1);
    }
}
