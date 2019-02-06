import org.snmp4j.smi.OID;

import java.io.IOException;

public class Containership {
    public Agent agent;
    //oid = 1.3.6.1.3.2019 :: CONTAINERSHIP\Q-MIB

    public Containership (String add) throws IOException{
        agent = new Agent(add);
    }

    public void startAgent() throws IOException,InterruptedException {
        agent.start();

        //TODO REGISTER MANAGED OBJECTS AND TABLES
        registerContainershipParam();

        while(true) {
            System.out.println("Agent running...");
            Thread.sleep(5000);
        }
    }

    public void registerContainershipParam(){
        String a;
        a = "1.3.6.1.2019.1.1.0";
        agent.registerManagedObject(MOScalarFactory.createReadWrite(new OID(a),""));
        a = "1.3.6.1.2019.1.2.0";
        agent.registerManagedObject(MOScalarFactory.createReadWrite(new OID(a),""));
        a = "1.3.6.1.2019.1.3.0";
        agent.registerManagedObject(MOScalarFactory.createReadWrite(new OID(a),""));
    }

    public static void main(String[] args) throws IOException, InterruptedException{
        String add = "0.0.0.0/6666";
        Containership containership = new Containership(add);
        containership.startAgent();
    }
}
