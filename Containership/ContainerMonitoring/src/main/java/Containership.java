import org.snmp4j.agent.mo.DefaultMOFactory;

import java.io.IOException;

public class Containership {


    public static void main(String[] args) throws IOException, InterruptedException {
        Agent agent = new Agent("0.0.0.0/2001");
        agent.start();

        //TODO CREATE SCALAR AND TABLES

        while(true) {
            System.out.println("Agent running...");
            Thread.sleep(5000);
        }
    }
}
