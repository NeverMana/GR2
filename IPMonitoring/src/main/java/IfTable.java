import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IfTable {
    private Map<String, IfStatus> ifList;
    private Map<String, List<Double>> ifTrafficLog;

    public IfTable(){
        this.ifList = new HashMap<String, IfStatus>();
        this.ifTrafficLog = new HashMap<String, List<Double>>();
    }

    public void addInterface(String mac, int index){
        ifList.put(mac, new IfStatus(mac,index));

    }

    public ArrayList<String> ifListToString(){
        ArrayList<String> res = new ArrayList<String>();
        for(IfStatus i: ifList.values()){
            res.add(i.getPhysAddress());
        }
        return res;
    }
}
