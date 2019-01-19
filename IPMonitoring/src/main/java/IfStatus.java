public class IfStatus implements Runnable{
    private int index;
    private double inOctets;
    private double outOctets;
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

    public void run(){
    }
}
