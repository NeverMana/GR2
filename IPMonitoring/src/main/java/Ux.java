import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class Ux {
    private int op;
    private Scanner input;
    private SNMPClient client;

    public Ux(){
        op=6;
        input = new Scanner(System.in);
    }
    public static void main(String[] args) throws IOException {
        Ux u = new Ux();
        u.setupClient();
        while(u.op!=0){
            u.displayMenu();
            u.changeOption();
        }
    }

    private void displayMenu(){
        System.out.println("Select operation");
        System.out.println("1: Do snmpwalk");
        System.out.println("2: Do snmpget");
        System.out.println("3: Do snmpgetnext");
        System.out.println("4: Change IP");
        System.out.println("5: Change port");
        System.out.println("6: Change IP and Port");
        System.out.println("0: Exit");
        op = input.nextInt();
    }

    private void changeOption(){
        String oid;
        switch (op){
            case 0: return;
            case 1: {
                oid = selectOID();
                startWalk(oid);
            }
            case 2: {
                oid = selectOID();
                startGet(oid);
            }
            case 3: {
                oid = selectOID();
                startGetNext(oid);
            }
            case 4: {
                ipChange();
            }
            case 5: {
                portChange();
            }
            case 6: {
                ipChange();
                portChange();
            }
        }
    }

    private String selectOID(){
        System.out.println("Insert intended object ID:");
        String oid = input.nextLine();
        return oid;
    }

    private void startWalk(String oid){
        client.walk(oid);
    }

    private void startGet(String oid){

    }

    private void startGetNext(String oid){

    }

    private void printWalk(@NotNull Map<String, String> result) throws IOException {

        //Isto assume que estamos a fazer walk no OID da ifTable
        for (Map.Entry<String, String> entry : result.entrySet()) {
            if (entry.getKey().startsWith(".1.3.6.1.2.1.2.2.1.2.")) {
                System.out.println("ifDescr" + entry.getKey().replace(".1.3.6.1.2.1.2.2.1.2", "") + ": " + entry.getValue());
            }
            if (entry.getKey().startsWith(".1.3.6.1.2.1.2.2.1.3.")) {
                System.out.println("ifType" + entry.getKey().replace(".1.3.6.1.2.1.2.2.1.3", "") + ": " + entry.getValue());
            }
        }
    }


    private void ipChange(){
        String ip;
        System.out.println("Select IP");
        ip = input.nextLine();
        client.setIp(ip);
    }

    private void portChange(){
        String port;
        System.out.println("Select Port");
        port = input.nextLine();
        client.setPort(port);
    }

    private void setupClient(){
        String add, ip, port;
        System.out.println("Select IP");
        ip = input.nextLine();
        System.out.println("Select Port");
        port = input.nextLine();
        add = "udp:"+ip+"/"+port;
        client = new SNMPClient(add);
        try {
            client.start();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
