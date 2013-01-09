package br.ufsm.psniffstat.sniffer;

import br.ufsm.psniffstat.XMLProperties;
import br.ufsm.psniffstat.buffer.PacketsBuffer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.JPacketHandler;
import org.jnetpcap.protocol.network.Icmp;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;

/**
 * JNetPcap is the wrapper class which allows sniffer threads to execute libpcap
 * actions based on configured filters
 *
 * @author Tulkas
 */
public class JNetPcap extends Thread {

    private int totalPackages;
    private int deviceID;
    private PcapIf device;
    private Pcap pcap;
    private JPacketHandler<String> handler; //Packet administrator
    private StringBuilder errbuf = new StringBuilder();

    private XMLProperties xmlProps;

    public JNetPcap(XMLProperties xmlProps) {
        //tcpAcc = udpAcc = icmpAcc = tcpAckAcc = tcpFinAcc = tcpSynAcc = -1;
        this.xmlProps = xmlProps;
        //sets configured counters at 0
        //zeroCountersInternal();
        //asks to the user which NIC should be used
        chooseNetworkDevice();
    }

    public JNetPcap(XMLProperties xmlProps, String nicId) {
        //tcpAcc = udpAcc = icmpAcc = tcpAckAcc = tcpFinAcc = tcpSynAcc = -1;
        this.xmlProps = xmlProps;
        //sets configured counters at 0
        //zeroCountersInternal();
        //asks to the user which NIC should be used
        setNetworkDevice(nicId);
    }

    /**
     * Search the available devices and if it has success asks to the user wich
     * NIC should be sniffed
     */
    private void chooseNetworkDevice() {
        List<PcapIf> alldevs = new ArrayList<>(); // Will be filled with NICs
        int r = Pcap.findAllDevs(alldevs, errbuf);
        if (r == Pcap.NOT_OK || alldevs.isEmpty()) {
            System.err.println("Não foi possível ler uma lista de dispositivos. "
                    + "O erro obtido é: " + errbuf.toString());
            System.exit(-1);
        }

        System.out.println("Lista de dispositivos encontrados:");
        int i = 0;
        for (PcapIf tempDevice : alldevs) {
            System.out.printf("#%d: %s [%s]\n", i++, tempDevice.getName(), tempDevice
                    .getDescription());
        }
        System.out.print("Qual desejas escolher? ");
        Scanner sc = new Scanner(System.in);
        deviceID = sc.nextInt();
        if (deviceID < 0 || deviceID >= alldevs.size()) {
            System.out.println("Dispositivo de rede escolhido inválido.");
            System.exit(-1);
        }
        device = alldevs.get(deviceID);
        System.out.printf("\nEscolhido o dispositivo: '%s'.\n", device.getDescription());
    }

    private void setNetworkDevice(String nicId) {
        List<PcapIf> alldevs = new ArrayList<>(); // Will be filled with NICs
        int r = Pcap.findAllDevs(alldevs, errbuf);
        if (r == Pcap.NOT_OK || alldevs.isEmpty()) {
            System.err.println("Não foi possível ler uma lista de dispositivos. "
                    + "O erro obtido é: " + errbuf.toString());
            System.exit(-1);
        }
        for (PcapIf tempDevice : alldevs) {
            if (tempDevice.getName().equalsIgnoreCase(nicId)) {
                device = tempDevice;
                break;
            }
        }
        if (device != null) {
            System.out.printf("\nEscolhido o dispositivo: '%s-%s'.\n", device.getName(), device.getDescription());
        } else {
            System.out.println("Dispositivo de rede escolhido inválido.");
            System.exit(-1);
        }
    }

    /**
     * Opens network device to start package capture
     */
    public void openNetworkDevice() {
        int snaplen = 64 * 1024;           // Captura todos pacotes, sem truncar
        int flags = Pcap.MODE_PROMISCUOUS; // Captura todos pacotes
        int timeout = xmlProps.getInterval() * 1000; // x segundos em milisegundos
        pcap = Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);
        if (pcap == null) {
            System.err.printf("Erro durante tentativa de abrir dispositivo para captura: "
                    + errbuf.toString());
            System.exit(-1);
        }

