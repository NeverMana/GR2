import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IfTable {
    private Map<String, IfStatus> ifList;
    private List<Thread> threads;

    public IfTable(){
        this.ifList = new HashMap<String, IfStatus>();
        this.threads = new ArrayList<Thread>();
    }

    public void addInterface(String mac, int index){
        IfStatus itf = new IfStatus(mac,index);
        Thread t = new Thread(itf);
        threads.add(t);
        ifList.put(mac, itf);
        t.start();
    }

    public ArrayList<String> ifListToString(){
        ArrayList<String> res = new ArrayList<String>();
        for(IfStatus i: ifList.values()){
            res.add(i.getPhysAddress());
        }
        return res;
    }
}
