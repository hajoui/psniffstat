package br.ufsm.psniffstat;

import br.ufsm.psniffstat.buffer.CountersBuffer;
import br.ufsm.psniffstat.buffer.PacketsBuffer;
import br.ufsm.psniffstat.sniffer.JNetPcap;
import br.ufsm.psniffstat.sniffer.PacketAnalyser;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;

/**
 *
 * @author Tulkas
 */
public class Main {

    static Scanner cmd;
    static int status = 0;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        XMLProperties xmlProps = new XMLProperties();
        JNetPcap jNetPcap;
        if (args.length > 0) {
            jNetPcap = new JNetPcap(xmlProps, args[0]);
        } else {
            jNetPcap = new JNetPcap(xmlProps);
        }
        /*DBDataSync dbds = new DBDataSync(xmlProps);
         SnifferCMD snifferCMDThread = new SnifferCMD(xmlProps, dbds);
         DataManager dataManager = new DataManager(xmlProps, dbds);
         snifferCMDThread.start();
         dataManager.start();*/
        //1 reiniciar, 2 computar, 3 salir
        while (status != 3) {
            if (!jNetPcap.isAlive()) {
                jNetPcap.openNetworkDevice();
                jNetPcap.start();
                System.out.println("\ncapture thread started, waiting for commands");
            }
            cmd = new Scanner(System.in);
            System.out.println(">");
            status = cmd.nextInt();
            if (status == 1) {
                System.out.println("\nrestarting . . ." + PacketsBuffer.getPacketsList().size() + " packages captured");
                PacketsBuffer.clearPacketsList();
                try {
                    jNetPcap.interrupt();
                    jNetPcap.join();
                } catch (Exception e) {
                    System.exit(-1);
                }
                System.out.println("thread restarted");
                if (args.length > 0) {
                    jNetPcap = new JNetPcap(xmlProps, args[0]);
                } else {
                    jNetPcap = new JNetPcap(xmlProps);
                }
                status = 0;
            } else if (status == 2) {
                //Compute test
                runAnalysis();
                status = 0;
            } else if (status != 3) {
                System.out.println("wrong command");
            }
        }
        if (jNetPcap.isAlive()) {
            jNetPcap.interrupt();
        }
        System.out.println("\nprogram finished, " + PacketsBuffer.getPacketsList().size() + " packages captured");

        //jNetPcap.runCapture();
        //jNetPcap.dispatch();
        //jNetPcap.close();

    }

    public static void runAnalysis() {
        
        PacketsBuffer.prepareAnalisysVector();
        System.out.println("running analisis total size "+PacketsBuffer.getPacketsList().size()+" analysis size "+PacketsBuffer.getPacketsAnalysisList().size());
        PacketAnalyser analyser = new PacketAnalyser(0, PacketsBuffer.getPacketsList().size());
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(analyser);
        PacketsBuffer.removeAnalysVector();
        System.out.println("Analysis finished, total size "+PacketsBuffer.getPacketsList().size()+" analysis size "+PacketsBuffer.getPacketsAnalysisList().size());
        System.out.println("Compute size " + PacketsBuffer.getPacketsList().size());
        CountersBuffer.printValues();
        CountersBuffer.zeroCounters();

    }
}
