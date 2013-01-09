package br.ufsm.psniffstat.thread;

import br.ufsm.psniffstat.buffer.CountersBuffer;
import br.ufsm.psniffstat.buffer.PacketsBuffer;
import java.util.Date;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    public PacketAnalyserDispatcher(int interval) {
        this.interval = interval;
        startTime = new Date();
        endTime = new Date();
    }

    @Override
    public void run() {
        while (true) {
            try {
                runAnalysis();
                long difference = interval - (endTime.getTime() - startTime.getTime());
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
        PacketsBuffer.prepareAnalisysVector();
        //System.out.println("--------------");
        //System.out.println("Analysis started. Total size " + PacketsBuffer.getPacketsList().size() + " analysis size " + PacketsBuffer.getPacketsAnalysisList().size());
        analyser = new PacketAnalyser(0, PacketsBuffer.getPacketsAnalysisList().size());
        pool = new ForkJoinPool();
        pool.invoke(analyser);
        PacketsBuffer.removeAnalysVector();

        //System.out.println("Analysis finished. Total size " + PacketsBuffer.getPacketsList().size() + " Analysis size " + PacketsBuffer.getPacketsAnalysisList().size());
        //System.out.println("Compute size " + PacketsBuffer.getPacketsList().size() + " Total loops " + totalExecutedAnalysis);
        //System.out.println("--------------");
        CountersBuffer.printValues();
        CountersBuffer.zeroCounters();
        endTime = new Date();
    }
}
