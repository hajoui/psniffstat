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

    public static void startPacketsBuffer() {
        packetsList = Collections.synchronizedList(new ArrayList<>());
    }

    public static List getPacketsList() {
        return packetsList;
    }

    public static List getAnalysisList() {
        int finalIndex=packetsList.size();
        List listShallowCopy = new ArrayList(packetsList.subList(0, finalIndex));
        return listShallowCopy;
    }

    //TODO ver si existe una mejor forma para clonar
    public static List getPacketsAnalisysSublist(int start, int end) {
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

    public static void addPacket(PcapPacket packet) {
        getPacketsList().add(packet);
    }

    public static void removeAnalysisList(List analysisList) {
        packetsList.removeAll(analysisList);
    }
}
