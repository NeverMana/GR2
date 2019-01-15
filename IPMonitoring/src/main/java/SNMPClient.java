import java.io.IOException;
import java.util.Scanner;
import java.util.*;

import org.jetbrains.annotations.NotNull;
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
    public String address ;
    CommunityTarget target;


    public SNMPClient(String add) {
        address = add;
        target = new CommunityTarget();
        configTarget();
    }

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

    public String getAsString(String oid) throws IOException {
        OID obj = new OID(oid);
        ResponseEvent event = get(new OID[] { obj });
        return event.getResponse().get(0).getVariable().toString();
    }

    private ResponseEvent get(@NotNull OID oids[]) throws IOException {
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

/*
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
*/
}
