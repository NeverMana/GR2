import java.util.concurrent.TimeUnit;

public class IfStatus implements Runnable{
    private static SNMPClient snmp;

    private int index;
    private String macAddress;
    private int polling;


    public IfStatus(String mac, int index){
        this.macAddress = mac;
        this.index = index;
        this.polling = 5;
    }

    public void setPolling(int x){
        this.polling=x;
    }
    public String getPhysAddress(){
        return this.macAddress;
    }

    public static void setSnmp (SNMPClient a) { snmp = a; }

    public void run(){
        while(true) {
            snmp.getTraffic(macAddress, index);
            try {
                TimeUnit.SECONDS.sleep(polling);
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
