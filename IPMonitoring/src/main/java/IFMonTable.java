import java.util.*;

public class IFMonTable {
    Map<String, SNMPClient> clients;

    public IFMonTable (){
        clients = new HashMap<String, SNMPClient>();
    }

    public void add (String add){
        SNMPClient a = new SNMPClient(add);
        clients.put(add,a);
    }
    public SNMPClient remove (String add){
        return clients.remove(add);
    }
    public void get(String add, String oid){

    }
    public void walk(String add, String oid){

    }
}
