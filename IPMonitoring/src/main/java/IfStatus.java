import java.util.List;

public class IfStatus implements Runnable{
    private int index;
    private double inOctets;
    private double outOctets;
    private String macAddress;
    private int polling, minInterval, maxInterval; //em segundos, min interval \ | val | /   \ / :: max interval | | :: min interval
    public List<Double> log;

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
        Double cur = new Double(1);

        /*
        * Como fazer get?
        * Depois de get guardar valores de trafego em log
        */
        checkPolling(cur);

    }

    private void checkPolling(double cur){
        Double last = log.get(log.size());
        if(last + minInterval > cur || last - minInterval < cur){
            updatePolling(false);
        }
        else if( last + maxInterval < cur || last - maxInterval > cur){
            updatePolling(true);
        }
    }

    private void updatePolling(boolean more){
        if(more){
            polling *=2;
        }
        else{
            polling /=2;
        }
    }

}
