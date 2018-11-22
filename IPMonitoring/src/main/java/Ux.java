import java.io.IOException;
import java.util.Scanner;

public class Ux {
    int op;
    Scanner input;
    SNMPClient client;

    public Ux(){
        op=6;
        input = new Scanner(System.in);
    }
    public static void main(String[] args) throws IOException {
        Ux u = new Ux();
        u.setupAgente();
        while(u.op!=0){
            u.displayMenu();
            u.changeOption();
        }
    }

    public void displayMenu(){
        System.out.println("Select operation");
        System.out.println("1: snmpwalk");
        System.out.println("2: snmpget");
        System.out.println("3: snmpgetnext");
        System.out.println("4: mudar ip agente");
        System.out.println("5: mudar porta");
        System.out.println("0: sair");
        op = input.nextInt();
    }

    public void changeOption(){
        switch (op){
            case 0: return;
            case 1: {
                startWalk();
            }
            case 2: {
                startGet();
            }
            case 3: {
                startGetNext();
            }
            case 4: {
                ipChange();
            }
            case 5: {
                portChange();
            }
        }
    }
    public void startWalk(){

    }
    public void startGet(){

    }
    public void startGetNext(){

    }
    public void ipChange(){

    }
    public void portChange(){

    }

    public void setupAgente(){
        String add, ip, porta;
        System.out.println("Indique ip do agente");
        ip = input.nextLine();
        System.out.println("Indique porta");
        porta = input.nextLine();
        add = "udp:"+ip+"/"+porta;
        client = new SNMPClient(add);
    }
}
