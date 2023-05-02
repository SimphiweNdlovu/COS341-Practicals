import java.util.ArrayList;

import javax.management.AttributeList;

public class node {
     int id;
    String name;
    String type;// terminal or  non terminal
    ArrayList<node> children=new ArrayList<>();

    int newScope=0;
    int scopeID=0;
    void setScopeID(int scopeID){
        this.scopeID=scopeID;
    }
    int getScopeID(){
        return this.scopeID;
    }
    void setNewScope(int newScope){
        this.newScope=newScope;
    }
    int getNewScope(){
        return this.newScope;
    }

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
