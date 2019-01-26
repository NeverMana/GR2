import java.io.IOException;
import java.util.*;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeUtils;

import static java.lang.Integer.parseInt;

public class SNMPClient {

    Snmp snmp = null;
    public String address ;
    CommunityTarget target;
    //IfTable interfaces;

    private Map<String, IfStatus> ifList;
    private Map<String, List<Double>> ifTrafficLog;
    private Map<String, Thread> threadMap;

    //Construtores
    public SNMPClient() {
        address = "127.0.0.1/24";
        target = new CommunityTarget();
        //interfaces = new IfTable();
        configTarget();

        this.ifList = new HashMap<String, IfStatus>();
        this.ifTrafficLog = new HashMap<String, List<Double>>();
    }
    public SNMPClient(String add) {
        address = add;
        target = new CommunityTarget();
        //interfaces = new IfTable();
        configTarget();

        this.ifList = new HashMap<String, IfStatus>();
        this.ifTrafficLog = new HashMap<String, List<Double>>();
    }

    //Métodos de configuração do target snmp
    public void setIp(String ip){
        String[] split = address.split("/");
        address = "udp:" + ip + "/" + split[1];
        configTarget();

    }
    public void setPort(String port){
        String[] split = address.split("/");
        address = split[0] + "/" + port;
        configTarget();
    }
    private void configTarget() {
        target.setCommunity(new OctetString("public"));
        target.setAddress(GenericAddress.parse(address));
        target.setRetries(2);
        target.setTimeout(1500);
        target.setVersion(SnmpConstants.version2c);
    }
    public void start() throws IOException {
        System.out.println("Starting client");
        TransportMapping transport = new DefaultUdpTransportMapping();
        snmp = new Snmp(transport);
        transport.listen();
    }




    //TODO get interfaces

    /*
    IFtableentry .1.3.6.1.2.1.2.2.1
    mac .6
    inoctets .10
    outoctets .16
     */
    public void fillIfTable(){
        Map<String, String> result = doWalk(".1.3.6.1.2.1.2.2"); // ifTable, mib-2 interfaces
        String key;

        for (Map.Entry<String, String> entry : result.entrySet()) {
            key = entry.getKey();
            if (key.startsWith(".1.3.6.1.2.1.2.2.1.6")) {
                int parseInt = parseInt(key.substring(key.length() - 1)); //get index
                ifList.put(entry.getValue(), new IfStatus(entry.getValue(),parseInt));
            }
        }
    }

    public void startMonitoring(){
        IfStatus.setSnmp(this);
        for(IfStatus i: ifList.values()){
            Thread t = new Thread(i);
            threadMap.put(i.getPhysAddress(),t);
            t.start();
        }
    }

    public void getTraffic(String mac, int index){
        Map<String, String> result = doWalk(".1.3.6.1.2.1.2.2"); // ifTable, mib-2 interfaces
        String key;
        double inOctets=0,outOctets=0;
        Double traffic;

        for (Map.Entry<String, String> entry : result.entrySet()) {
            key = entry.getKey();
            if (key.startsWith(".1.3.6.1.2.1.2.2.1.10."+index)) {
                inOctets = parseInt(entry.getValue());

            } else if (key.startsWith(".1.3.6.1.2.1.2.2.1.16."+index)) {
                outOctets = parseInt(entry.getValue());
            }
        }
        traffic = inOctets - outOctets;
        setTraffic(mac, traffic);

    }

    public void setTraffic(String mac, Double traffic){
        List<Double> list = ifTrafficLog.get(mac);
        if(list.size() == 10){
            list.remove(0);
        }
        list.add(traffic);
        ifTrafficLog.put(mac,list);
    }







    //Função que inicia o get
    public String getAsString(String oid) throws IOException {
        OID obj = new OID(oid);
        ResponseEvent event = get(new OID[] { obj });
        return event.getResponse().get(0).getVariable().toString();
    }

    private ResponseEvent get(OID oids[]) throws IOException {
        PDU pdu = new PDU();
        for (OID oid : oids) {
            pdu.add(new VariableBinding(oid));
        }
        pdu.setType(PDU.GET);
        ResponseEvent event = snmp.send(pdu, target, null);
        if(event != null) {
            return event;
        }
        throw new RuntimeException("GET timed out");
    }

    public Map<String, String> doWalk(String tableOid) {
        Map<String, String> result = new TreeMap<String, String>();
        TreeUtils treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
        List<TreeEvent> events = treeUtils.getSubtree(target, new OID(tableOid));
        if (events == null || events.size() == 0) {
            System.out.println("Error: Unable to read table!");
            return result;
        }

        for (TreeEvent event : events) {
            if (event == null) {
                continue;
            }
            if (event.isError()) {
                System.out.println("Error: table OID [" + tableOid + "] " + event.getErrorMessage());
                continue;
            }

            VariableBinding[] varBindings = event.getVariableBindings();
            if (varBindings == null || varBindings.length == 0) {
                continue;
            }
            for (VariableBinding varBinding : varBindings) {
                if (varBinding == null) {
                    continue;
                }

                result.put("." + varBinding.getOid().format(), varBinding.getVariable().toString());
            }

        }

        return result;
    }

    public ArrayList<String> interfacesToString(){
        ArrayList<String> res = new ArrayList<String>();
        for(IfStatus i: ifList.values()){
            res.add(i.getPhysAddress());
        }
        return res;
    }
}
