package br.ufsm.psniffstat.thread;

import br.ufsm.psniffstat.buffer.CountersBuffer;
import br.ufsm.psniffstat.buffer.PacketsBuffer;
import java.util.concurrent.ForkJoinPool;

/**
 * PacketAnalyserDispatcher excecutes periodically PacketAnalyser routines
 * @author tuxtor
 */
public class PacketAnalyserDispatcher {
    PacketAnalyser analyser;
    ForkJoinPool pool;
    
    public void run(){
        PacketsBuffer.prepareAnalisysVector();
        System.out.println("running analisis total size "+PacketsBuffer.getPacketsList().size()+" analysis size "+PacketsBuffer.getPacketsAnalysisList().size());
        analyser = new PacketAnalyser(0, PacketsBuffer.getPacketsAnalysisList().size());
        pool = new ForkJoinPool();
        pool.invoke(analyser);
        PacketsBuffer.removeAnalysVector();
        
        System.out.println("Analysis finished, total size "+PacketsBuffer.getPacketsList().size()+" analysis size "+PacketsBuffer.getPacketsAnalysisList().size());
        System.out.println("Compute size " + PacketsBuffer.getPacketsList().size());
        CountersBuffer.printValues();
        CountersBuffer.zeroCounters();
    }

    public void interrupt(){
        pool.shutdownNow();
    }
    
    private void runAnalysis(){
        
    }
}
