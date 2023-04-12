import java.util.ArrayList;

public class main
{
  public static void main(String[] args) throws Exception
  {
    lexer obj=new lexer();
   
    Parse obj1=new Parse();
    ArrayList<lexer.token> arrayList = new ArrayList<lexer.token>();
    arrayList= obj.c_lexer();
    obj1.GoParse(arrayList);
  }

}