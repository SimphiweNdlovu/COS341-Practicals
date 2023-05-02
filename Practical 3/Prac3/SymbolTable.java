public class SymbolTable {
    int NodeId;
    String NodeName;
    int ScopeId;
    String ScopeName; //can Global or  scope that  is defined in

    Boolean called=false;
    int parentID;
/* EXAMPLE:

    h
    ,p1 {h,p2}
    The table:

    NodeId:     NodeName:   ScopeId:    ScopeName:
    3            p1          0           global

    5           p2           1           p1


So basically what the person was asking was if a proc is defined will it's scope be it's self 
or will it be the scope it is defined in, and since p2 was defined in p1 it's scope is p1 */
    SymbolTable(int NodeId, String NodeName, int ScopeId, String ScopeName){
        this.NodeId = NodeId;
        this.NodeName = NodeName;
        this.ScopeId = ScopeId;
        this.ScopeName = ScopeName;
    }

    

}
