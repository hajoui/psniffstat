package br.ufsm.psniffstat.buffer;

import br.ufsm.psniffstat.XMLProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.jnetpcap.packet.JPacket;

/**
 *
 * @author tuxtor
 */
public class PacketsBuffer {

    private static List packetsList;
    private static List packetsAnalysisList;
    
    public synchronized static List getPacketsList() {
        if (packetsList == null) {
            packetsList = new Vector<>();
        }
        return packetsList;
    }
    
    public synchronized static List getPacketsAnalysisList() {
        if (packetsAnalysisList == null) {
            packetsAnalysisList = new Vector<>();
        }
        return packetsAnalysisList;
    }
    
    public static void prepareAnalisysVector(){
        getPacketsAnalysisList().clear();
        packetsAnalysisList = new Vector(getPacketsList());
    }
    
    public static void removeAnalysVector(){
        getPacketsList().removeAll(getPacketsAnalysisList());
        getPacketsAnalysisList().clear();
    }

    //TODO ver si existe una mejor forma para clonar
    public synchronized static List getPacketsAnalisysSublist(int start, int end) {
        List packetList;
        if (end > getPacketsList().size()) {
            packetList = new ArrayList(getPacketsAnalysisList().subList(start, getPacketsList().size()));
            //getPacketsList().removeAll(packetList);
        } else {
            packetList = new ArrayList(getPacketsAnalysisList().subList(start, end));
            //getPacketsList().removeAll(packetList);
        }
        return packetList;
    }

    public synchronized static void clearPacketsList() {
        getPacketsList().clear();
        getPacketsAnalysisList().clear();
    }

    public synchronized static void addPacket(JPacket packet) {
        getPacketsList().add(packet);
    }

    public synchronized static void addPacketList(List packets) {
        getPacketsList().addAll(packets);
    }

    /**
     * Returns the actual packets collection cloning the vector and setting it
     * as empty
     */
    public synchronized static List getAndRemovePackets() {
        List newPacketList = new ArrayList(getPacketsList());
        getPacketsList().clear();
        return newPacketList;
    }
}
