package br.ufsm.psniffstat.buffer;

/**
 *
 * @author tuxtor
 */
public class CountersBuffer {

    private static int tcpAcc, udpAcc, icmpAcc, tcpAckAcc, tcpFinAcc, tcpSynAcc;
    
    public static synchronized void addTcpAcc(){
        tcpAcc++;
    }
    
    public static synchronized void addUdpAcc(){
        udpAcc++;
    }
    
    public static synchronized void addIcmpAcc(){
        icmpAcc++;
    }
    
    public static synchronized void addTcpAckAcc(){
        tcpAckAcc++;
    }
    
    public static synchronized void addTcpFinAcc(){
        tcpFinAcc++;
    }
    
    public static synchronized void addTcpSynAcc(){
        tcpSynAcc++;
    }

    public static synchronized void addTcpAcc(int qty){
        tcpAcc+=qty;
    }
    
    public static synchronized void addUdpAcc(int qty){
        udpAcc+=qty;
    }
    
    public static synchronized void addIcmpAcc(int qty){
        icmpAcc+=qty;
    }
    
    public static synchronized void addTcpAckAcc(int qty){
        tcpAckAcc+=qty;
    }
    
    public static synchronized void addTcpFinAcc(int qty){
        tcpFinAcc+=qty;
    }
    
    public static synchronized void addTcpSynAcc(int qty){
        tcpSynAcc+=qty;
    }

    public static synchronized int getTcpAcc() {
        return tcpAcc;
    }

    public static synchronized int getUdpAcc() {
        return udpAcc;
    }

    public static synchronized int getIcmpAcc() {
        return icmpAcc;
    }

    public static synchronized int getTcpAckAcc() {
        return tcpAckAcc;
    }

    public static synchronized int getTcpFinAcc() {
        return tcpFinAcc;
    }

    public static synchronized int getTcpSynAcc() {
        return tcpSynAcc;
    }

    public static synchronized void zeroCounters() {
        tcpAcc = 0;
        udpAcc = 0;
        icmpAcc = 0;
        tcpAckAcc = 0;
        tcpFinAcc = 0;
        tcpSynAcc = 0;
    }
    
        
    public static void printValues(){
        System.out.println("Tcp "+tcpAcc+" Udp "+udpAcc+" Icmp "+icmpAcc+" TcpAck "+tcpAckAcc+" TcpFin "+tcpFinAcc+" TcpSyn "+tcpSynAcc);
    }
}
