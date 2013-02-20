package br.ufsm.psniffstat.thread;

import br.ufsm.psniffstat.XMLProperties;
import br.ufsm.psniffstat.buffer.CountersBuffer;
import br.ufsm.psniffstat.buffer.DBDataBuffer;
import br.ufsm.psniffstat.buffer.PacketsBuffer;
import br.ufsm.psniffstat.database.DBData;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

/**
 * PacketAnalyserDispatcher excecutes periodically PacketAnalyser routines
 *
 * @author tuxtor
 */
public class PacketAnalyserDispatcher extends Thread {

    PacketAnalyser analyser;
    ForkJoinPool pool;
    int interval;//miliseconds
    int totalExecutedAnalysis;
    Date startTime;
    Date endTime;
    XMLProperties xmlProps;
    DBDataBuffer dataBuffer;

    public PacketAnalyserDispatcher(XMLProperties xmlProps) {
        this.interval = xmlProps.getInterval() * 1000;
        startTime = new Date();
        endTime = new Date();
        this.xmlProps = xmlProps;
    }

    public PacketAnalyserDispatcher(XMLProperties xmlProps, DBDataBuffer dataBuffer) {
        this.interval = xmlProps.getInterval() * 1000;
        startTime = new Date();
        endTime = new Date();
        this.xmlProps = xmlProps;
        this.dataBuffer = dataBuffer;
    }

    @Override
    public void run() {
        while (true) {
            try {
                runAnalysis();
                //long difference = interval - (endTime.getTime() - startTime.getTime());
                long difference = 2000;
                if (difference > 500) {
                    Thread.sleep(difference);
                }

            } catch (InterruptedException ex) {
                //Logger.getLogger(PacketAnalyserDispatcher.class.getName()).log(Level.SEVERE, null, ex);
                break;
            }
            totalExecutedAnalysis++;
        }
    }

    private void runAnalysis() {
        startTime = new Date();
        //PacketsBuffer.prepareAnalisysVector();
        //System.out.println("--------------");
        //System.out.println("Analysis started. Total size " + PacketsBuffer.getPacketsList().size() + " analysis size " + PacketsBuffer.getPacketsAnalysisList().size());
        List analysisList = PacketsBuffer.getAnalysisList();
        analyser = new PacketAnalyser(analysisList, 0, analysisList.size());
        pool = new ForkJoinPool();
        pool.invoke(analyser);

        PacketsBuffer.removeAnalysisList(analysisList);
        //System.out.println("Analysis finished. Total size " + PacketsBuffer.getPacketsList().size() + " Analysis size " + PacketsBuffer.getPacketsAnalysisList().size());
        //System.out.println("Compute size " + PacketsBuffer.getPacketsList().size() + " Total loops " + totalExecutedAnalysis);
        //System.out.println("--------------");
        CountersBuffer.printValues();
        //persist data
        endTime = new Date();
        if (dataBuffer != null) {
            //System.out.println("Sending to database");
            DBData data = new DBData(xmlProps.getFilters().getNumberOfActivatedFilters());
            data.setCounters(CountersBuffer.getCounters(xmlProps.getFilters().getNumberOfActivatedFilters()));
            data.setTimestamp(new Timestamp(endTime.getTime()));
            dataBuffer.addItem(data);
        }
        CountersBuffer.zeroCounters();

    }
}
