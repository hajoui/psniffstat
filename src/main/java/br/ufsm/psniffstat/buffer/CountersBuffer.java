package br.ufsm.psniffstat.buffer;

/**
 *
 * @author tuxtor
 */
public class CountersBuffer {

    private static int tcpAcc, udpAcc, icmpAcc, tcpAckAcc, tcpFinAcc, tcpSynAcc, totalAcc;

    public static synchronized void addAll(int tcpAcc, int udpAcc, int icmpAcc, int tcpAckAcc, int tcpFinAcc, int tcpSynAcc, int totalAcc) {
        CountersBuffer.tcpAcc += tcpAcc;
        CountersBuffer.udpAcc += udpAcc;
        CountersBuffer.icmpAcc += icmpAcc;
        CountersBuffer.tcpAckAcc += tcpAckAcc;
        CountersBuffer.tcpFinAcc += tcpFinAcc;
        CountersBuffer.tcpSynAcc += tcpSynAcc;
        CountersBuffer.totalAcc += totalAcc;
    }

    public static synchronized int[] getCounters(int numberOfActiveFilters) {
        int[] counters = new int[numberOfActiveFilters];
        int index = 0;
        if (tcpAcc != -1) {
            counters[index] = tcpAcc;
            index++;
        }
        if (udpAcc != -1) {
            counters[index] = udpAcc;
            index++;
        }
        if (icmpAcc != -1) {
            counters[index] = icmpAcc;
            index++;
        }
        if (tcpAckAcc != -1) {
            counters[index] = tcpAckAcc;
            index++;
        }
        if (tcpFinAcc != -1) {
            counters[index] = tcpFinAcc;
            index++;
        }
        if (tcpSynAcc != -1) {
            counters[index] = tcpSynAcc;
            index++;
        }
        return counters;
    }

    public static synchronized void addTcpAcc() {
        tcpAcc++;
    }

    public static synchronized void addUdpAcc() {
        udpAcc++;
    }

    public static synchronized void addIcmpAcc() {
        icmpAcc++;
    }

    public static synchronized void addTcpAckAcc() {
        tcpAckAcc++;
    }

    public static synchronized void addTcpFinAcc() {
        tcpFinAcc++;
    }

    public static synchronized void addTcpSynAcc() {
        tcpSynAcc++;
    }

    public static synchronized void addTcpAcc(int qty) {
        tcpAcc += qty;
    }

    public static synchronized void addUdpAcc(int qty) {
        udpAcc += qty;
    }

    public static synchronized void addIcmpAcc(int qty) {
        icmpAcc += qty;
    }

    public static synchronized void addTcpAckAcc(int qty) {
        tcpAckAcc += qty;
    }

    public static synchronized void addTcpFinAcc(int qty) {
        tcpFinAcc += qty;
    }

    public static synchronized void addTcpSynAcc(int qty) {
        tcpSynAcc += qty;
    }

    public static synchronized void addTotalAcc(int qty) {
        totalAcc += qty;
    }

    public static synchronized void zeroCounters() {
        tcpAcc = 0;
        udpAcc = 0;
        icmpAcc = 0;
        tcpAckAcc = 0;
        tcpFinAcc = 0;
        tcpSynAcc = 0;
        totalAcc = 0;
    }

    public static void printValues() {
        System.out.println("Tcp " + tcpAcc + " Udp " + udpAcc + " Icmp " + icmpAcc + " TcpAck " + tcpAckAcc + " TcpFin " + tcpFinAcc + " TcpSyn " + tcpSynAcc + " Total " + totalAcc);
    }
}