        PcapBpfProgram filter = new PcapBpfProgram();
        String expression = buildExpression();
        if (pcap.compile(filter, expression, 0, 0) != Pcap.OK) {
            System.out.println("Erro no filtro: " + pcap.getErr());
            System.exit(-1);
        }
        if (pcap.setFilter(filter) != Pcap.OK) {
            System.err.println(pcap.getErr());
            System.exit(-1);
        }
        handler = new JPacketHandler<String>() {
            /**
             * Increments the packets counters based on its headers
             */
            @Override
            public void nextPacket(JPacket packet, String user) {
                PacketsBuffer.addPacket(packet);
                /*if (packet.hasHeader(tcpHeaderModel)) {
                 Tcp tcpHeader = packet.getHeader(tcpHeaderModel);
                    
                 if(packet.hasHeader(htt)){
                 byte[] t = tcpHeaderModel.getPayload();
                 String teste = new String(t);
                 if(teste!=null){
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
                 }
                 if (packet.hasHeader(udpHeaderModel)) {
                 udpAcc++;
                 }
                 if (packet.hasHeader(icmpHeaderModel)) {
                 icmpAcc++;
                 }*/
            }
        };
    }

    public void runCapture() {
        pcap.loop(-1, handler, "PSniffStat");
    }

    @Override
    public void run() {
        pcap.loop(-1, handler, "PSniffStat");
    }

    @Override
    public void interrupt() {
        pcap.breakloop();
        this.close();
        super.interrupt();
    }

    /**
     * Releases the NIC and captures
     */
    public void dispatch() {
        Date today = new Date();
        Timestamp tsp = new Timestamp(today.getTime());
        System.out.println("disp before: " + tsp.toString());
        pcap.dispatch(-1, handler, "user");
        Date today2 = new Date();
        Timestamp tsp2 = new Timestamp(today2.getTime());
        System.out.println("disp after: " + tsp2.toString());
    }

    /**
     * Closes the NIC
     */
    public void close() {
        pcap.close();
    }

    /*
     * Builds jnetpcap filter expression based on active filters at FiltersStatus
     * @see FilterStatus
     */
    private String buildExpression() {
        String filter = "";
        ArrayList<String> separatedFilters = new ArrayList<>();
        FiltersStatus fs = xmlProps.getFilters();
        if (fs.isTCPActivated()) {
            separatedFilters.add("tcp");
        }
        if (fs.isUDPActivated()) {
            separatedFilters.add("udp");
        }
        if (fs.isICMPActivated()) {
            separatedFilters.add("icmp");
        }
        if (fs.isTCPACKActivated()) {
            separatedFilters.add("(tcp[tcpflags] & (tcp-ack) != 0)");
        }
        if (fs.isTCPFINActivated()) {
            separatedFilters.add("(tcp[tcpflags] & (tcp-fin) != 0)");
        }
        if (fs.isTCPSYNActivated()) {
            separatedFilters.add("(tcp[tcpflags] & (tcp-syn) != 0)");
        }
        for (int i = 0; i < separatedFilters.size(); i++) {
            filter += separatedFilters.get(i);
            if ((i + 1) != separatedFilters.size()) {
                filter += " or ";
            }
        }
        if (xmlProps.getPortToFilter() != -1) {
            filter = "port " + xmlProps.getPortToFilter() + " and (" + filter + ")";
        }
        System.out.println("exp: " + filter);
        return filter;
    }

    /*
     * Sets internal counters at 0
     */
    /*private void zeroCountersInternal() {
        FiltersStatus fs = xmlProps.getFilters();
        if (fs.isTCPActivated()) {
            tcpAcc = 0;
        }
        if (fs.isUDPActivated()) {
            udpAcc = 0;
        }
        if (fs.isICMPActivated()) {
            icmpAcc = 0;
        }
        if (fs.isTCPACKActivated()) {
            tcpAckAcc = 0;
        }
        if (fs.isTCPFINActivated()) {
            tcpFinAcc = 0;
        }
        if (fs.isTCPSYNActivated()) {
            tcpSynAcc = 0;
        }
    }*/

    /**
     * Sets packages counters at 0
     */
    /*public void zeroCounters() {
        if (tcpAcc != -1) {
            tcpAcc = 0;
        }
        if (udpAcc != -1) {
            udpAcc = 0;
        }
        if (icmpAcc != -1) {
            icmpAcc = 0;
        }
        if (tcpAckAcc != -1) {
            tcpAckAcc = 0;
        }
        if (tcpFinAcc != -1) {
            tcpFinAcc = 0;
        }
        if (tcpSynAcc != -1) {
            tcpSynAcc = 0;
        }
    }*/

    /**
     * Returns array values based on filters status
     *
     * @return array that represents counters values
     */
    /*public int[] getCounters() {
        int[] counters = new int[xmlProps.getFilters().getNumberOfActivatedFilters()];
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
    }*/
}
