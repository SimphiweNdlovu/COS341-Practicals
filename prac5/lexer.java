import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

// import javax.lang.model.util.ElementScanner14;

import java.util.Arrays;
import java.lang.*;
import java.util.ArrayList;
import java.util.*;
import java.io.*;
import java.nio.*;

public class lexer {

  final ArrayList<token> Validtokens = new ArrayList<token>();

  class token {
    int id; // (position of the token in the stream).
    String _class; // (for example: number, or keyword)
    String contents; // token-type , whats written in the string
    int lineNumber;
    // token next;

    public token(int id, String _class, String contents, int lineNumber) {
      this.id = id;
      this._class = _class;
      this.contents = contents;
      this.lineNumber = lineNumber;

    }
  }

  public static boolean AssciCharBetween32to127(String s) {
    for (char c : s.toCharArray()) {

      if (c >= 32 && c <= 127) {
        continue;

      } else {
        return false;
      }

    }
    return true;
  }

  public ArrayList<token> c_lexer() throws FileNotFoundException {
    Character[] letter_chars = new Character[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
        'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
    Character[] letterupppercase_chars = new Character[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
        'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
    Character[] numbers1 = new Character[] { '0', '2', '1', '3', '4', '5', '6', '7', '8', '9' };
    Character[] validletters = new Character[] { 'p', 'h', 'c', 'w', 'i', 'e', 'n', 'b', 's', 'a', 'm', 'd', 'T', 'F',
        'v', 'E', 'g', 'o', 'r', 't' };
    // System.out.println("valid letters" + validletters.length);

    Character tokenSymbols[] = { ';', '{', '}', '(', ')', ',', '-', '^', '<', '>', '!', '.', ':', '=' };

    List<Character> validLetters = new ArrayList<>(Arrays.asList(validletters));
    List<Character> letterupppercase = new ArrayList<>(Arrays.asList(letterupppercase_chars));
    List<Character> letter = new ArrayList<>(Arrays.asList(letter_chars));
    List<Character> numbers = new ArrayList<>(Arrays.asList(numbers1));
    List<Character> _tokenSymbols = new ArrayList<>(Arrays.asList(tokenSymbols));

    int Id_num = 0;
    final ArrayList<String> OutputArr = new ArrayList<String>();
    int OutputArr_Count = 0;

    // pass the path to the file as a parameter

    File file = new File("input.txt");
    try (Scanner sc = new Scanner(file)) {
      int counter = 0;
      String store = "";
      // final ArrayList<String> store = new ArrayList<String>();
      int lineNumber = 1;
      while (sc.hasNextLine()) {
        String str = sc.nextLine();

        // System.out.println(str);
        char[] myChar = str.toCharArray();
        int linelength = myChar.length;
        // do something

        for (int i = 0; i < myChar.length; i++) {
          // System.out.println(" the char "+myChar[i] + " number" + i);
          if (myChar[i] == ' ') {
            continue;
          }

          // allow for tab space
          else if (myChar[i] == '\t') {
            continue;
          }
        
          if (myChar[i] != ' ' && myChar[i] != '*' && myChar[i] != ':' && myChar[i] != '=' && myChar[i] != '0'
              && !numbers.contains(myChar[i]) && myChar[i] != '\"'
              && !(letter.contains(myChar[i]) || numbers.contains(myChar[i]) || _tokenSymbols.contains(myChar[i])
                  || letterupppercase.contains(myChar[i]))) {
            System.out.println(
                "LEXICAL ERROR Undefined Symbol: " + myChar[i] + " line number: " + lineNumber + " in position " + (i));
            System.exit(1);
          }

          else if (myChar[i] == '\"') {// CHECKING FOR A VALID short STRING
            int count_string_leng = 0;
            i++;// the first "
            store += '"'; // storing the first "
            for (int ii = i; ii < myChar.length; ii++) {
              if (myChar[ii] == '\"') {
                store += '\"'; // storing the last "
                Boolean status = AssciCharBetween32to127(store);
                if (status == true) {
                  if (count_string_leng > 15) {
                    System.out.println(
                        "LEXICAL ERROR shortstring must be less than 15 characters" + " On lineNumber " + lineNumber);
                    System.exit(1);
                    i = ii;// updating the curser

                    break;
                  } else if (count_string_leng == 15) {
                    OutputArr.add(store);
                    Id_num++;
                    token obj = new token(Id_num, "Shotstring", store, lineNumber);
                    Validtokens.add(obj);
                    OutputArr_Count++;
                    i = ii; // updating the curser
                    // System.out.println(
                    //     "( Token number: " + (Id_num) + " ,Short String, " + store + ")   line number:" + lineNumber);
                    break;
                  } else {
                    System.out.println(
                        "LEXICAL ERROR shortstring must be  15 characters long " + " On lineNumber " + lineNumber);
                    System.exit(1);
                  }

                } else {
                  System.out.println("LEXICAL ERROR exepected short string " + " On lineNumber " + lineNumber);
                  System.exit(1);
                  i = ii;// updating the curser
                  break;
                }

              } else {
                store += myChar[ii];
                count_string_leng++;
              }
            }
            if (store.charAt(store.length() - 1) != '\"') {
              System.out.println("LEXICAL ERROR found a short string  with no second inveted comma "
                  + " On lineNumber " + lineNumber);
              System.exit(1);
            }
          } else if (myChar[i] == '*') {// CHECKING FOR A VALID comment
            int count_string_leng = 0;
            i++;// the first "
            store += '*'; // storing the first *
            for (int ii = i; ii < myChar.length; ii++) {
              if (myChar[ii] == '*') {
                store += '*'; // storing the last *
                Boolean status = AssciCharBetween32to127(store);
                if (status == true) {
                  if (count_string_leng > 15) {
                    System.out.println(
                        "LEXICAL ERROR comment must be less than 15 characters" + " On lineNumber " + lineNumber);
                    System.exit(1);
                    i = ii;// updating the curser

                    break;
                  } else if (count_string_leng == 15) {
                    OutputArr.add(store);
                    Id_num++;
                    token obj = new token(Id_num, "Comment", store, lineNumber);
                    Validtokens.add(obj);
                    OutputArr_Count++;
                    i = ii; // updating the curser
                    // System.out.println(
                        // "( Token number: " + (Id_num) + " ,Comment, " + store + ")   line number:" + lineNumber);
                    break;
                  } else {
                    System.out
                        .println("LEXICAL ERROR comment must be  15 characters long " + " On lineNumber " + lineNumber);
                    System.exit(1);
                  }

                } else {
                  System.out.println("LEXICAL ERROR exepected comment " + " On lineNumber " + lineNumber);
                  System.exit(1);
                  i = ii;// updating the curser
                  break;
                }

              } else {
                store += myChar[ii];
                count_string_leng++;
              }
            }
            if (store.charAt(store.length() - 1) != '*') {
              System.out.println("LEXICAL ERROR found a Comment  with no second  * "
                  + " On lineNumber " + lineNumber);
              System.exit(1);
            }
          } else if (letter.contains(myChar[i]) == true || letterupppercase.contains(myChar[i]) == true) {

            
            if (validLetters.contains(myChar[i]) == false) {
              System.out.println("LEXICAL ERROR exepected letter " + " On lineNumber " + lineNumber);
              System.exit(1);
            }
            store = "";
            store += myChar[i];
            Id_num++;
            token obj = new token(Id_num, "Letter", store, lineNumber);
            Validtokens.add(obj);

            // System.out
            //     .println("( Token number: " + (Id_num) + " ,Letter, " + myChar[i] + ")   line number:" + lineNumber);

          } else if (_tokenSymbols.contains(myChar[i])) {
            
              store = "";
              store += myChar[i];
              Id_num++;
              token obj = new token(Id_num, "tokenSymbol", store, lineNumber);
              Validtokens.add(obj);

              // System.out.println("( Token: " + Id_num + ",tokenSymbol, " + store + " ) ");

          

          } else if (numbers.contains(myChar[i])) {
            
              store = "";
              store += myChar[i];
              Id_num++;
              token obj = new token(Id_num, "Digits", store, lineNumber);
              Validtokens.add(obj);
              // System.out.println("( Token " + (Id_num) + " ,Digits, " + store + " )");
            

          }
     
          store = "";
        }
        // System.out.println();
        // System.out.println("#############################################");
        // System.out.println("Line number: " + lineNumber + " Done.");
        // System.out.println();
        // System.out.println();

        lineNumber++;
      }
    }
    return Validtokens;

  }

}