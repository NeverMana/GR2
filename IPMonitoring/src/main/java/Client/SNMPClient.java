package Client;
import java.io.IOException;
import java.util.Scanner;
import java.util.*;


import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeUtils;

public class SNMPClient {

    Snmp snmp = null;
    String address ;

    public SNMPClient(String add)
    {
        address = add;
    }

    public static void main(String[] args) throws IOException {
        Scanner input;
        String add, ip, porta, sysDescr, oid;
        SNMPClient client;

        CommunityTarget target = new CommunityTarget();

        System.out.println("Indique ip do agente:");
        input = new Scanner(System. in);
        ip = input. nextLine(); //formato::  "127.0.0.1"
        System.out.println("Indique porta:");
        porta = input.nextLine();//int
        add = "udp:"+ip+"/"+porta;


        client = new SNMPClient(add);
        client.start();


        System.out.println("Indique OID:");
        /*
        * OIDs:
        * ".1.3.6.1.2.1.1.1.0" - sysdescription
        * ".1.3.6.1.2.1.2.2" ifTable
        */
        oid= input.nextLine();
        target = client.configTarget(target,add);
        int op=0;
        switch (op){
            case 0:{
                Map<String, String> result = client.doWalk(oid, target); // ifTable, mib-2 interfaces
                client.printWalk(result);
            }
            case 1:{
                String val = client.getAsString(new OID(oid));
                System.out.println(val);
            }
        }



    }

    private void printWalk(Map<String, String> result) throws IOException {

        for (Map.Entry<String, String> entry : result.entrySet()) {
            if (entry.getKey().startsWith(".1.3.6.1.2.1.2.2.1.2.")) {
                System.out.println("ifDescr" + entry.getKey().replace(".1.3.6.1.2.1.2.2.1.2", "") + ": " + entry.getValue());
            }
            if (entry.getKey().startsWith(".1.3.6.1.2.1.2.2.1.3.")) {
                System.out.println("ifType" + entry.getKey().replace(".1.3.6.1.2.1.2.2.1.3", "") + ": " + entry.getValue());
            }
        }
    }

    private CommunityTarget configTarget(CommunityTarget target, String add) {
        target.setCommunity(new OctetString("public"));
        target.setAddress(GenericAddress.parse(add));
        target.setRetries(2);
        target.setTimeout(1500);
        target.setVersion(SnmpConstants.version2c);
        return target;
    }

    /*
        TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
        Snmp snmp = new Snmp(transport);
        transport.listen();
     */

    public Map<String, String> doWalk(String tableOid, Target target) throws IOException {
        Map<String, String> result = new TreeMap<String, String>();

        TreeUtils treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
        List<TreeEvent> events = treeUtils.getSubtree(target, new OID(tableOid));
        if (events == null || events.size() == 0) {
            System.out.println("Error: Unable to read table...");
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

                result.put("." + varBinding.getOid().toString(), varBinding.getVariable().toString());
            }

        }

        return result;
    }

    private void start() throws IOException {
        TransportMapping transport = new DefaultUdpTransportMapping();
        snmp = new Snmp(transport);
        transport.listen();
    }


    public String getAsString(OID oid) throws IOException {
        ResponseEvent event = get(new OID[] { oid });
        return event.getResponse().get(0).getVariable().toString();
    }


    public ResponseEvent get(OID oids[]) throws IOException {
        PDU pdu = new PDU();
        for (OID oid : oids) {
            pdu.add(new VariableBinding(oid));
        }
        pdu.setType(PDU.GET);
        ResponseEvent event = snmp.send(pdu, getTarget(), null);
        if(event != null) {
            return event;
        }
        throw new RuntimeException("GET timed out");
    }

    private Target getTarget() {
        Address targetAddress = GenericAddress.parse(address);
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString("public"));
        target.setAddress(targetAddress);
        target.setRetries(2);
        target.setTimeout(1500);
        target.setVersion(SnmpConstants.version2c);
        return target;
    }

}
