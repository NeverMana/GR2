import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

import static java.lang.Integer.parseInt;

public class Ux {
    private int op;
    private Scanner input;
    private static SNMPClient client;

    public Ux(){
        op=9;
        input = new Scanner(System.in);
        client = new SNMPClient();
    }
    public static void main(String[] args) throws IOException {
        Ux u = new Ux();
        while(u.op!=0){
            u.displayMenu();
            u.changeOption();
        }
    }

    private void displayMenu(){
        System.out.println("=====SNMP MANAGER=====");
        System.out.println("===Select operation===");
        System.out.println("1: Find Interfaces");
        System.out.println("2: Display Traffic");
        System.out.println("3: Config Client");
        System.out.println("4: Display Interfaces");
        System.out.println("0: Exit");
        System.out.println("======================");
        op = parseInt( input.nextLine() );
    }

    private void changeOption(){
        String oid;
        switch (op) {
            case 0:
                client.killAll();
                break;
            case 1: {
                client.fillIfTable();
                break;
            }
            case 2: {
                client.startMonitoring();
                break;
            }
            case 3: {
                setupClient();
                break;
            }
            case 4: {
                Collection<List<Double>> a = client.getIfTrafficLog().values();
                ArrayList<String> res = client.interfacesToString();
                int i = 0;
                for(List<Double> l: a) {
                    printInterfaces(l, res.get(i));
                    i++;
                }
                break;
            }
        }
    }


    private String selectOID(){
        System.out.println("Insert intended object ID:");
        return input.nextLine();
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
            String value = client.getAsString(oid);
            System.out.println(oid + " :: " + value );
        } catch (IOException e) {
            System.out.println(e.getMessage());
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
        add = ip + "/" + port;
        client = new SNMPClient(add);
        try {
            client.start();
            client.fillIfTable();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    public void printInterfaces(List<Double> l, String s){
        System.out.println(s);

        for (Double d: l ){
            System.out.println(d);
        }
    }
}
