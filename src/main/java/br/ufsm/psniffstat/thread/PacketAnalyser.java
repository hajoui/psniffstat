package br.ufsm.psniffstat.thread;

import br.ufsm.psniffstat.buffer.CountersBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.network.Icmp;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;

/**
 * PacketAnalyser process JNetPcap packages between n threads and stores the
 * analysis results in a temporal buffer before database writing
 *
 * @see JNetPcap
 * @author tuxtor
 */
public class PacketAnalyser extends RecursiveAction {

    private Tcp tcpHeaderModel = new Tcp();
    private Udp udpHeaderModel = new Udp();
    private Icmp icmpHeaderModel = new Icmp();
    //Paralell variables
    private int start;
    private int lenght;
    private List packetList;
    Http htt = new Http();
    
    public PacketAnalyser(List packetList, int start, int lenght) {
        this.start = start;
        this.lenght = lenght;
        this.packetList = packetList;
    }

    @Override
    protected void compute() {
        int mainThreads = 2;
        int threshold = 1000;
        if (lenght <= threshold) {
            computeDirectly();
            return;
        }
        int split = lenght / mainThreads;

        List packetAnalyzers = new ArrayList();
        for (int i = 0; i < mainThreads; i++) {
            packetAnalyzers.add(new PacketAnalyser(packetList, start+i * split, split));
        }
        invokeAll(packetAnalyzers);
        /*invokeAll(new PacketAnalyser(mStart, split, mDestination),
         new ForkBlur(mSource, mStart + split, mLength - split, mDestination));*/
    }

    protected void computeDirectly() {
        int tcpAcc=0, udpAcc=0, icmpAcc=0, tcpAckAcc=0, tcpFinAcc=0, tcpSynAcc=0, totalAcc=0;
        //System.out.println("computing from "+start+" to "+(lenght+start));
        //TODO Revisar indices
        List<PcapPacket> packetsList = packetList.subList(start, lenght+start);
        for (PcapPacket packet : packetsList) {
            totalAcc++;
            if (packet.hasHeader(tcpHeaderModel)) {
                Tcp tcpHeader = packet.getHeader(tcpHeaderModel);
                tcpAcc++;
                if (tcpHeader.flags_ACK()) {
                    tcpAckAcc++;
                }
                if (tcpHeader.flags_FIN()) {
                    tcpFinAcc++;
                }
                if (tcpHeader.flags_SYN()) {
                    tcpSynAcc++;
                }
            }else if (packet.hasHeader(udpHeaderModel)) {
                udpAcc++;
            }else if(packet.hasHeader(icmpHeaderModel)) {
                icmpAcc++;
            }
        }
        //Writing to packet buffer
        CountersBuffer.addAll(tcpAcc, udpAcc, icmpAcc, tcpAckAcc, tcpFinAcc, tcpSynAcc, totalAcc);
        //PacketsBuffer.removePacketsList(packetsList);
        //System.out.println("computing from "+start+" to "+(lenght+start)+" finished");
    }
}
