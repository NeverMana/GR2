import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import static java.lang.Integer.parseInt;

public class Ux {
    private int op;
    private Scanner input;
    private String curAdd;
    private Map<String, SNMPClient> clients;

    public Ux(){
        op=9;
        input = new Scanner(System.in);
        clients = new HashMap<String, SNMPClient>();
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
        System.out.println("1: Display Traffic");
        System.out.println("2: Change IP");
        System.out.println("3: Change port");
        System.out.println("4: Change IP and Port");
        System.out.println("5: Add new SNMP Client");
        System.out.println("6: Choose Client");
        System.out.println("0: Exit");
        System.out.println("======================");
        op = parseInt( input.nextLine() );
    }

    private void changeOption(){
        String oid;
        switch (op) {
            case 0:
                break;
            case 1: {
                oid = selectOID();
                break;
            }
            case 2: {
                ipChange();
                break;
            }
            case 3: {
                portChange();
                break;
            }
            case 4: {
                ipChange();
                portChange();
                break;
            }
            case 5: {
                setupClient();
            }
            case 6: {
                selectClient();
            }
        }
    }
    private void selectClient(){
        int i = 0;
        ArrayList<String> adds = new ArrayList<String>();
        for (SNMPClient c: clients.values()) {
            System.out.println("ID:" + i + " , address:" + c.address );
            adds.add(i,c.address);
            i++;
        }
        op = parseInt( input.nextLine() );
        curAdd = clients.get(adds.get(op)).address;
    }

    private String selectOID(){
        System.out.println("Insert intended object ID:");
        return input.nextLine();
    }

   /* private void startWalk(String oid){
        Map<String, String> res;
        try {
            res = clients.get(curAdd).doWalk(oid);
            printResults(res);
        } catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }*/

    private void printResults(@NotNull Map<String,String> res){
        String value;
        for (String oid: res.keySet() ) {
            value = res.get(oid);
            System.out.println(oid + " :: " + value );
        }
    }

    private void startGet(String oid){
        try {
            String value = clients.get(curAdd).getAsString(oid);
            System.out.println(oid + " :: " + value );
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void ipChange(){
        String ip;
        System.out.println("Select IP");
        ip = input.nextLine();
        clients.get(curAdd).setIp(ip);
    }

    private void portChange(){
        String port;
        System.out.println("Select Port");
        port = input.nextLine();
        clients.get(curAdd).setPort(port);
    }

    private void setupClient(){
        String add, ip, port;
        System.out.println("Select IP");
        ip = input.nextLine();
        System.out.println("Select Port");
        port = input.nextLine();
        curAdd = "udp:"+ip+"/"+port;
        clients.put(curAdd,new SNMPClient(curAdd));
        try {
            clients.get(curAdd).start();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
