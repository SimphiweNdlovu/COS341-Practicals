import java.util.ArrayList;

public class main
{
  public static void main (String[] args) throws Exception
  {
    lexer obj=new lexer();
   
    Parse obj1=new Parse();
    ArrayList<lexer.token> arrayList = new ArrayList<lexer.token>();
    arrayList= obj.c_lexer();
   node root= obj1.GoParse(arrayList);
   SemanticAnalyzer obj2=new SemanticAnalyzer();
    obj2.GOSemanticAnalyzer(root);
    
  }

}