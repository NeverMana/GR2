import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class Ux {
    private int op;
    private Scanner input;
    private IFMonTable clients;

    public Ux(){
        op=7;
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
        System.out.println("=====SNMP MANAGER=====");
        System.out.println("===Select operation===");
        System.out.println("1: Do snmpwalk");
        System.out.println("2: Do snmpget");
        System.out.println("3: Do snmpgetnext");
        System.out.println("4: Change IP");
        System.out.println("5: Change port");
        System.out.println("6: Change IP and Port");
        System.out.println("7: Add new SNMP agent");
        System.out.println("0: Exit");
        System.out.println("======================");
        op = Integer.parseInt( input.nextLine() );
    }

    private void changeOption(){
        String oid;
        switch (op) {
            case 0:
                break;
            case 1: {
                oid = selectOID();
                startWalk(oid);
                break;
            }
            case 2: {
                oid = selectOID();
                startGet(oid);
                break;
            }
            case 3: {
                oid = selectOID();
                startGetNext(oid);
                break;
            }
            case 4: {
                ipChange();
                break;
            }
            case 5: {
                portChange();
                break;
            }
            case 6: {
                ipChange();
                portChange();
                break;
            }
        }
    }

    private String selectOID(){
        System.out.println("Insert intended object ID:");
        return input.nextLine();
    }

    private void startWalk(String oid){
        Map<String, String> res;
        try {
            res = agent.doWalk(oid);
            printResults(res);
        } catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    private void printResults(@NotNull Map<String,String> res){
        String value;
        for (String oid: res.keySet() ) {
            value = res.get(oid);
            System.out.println(oid + " :: " + value );
        }
    }

    private void startGet(String oid){
        try {
            String value = agent.getAsString(oid);
            System.out.println(oid + " :: " + value );
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void startGetNext(String oid){

    }

    private void ipChange(){
        String ip;
        System.out.println("Select IP");
        ip = input.nextLine();
        agent.setIp(ip);
    }

    private void portChange(){
        String port;
        System.out.println("Select Port");
        port = input.nextLine();
        agent.setPort(port);
    }

    private void setupClient(){
        String add, ip, port;
        System.out.println("Select IP");
        ip = input.nextLine();
        System.out.println("Select Port");
        port = input.nextLine();
        add = "udp:"+ip+"/"+port;
        agent = new SNMPClient(add);
        try {
            agent.start();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
