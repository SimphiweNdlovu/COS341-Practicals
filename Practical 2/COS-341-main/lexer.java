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

  class token{    
    int id; //(position of the token in the stream).
    String _class; //  (for example: number, or keyword) 
    String contents; // token-type , whats written in the string
    // token next;    
        
    public token(int id,String _class,String contents) {    
        this.id=id;
        this._class = _class; 
        this.contents=contents;   
            
    }    
} 

public static boolean isAllUpper(String s) {
  for(char c : s.toCharArray()) {
    if(c=='\"'){
       continue;
      }
    else
      {
        if(Character.isLetter(c) && Character.isLowerCase(c) ) {
          System.out.println(s);
          return false;
       }
      }
     
  }
  return true;
}

  public void c_lexer() throws FileNotFoundException {
    Character[] letter_chars = new Character[]{'a','b','c','d','e','f','g','h','i' ,'j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
    Character[] numbers1 = new Character[]{'0','2','1','3','4','5','6','7','8','9'};
    String tokenDec[] = { "arr" };
    String tokenPCall[]={"call"};
    String tokenTypeIdentifiers[] = { "num", "bool", "string" };
    String tokenKeywords[] = { "main", "if", "while", "not", "then", "else", "do", "until", "while", "output", "halt", "return", "proc" };
    String tokenBinOperators[] = { "and", "or", "eq", "larger", "add", "sub", "mult" };
    String tokenUnaryOperators[] = { "input", "not" };
    String tokenConstants[] = { "true", "false" };
    Character tokenSymbols[] = { ';', '{', '}', '[', ']', '(', ')', ',' };
    String token_assignOperator = ":=";

    List<Character> letter = new ArrayList<>(Arrays.asList(letter_chars));
    List<Character> numbers = new ArrayList<>(Arrays.asList(numbers1));
    List<Character> _tokenSymbols = new ArrayList<>(Arrays.asList(tokenSymbols));
    
    final  ArrayList<token> Validtokens = new ArrayList<token>();
    int Id_num=0;
      final  ArrayList<String> OutputArr = new ArrayList<String>();
      int OutputArr_Count=0;

    // pass the path to the file as a parameter
    File file = new File("input.txt");
    Scanner sc = new Scanner(file);
    int counter = 0;
     String store = "";
  //  final  ArrayList<String> store = new ArrayList<String>();
   int lineNumber=1;
    while (sc.hasNextLine()) {
      String str = sc.nextLine();

      System.out.println(str);
      char[] myChar = str.toCharArray();
      int linelength=myChar.length;
      // do something
      
      
      for (int i = 0; i < myChar.length; i++) {
          System.out.println(" the char "+myChar[i]  + " number" + i);
         if(myChar[i] != ' ' && myChar[i] != '=' && myChar[i] != ':' && myChar[i] != '0' &&  !numbers.contains(myChar[i]) && myChar[i] != '\"' && !(letter.contains(myChar[i]) || numbers.contains(myChar[i]) || _tokenSymbols.contains(myChar[i]) ))
        {
            System.out.println("Undefined Symbol: " + myChar[i] + " in position " + (i) );         
        }

        else if (myChar[i] == '\"') {// CHECKING FOR A VALID STRING
          int count_string_leng=0;
          i++;// the first "
          store+='"'; // storing the first "
            for(int ii=i;ii<myChar.length;ii++)
            {
              if(myChar[ii]=='\"')
                { store+='\"';
                  Boolean status= isAllUpper(store);
                  if(status==true)
                  {
                    if(count_string_leng>15)
                    {
                      System.out.println("LEXICAL ERROR shortstring must be less than 15 characters"+ " On lineNumber "+ lineNumber);
                      i=ii;// updating the curser
                      
                      break;
                    }
                    else
                    {
                      OutputArr.add(store);
                      Id_num++;
                      token obj=new token(Id_num,"Shotstring", store);
                      Validtokens.add(obj);
                      OutputArr_Count++;
                      i=ii; //updating the curser
                      System.out.println("( Token number: " + (Id_num) + " ,Short String, " +  store + ")   line number:"+lineNumber );
                      break;
                    }
                                    
                  }
                  else
                  {
                    System.out.println("LEXICAL ERROR exepected short string "+ " On lineNumber "+ lineNumber);
                    i=ii;// updating the curser
                    break;
                  }
                 
                }
                else
                {
                  store+= myChar[ii];               
                  count_string_leng++;
                }
            }
            if(store.charAt(store.length()-1)!='\"')
            { 
              System.out.println("LEXICAL ERROR exepected short string  with no second inveted comma "+ " On lineNumber "+ lineNumber);
            }
        }
      else if (letter.contains(myChar[i])==true )
        {
            if(myChar[i]=='i')
              {  
                    if(i+4 <linelength && myChar[i] == 'i' && myChar[i+1] == 'n' && myChar[i+2]=='p' && myChar[i+3]=='u' && myChar[i+4]=='t')
                    {
                      Id_num++;
                      token obj=new token(Id_num,"UnaryOperator", store);
                      Validtokens.add(obj);
                      OutputArr_Count++;
                      
                      System.out.println("( Token number: "+(Id_num)+" , UnaryOperator, input )");
                      i=i+4;  //updating the curser
                      
                    }
                    else if( (i+1) <linelength && myChar[i+1] == 'f' )
                    {
                      if (i + 2 < linelength)
                      {
                        if(myChar[i+2]==' ')
                        { store="if";
                          Id_num++;
                          token obj=new token(Id_num,"Keyword", store);
                          Validtokens.add(obj);
                          
                          System.out.println("( Token: " + Id_num + ",Keyword, if ) ");
                          i+=2;
                        }
                        else if (_tokenSymbols.contains(myChar[i+2]  ))
                        {
                          store="if";
                          Id_num++;
                          token obj=new token(Id_num,"Keyword", store);
                          Validtokens.add(obj);
                          System.out.println("( Token: " + Id_num + ",Keyword, if ) ");
                          i+=2;
                          store="";
                          Id_num++;
                          System.out.println("( Token: "+(Id_num)+" , tokenSymbols, "+myChar[i] + "  )");
                          store+=myChar[i];
                 
                          token obj1=new token(Id_num,"Keyword", store);
                          Validtokens.add(obj1);
                   

                        }

                      }
                    }
      
              }
               else if( i+3< linelength &&  myChar[i] == 't' && myChar[i+1] == 'h' && myChar[i+2]=='e' && myChar[i+3]=='n')
               {
                store="then";
                Id_num++;
                token obj=new token(Id_num,"tokenKeyword", store);
                Validtokens.add(obj);
      
                System.out.println("( Token "+(Id_num)+" ,Keyword, then ) ");
                i=i+3;
               
              }
              else if(i+4 < linelength &&  myChar[i] == 'h' && myChar[i+1] == 'a' && myChar[i+2]=='l' && myChar[i+3]=='t' && myChar[i+4]==' ')
               {
                store="halt";
                Id_num++;
                token obj=new token(Id_num,"tokenKeyword", store);
                Validtokens.add(obj);
      
                System.out.println("( Token "+(Id_num)+" , Special Command, halt ) ");
                i=i+4;
            }
            else if(i+4 < linelength && myChar[i] == 'p' && myChar[i+1] == 'r' && myChar[i+2]=='o' && myChar[i+3]=='c' && myChar[i+4]==' ')
            {
      
                store="proc";
                Id_num++;
                token obj=new token(Id_num,"tokenKeyword", store);
                Validtokens.add(obj);
      
              System.out.println("( Token "+(Id_num)+" , Procedure, proc )");
              i=i+4;
             
           }
            else if( i+5< linelength && myChar[i] == 'w' && myChar[i+1] == 'h' && myChar[i+2]=='i' && myChar[i+3]=='l' &&  myChar[i+4]=='e' && myChar[i+5]==' ')
            {
      
              store="while";
              Id_num++;
              token obj=new token(Id_num,"tokenKeyword", store);
              Validtokens.add(obj);
      
              System.out.println("( Token "+(Id_num)+" , Keyword, while )");
              i=i+5;
             
            }
            else if(i+6< linelength && myChar[i] == 'o' && myChar[i+1] == 'u' && myChar[i+2]=='t' && myChar[i+3]=='p' && myChar[i+4]=='u' && myChar[i+5]=='t' && myChar[i+6]==' ')
            {
      
              store="output";
              Id_num++;
              token obj=new token(Id_num,"tokenKeyword", store);
              Validtokens.add(obj);
      
              System.out.println("( Token "+(Id_num)+" , IO-Command, output)");
              i=i+6;
      
          }
          else if(i+3< linelength && myChar[i] == 'a' && myChar[i+1] == 'n' && myChar[i+2]=='d' &&( myChar[i+3]==' '  || myChar[i+3]=='(' ))
          {
          
            store="and";
            Id_num++;
            token obj=new token(Id_num,"tokenBinOperator", store);
            Validtokens.add(obj);
            System.out.println("( Token "+(Id_num)+" , Boolean Operator, and )");
      
            store="(";
            Id_num++;
            token obj1=new token(Id_num,"tokenSymbol", store);
            Validtokens.add(obj1);
      
            System.out.println(" Token "+(Id_num)+" , tokenSymbol, (   ");
            
            i=i+3;
      
        }
        else if(i+2< linelength && myChar[i] == 'o' && myChar[i+1] == 'r' && (myChar[i+2]==' ' || myChar[i+2]=='(') )
        {
         
          store="or";
          Id_num++;
          token obj=new token(Id_num,"tokenBinOperator", store);
          Validtokens.add(obj);
          System.out.println("( Token "+(Id_num)+" , tokenBinOperator, or )");
      
          if(myChar[i+2]=='(')
          {
            store="(";
            Id_num++;
            token obj1=new token(Id_num,"tokenSymbol", store);
            Validtokens.add(obj1);
        
            System.out.println(" Token "+(Id_num)+" , tokenSymbol, (   ");
          }
         
          i=i+2;
         
        }
        else if(i+3< linelength && myChar[i] == 'n' && myChar[i+1] == 'o' && myChar[i+2]=='t' && (myChar[i+3]==' ' ||myChar[i+3]=='('))
        {
        
      
        store="not";
        Id_num++;
        token obj=new token(Id_num,"tokenBinOperator", store);
        Validtokens.add(obj);
        System.out.println("( Token "+(Id_num)+" , tokenBinOperator, not )");
      
        if(myChar[i+3]=='(')
        {
          store="(";
          Id_num++;
          token obj1=new token(Id_num,"tokenSymbol", store);
          Validtokens.add(obj1);
      
          System.out.println(" Token "+(Id_num)+" , tokenSymbol, (   ");
        }
      
      
          i=i+3;
      
        }
        else if((i+3) < linelength && myChar[i] == 'a' && myChar[i+1] == 'd' && myChar[i+2]=='d' && (myChar[i+3]==' ' || myChar[i+3]=='('))
        {
         store="add";
          Id_num++;
          token obj=new token(Id_num,"tokenBinOperator", store);
          Validtokens.add(obj);
          System.out.println("( Token "+(Id_num)+" , tokenBinOperator, add )");
      
            
          if(myChar[i+3]=='(')
          {
            store="(";
            Id_num++;
            token obj1=new token(Id_num,"tokenSymbol", store);
            Validtokens.add(obj1);
      
            System.out.println(" Token "+(Id_num)+" , tokenSymbol, (   ");
          }
          i=i+3;
      
        }
        else if(i+3 < linelength && myChar[i] == 's' && myChar[i+1] == 'u' && myChar[i+2]=='b' &&  (myChar[i+3]==' ' || myChar[i+3]=='('))
        {
        
          store="sub";
          Id_num++;
          token obj=new token(Id_num,"tokenBinOperator", store);
          Validtokens.add(obj);
          System.out.println("( Token "+(Id_num)+" , tokenBinOperator, sub )");
              
          if(myChar[i+3]=='(')
          {
            store="(";
            Id_num++;
            token obj1=new token(Id_num,"tokenSymbol", store);
            Validtokens.add(obj1);
      
            System.out.println(" Token "+(Id_num)+" , tokenSymbol, (   ");
          }
            i=i+3;
        }
        else if(i+4< linelength && myChar[i] == 'm' && myChar[i+1] == 'u' && myChar[i+2]=='l'  && myChar[i+3]=='t' &&   (myChar[i+4]==' ' || myChar[i+4]=='('))
        {
          
          store="mult";
          Id_num++;
          token obj=new token(Id_num,"tokenBinOperator", store);
          Validtokens.add(obj);
          System.out.println("( Token "+(Id_num)+" , tokenBinOperator, mult )");
              
      
          if(myChar[i+4]=='(')
          {
            store="(";
            Id_num++;
            token obj1=new token(Id_num,"tokenSymbol", store);
            Validtokens.add(obj1);
      
            System.out.println(" Token "+(Id_num)+" , tokenSymbol, (   ");
          }
          i=i+4;
         
        }
        else if(i+2< linelength && myChar[i] == 'e' && myChar[i+1] == 'q' && (myChar[i+2]==' ' || myChar[i+2]=='(') )
        {
         
          store="eq";
          Id_num++;
          token obj=new token(Id_num,"tokenBinOperator", store);
          Validtokens.add(obj);
          System.out.println("( Token "+(Id_num)+" , tokenBinOperator, eq )");
      
          if(myChar[i+2]=='(')
          {
            store="(";
            Id_num++;
            token obj1=new token(Id_num,"tokenSymbol", store);
            Validtokens.add(obj1);
        
            System.out.println(" Token "+(Id_num)+" , tokenSymbol, (   ");
          }
         
          i=i+2;
         
        }
        else if(i+6 < linelength && myChar[i] == 'l' && myChar[i+1] == 'a' && myChar[i+2]=='r'&&  myChar[i+3]=='g' &&  myChar[i+4]=='e' &&  myChar[i+5]=='r' && (myChar[i+6]==' ' || myChar[i+6]=='('))
        {//larger
         
          store="larger";
          Id_num++;
          token obj=new token(Id_num,"tokenBinOperator", store);
          Validtokens.add(obj);
          System.out.println("( Token "+(Id_num)+" , tokenBinOperator, larger )");
      
          if(myChar[i+6]=='(')
          {
            store="(";
            Id_num++;
            token obj1=new token(Id_num,"tokenSymbol", store);
            Validtokens.add(obj1);
        
            System.out.println(" Token "+(Id_num)+" , tokenSymbol, (   ");
          }
         
          i=i+6;
         
        }
        else if(i+4< linelength && myChar[i] == 't' && myChar[i+1] == 'r'&& myChar[i+2] == 'u' && myChar[i+3] == 'e'&& (myChar[i+4]==' ') )
        {
         
          store="true";
          Id_num++;
          token obj=new token(Id_num,"tokenConstants", store);
          Validtokens.add(obj);
          System.out.println("( Token "+(Id_num)+" , tokenConstants, true )");
      
         
          i=i+4;
         
        }
        else if(i+5< linelength && myChar[i] == 'f' && myChar[i+1] == 'a'&& myChar[i+2] == 'l' && myChar[i+3] == 's'&& (myChar[i+4]=='e') && (myChar[i+5]==' ') )
        {
         
          store="false";
          Id_num++;
          token obj=new token(Id_num,"tokenConstants", store);
          Validtokens.add(obj);
          System.out.println("( Token "+(Id_num)+" , tokenConstants, false )");
      
         
          i=i+5;
         
        }
        else if(i+3< linelength && myChar[i] == 'a' && myChar[i+1] == 'r'&& myChar[i+2] == 'r' && myChar[i+3] == ' ' )
        {
         
          store="arr";
          Id_num++;
          token obj=new token(Id_num,"tokenDec", store);
          Validtokens.add(obj);
          System.out.println("( Token "+(Id_num)+" , tokenDec, arr )");
      
         
          i=i+3;
         
        }
        else if(i+3< linelength && myChar[i] == 'n' && myChar[i+1] == 'u'&& myChar[i+2] == 'm' && ( myChar[i+3] == ' ' || myChar[i+3] == '[' ) )
        {
         
          store="num";
          Id_num++;
          token obj=new token(Id_num,"tokenTypeIdentifiers", store);
          Validtokens.add(obj);
          System.out.println("( Token "+(Id_num)+" , tokenTypeIdentifiers, num )");
          if(myChar[i+3]=='[')
          {
            store="[";
            Id_num++;
            token obj1=new token(Id_num,"tokenSymbol", store);
            Validtokens.add(obj1);
        
            System.out.println(" Token "+(Id_num)+" , tokenSymbol, [   ");
          }
         
          i=i+3;
         
        }
      
        else if(i+4< linelength && myChar[i] == 'b' && myChar[i+1] == 'o'&& myChar[i+2] == 'o' && myChar[i+3] == 'l' && ( myChar[i+4] == ' ' || myChar[i+4] == '[' ) )
        {
         
          store="bool";
          Id_num++;
          token obj=new token(Id_num,"tokenTypeIdentifiers", store);
          Validtokens.add(obj);
          System.out.println("( Token "+(Id_num)+" , tokenTypeIdentifiers, bool )");
          if(myChar[i+4]=='[')
          {
            store="[";
            Id_num++;
            token obj1=new token(Id_num,"tokenSymbol", store);
            Validtokens.add(obj1);
        
            System.out.println(" Token "+(Id_num)+" , tokenSymbol, [   ");
          }
         
          i=i+4;
         
        }
      
        
        else if(i+6< linelength && myChar[i] == 's' && myChar[i+1] == 't'&& myChar[i+2] == 'r' && myChar[i+3] == 'i' && myChar[i+4] == 'n'  && myChar[i+5] == 'g' && ( myChar[i+6] == ' ' || myChar[i+6] == '[' ) )
        {
         
          store="bool";
          Id_num++;
          token obj=new token(Id_num,"tokenTypeIdentifiers", store);
          Validtokens.add(obj);
          System.out.println("( Token "+(Id_num)+" , tokenTypeIdentifiers, bool )");
          if(myChar[i+6]=='[')
          {
            store="[";
            Id_num++;
            token obj1=new token(Id_num,"tokenSymbol", store);
            Validtokens.add(obj1);
        
            System.out.println(" Token "+(Id_num)+" , tokenSymbol, [   ");
          }
         
          i=i+6;
         
        }
        else if(i+4< linelength && myChar[i] == 'c' && myChar[i+1] == 'a'&& myChar[i+2] == 'l' && myChar[i+3] == 'l' && myChar[i+4] == ' ' )
        {
         
          store="arr";
          Id_num++;
          token obj=new token(Id_num,"tokenDec", store);
          Validtokens.add(obj);
          System.out.println("( Token "+(Id_num)+" , tokenDec, arr )");
      
         
          i=i+4;
         
        }
        else
            {
                
                    {store="";
              

                        while (i < linelength){// Must  check if its only lower case the user define name
                              if (numbers.contains(myChar[i]) ||  letter.contains(myChar[i])){
                                    store +=myChar[i];   

                                    i = i + 1;
                              }
                                else
                                { i--;
                                  break;
                                }                            
                            }
                        Boolean status=isAllUpper(store);
                        if(status==false)
                          {
                                System.out.println("( " + (Id_num+1) + ",userDefinedName, " + store + "  ) "); //Not sure if i have to store it in the token...
                                Id_num++;
                                token obj1=new token(Id_num,"userDefinedName", store);
                                Validtokens.add(obj1);
                          }
                          else
                            {
                                System.out.println("LEXICAL ERROR exepected userDefinedName "+ " On lineNumber: "+ lineNumber +" character number: "+ i);
                             //    System.out.println("SINGENE LANA!!!!!!!!!!!!!!!  "+ store);
                            }
                             
                              
                    }
            }
        }
        else if(_tokenSymbols.contains(myChar[i]))
        { if(myChar[i]=='=')
          {
            System.out.println("Error! The symbol '=' has no meaning on its own. " + "Must be paired/concatinated with '='  , should be := " );
      
          } 
          else if(myChar[i]==':')
          {
            if(( i+2 <linelength) &&  myChar[i+1]=='=')
            {
              store=":=";
              Id_num++;
              token obj=new token(Id_num,"token_assignOperator", store);
              Validtokens.add(obj);
              System.out.println("( Token "+(Id_num)+" , token_assignOperator, := )");
                      
              i=i+1;
            }
            else if(( i+1 <linelength) &&  myChar[i+1]==' ')
            {
              System.out.println("Error! The symbol ':' has no meaning on its own. " + "Must be paired/concatinated with '=' , should be := " );
              i +=1;
            }
          }else
          {
            store="";
            store+=myChar[i];
            Id_num++;
            token obj=new token(Id_num,"tokenSymbol", store);
            Validtokens.add(obj);
   
            System.out.println("( Token: " + Id_num + ",tokenSymbol, "+store+ " ) ");

          }
         

        }
        else if(numbers.contains(myChar[i]))
        {
          int posCheck = i;
          store="";
          int error=0;

          while( (posCheck+1 < linelength) &&  (myChar[posCheck+1]!= ' ') ){// Validating the number
              posCheck = posCheck + 1;
              if (myChar[posCheck]!=' ' &&   !numbers.contains(myChar[posCheck]) &&  letter.contains(myChar[posCheck]) && ! _tokenSymbols.contains(myChar[posCheck])){
                  System.out.println("Error: A digit may not contain a letter. See position: " + posCheck + " line number "+ lineNumber);
                  error=1;
              }
          }

          if(error==0)
          {
            if(myChar[i] == '0')
            {
                if(numbers.contains(myChar[i+1]))
                {
                  System.out.println("Error, integer cannont start with zero and not be zero. See position: " + i );
                  
                }
                else
                  {
                    store="0";
                
                    Id_num++;
                    token obj=new token(Id_num,"tokenSymbol", store);
                    Validtokens.add(obj);

                      System.out.println("( Token " + (Id_num)+" ,Integer, " + myChar[i] + " )");
                     
                      i++;
                  }
             }
             else
             {
                  while (i < linelength)
                  {
                      if (numbers.contains(myChar[i]))
                      {
                          store  += myChar[i];
                          i = i+1;
                      }
                      else
                          break;
                  }
                      Id_num++;
                        token obj=new token(Id_num,"Integer", store);
                        Validtokens.add(obj);
                  System.out.println("( Token " + (Id_num)+" ,Integer, " + store + " )");
                
              }

         
          }

        }
      


  
        // System.out.println(myChar[i] + " " + i);
        store="";
      }
      System.out.println();
      System.out.println("#############################################");
      System.out.println("Line number: "+lineNumber +" Done.");
      System.out.println();
      System.out.println();

      lineNumber++;
    }

  }

}