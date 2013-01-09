package br.ufsm.psniffstat.thread;

import br.ufsm.psniffstat.buffer.CountersBuffer;
import br.ufsm.psniffstat.buffer.PacketsBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;
import org.jnetpcap.packet.JPacket;
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
    Http htt = new Http();
    
    public PacketAnalyser(int start, int lenght) {
        this.start = start;
        this.lenght = lenght;
    }

    @Override
    protected void compute() {
        int mainThreads = 7;
        int threshold = 1000000000;
        if (lenght <= threshold) {
            computeDirectly();
            return;
        }
        int split = lenght / mainThreads;

        List packetAnalyzers = new ArrayList();
        for (int i = 0; i < mainThreads; i++) {
            packetAnalyzers.add(new PacketAnalyser(start+i * split, split));
        }
        invokeAll(packetAnalyzers);
        /*invokeAll(new PacketAnalyser(mStart, split, mDestination),
         new ForkBlur(mSource, mStart + split, mLength - split, mDestination));*/
    }

    protected void computeDirectly() {
        int tcpAcc=0, udpAcc=0, icmpAcc=0, tcpAckAcc=0, tcpFinAcc=0, tcpSynAcc=0, totalAcc=0;
        //System.out.println("computing from "+start+" to "+(lenght+start));
        //TODO Revisar indices
        List<JPacket> packetsList = PacketsBuffer.getPacketsAnalisysSublist(start, lenght+start);
        for (JPacket packet : packetsList) {
            if (packet.hasHeader(tcpHeaderModel)) {
                Tcp tcpHeader = packet.getHeader(tcpHeaderModel);

                if (packet.hasHeader(htt)) {
                    byte[] t = tcpHeaderModel.getPayload();
                    String teste = new String(t);
                    if (teste != null) {
                        System.out.println(teste);
                    }
                }

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
        CountersBuffer.addTcpAcc(tcpAcc);
        CountersBuffer.addUdpAcc(udpAcc);
        CountersBuffer.addIcmpAcc(icmpAcc);
        CountersBuffer.addTcpAckAcc(tcpAckAcc);
        CountersBuffer.addTcpFinAcc(tcpFinAcc);
        CountersBuffer.addTcpSynAcc(tcpSynAcc);
        CountersBuffer.addTotalAcc(packetsList.size());
        //System.out.println("computing from "+start+" to "+(lenght+start)+" finished");
    }
}
