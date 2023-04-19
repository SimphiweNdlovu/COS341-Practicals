import java.util.ArrayList;

import javax.management.AttributeList;

public class node {
     int id;
    String name;
    String type;// terminal or  non terminal
    ArrayList<node> children=new ArrayList<>();

    public node(String name){
        this.name=name;
    }
    public node(String name,String type) {
        this.type=type;
        this.name = name;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void addChild(node child){
        this.children.add(child);
    }
}
