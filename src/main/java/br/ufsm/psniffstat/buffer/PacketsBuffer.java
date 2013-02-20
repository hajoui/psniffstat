package br.ufsm.psniffstat.buffer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jnetpcap.packet.PcapPacket;

/**
 *
 * @author tuxtor
 */
public class PacketsBuffer {

    private static List packetsList;

    public static synchronized void startPacketsBuffer() {
        packetsList = Collections.synchronizedList(new ArrayList<>());
    }

    public static synchronized List getPacketsList() {
        return packetsList;
    }

    public static synchronized List getAnalysisList() {
        int finalIndex=packetsList.size();
        List listShallowCopy = new ArrayList(packetsList.subList(0, finalIndex));
        return listShallowCopy;
    }
    
    public static synchronized List getAnalysisListReference(int startIndex, int finalIndex) {
        return packetsList.subList(0, finalIndex);
    }

    //TODO ver si existe una mejor forma para clonar
    public static synchronized List getPacketsAnalisysSublist(int start, int end) {
        List packetList;
        if (end > getPacketsList().size()) {
            packetList = new ArrayList(packetsList.subList(start, getPacketsList().size()));
            //getPacketsList().removeAll(packetList);
        } else {
            packetList = new ArrayList(packetsList.subList(start, end));
            //getPacketsList().removeAll(packetList);
        }
        return packetList;
    }

    public static synchronized void addPacket(PcapPacket packet) {
        getPacketsList().add(packet);
    }

    public static synchronized void removeAnalysisList(List analysisList) {
        packetsList.removeAll(analysisList);
    }
}
