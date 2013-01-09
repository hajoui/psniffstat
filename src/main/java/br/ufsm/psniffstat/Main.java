package br.ufsm.psniffstat;

import br.ufsm.psniffstat.buffer.CountersBuffer;
import br.ufsm.psniffstat.buffer.PacketsBuffer;
import br.ufsm.psniffstat.thread.DataManager;
import br.ufsm.psniffstat.thread.JNetPcap;
import br.ufsm.psniffstat.thread.PacketAnalyser;
import br.ufsm.psniffstat.thread.PacketAnalyserDispatcher;
import java.util.Scanner;
import java.util.Timer;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tulkas
 */
public class Main {

    private static Scanner cmd;
    private static int status = 0;
    private static XMLProperties xmlProps;
    //Main threads objects
    private static DataManager dataManager;
    private static JNetPcap jNetPcap;
    private static PacketAnalyserDispatcher packetAnalyserDispatcher;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        xmlProps = new XMLProperties();
        //If ther is not args, jNetCap ask for a NIC
        if (args.length > 0) {
            jNetPcap = new JNetPcap(xmlProps, args[0]);
        } else {
            jNetPcap = new JNetPcap(xmlProps);
        }
        while (status != 2) {
            if (!jNetPcap.isAlive()) {
                runCapture();
            }
            cmd = new Scanner(System.in);
            System.out.println(">");
            status = cmd.nextInt();
            if (status == 1) {
                restartAll(args);
            } else if (status < 0 && status > 2) {
                System.out.println("Wrong command");
            }
        }
        if (jNetPcap.isAlive()) {
            shutdown();
            
        }
    }

    public static void runCapture() {
        //Run capture
        jNetPcap.openNetworkDevice();
        jNetPcap.start();
        //Run analysis
        packetAnalyserDispatcher = new PacketAnalyserDispatcher(5 * 1000);
        packetAnalyserDispatcher.start();
    }

    public static void shutdown() {
        try {
            jNetPcap.interrupt();
            packetAnalyserDispatcher.interrupt();
            jNetPcap.join();
            packetAnalyserDispatcher.join();
            System.out.println("Bye . . .");
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void restartAll(String[] args) {
        System.out.println("\nrestarting . . ." + PacketsBuffer.getPacketsList().size() + " packages captured");
        PacketsBuffer.clearPacketsList();
        try {
            jNetPcap.interrupt();
            packetAnalyserDispatcher.interrupt();
            jNetPcap.join();
            packetAnalyserDispatcher.join();
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
    }

//    public static void main(String[] args) {
//        XMLProperties xmlProps = new XMLProperties();
//        JNetPcap jNetPcap;
//        if (args.length > 0) {
//            jNetPcap = new JNetPcap(xmlProps, args[0]);
//        } else {
//            jNetPcap = new JNetPcap(xmlProps);
//        }
//        /*DBDataSync dbds = new DBDataSync(xmlProps);
//         SnifferCMD snifferCMDThread = new SnifferCMD(xmlProps, dbds);
//         DataManager dataManager = new DataManager(xmlProps, dbds);
//         snifferCMDThread.start();
//         dataManager.start();*/
//        //1 reiniciar, 2 computar, 3 salir
//        while (status != 3) {
//            if (!jNetPcap.isAlive()) {
//                jNetPcap.openNetworkDevice();
//                jNetPcap.start();
//                System.out.println("\ncapture thread started, waiting for commands");
//            }
//            cmd = new Scanner(System.in);
//            System.out.println(">");
//            status = cmd.nextInt();
//            if (status == 1) {
//                System.out.println("\nrestarting . . ." + PacketsBuffer.getPacketsList().size() + " packages captured");
//                PacketsBuffer.clearPacketsList();
//                try {
//                    jNetPcap.interrupt();
//                    jNetPcap.join();
//                } catch (Exception e) {
//                    System.exit(-1);
//                }
//                System.out.println("thread restarted");
//                if (args.length > 0) {
//                    jNetPcap = new JNetPcap(xmlProps, args[0]);
//                } else {
//                    jNetPcap = new JNetPcap(xmlProps);
//                }
//                status = 0;
//            } else if (status == 2) {
//                //Compute test
//                runAnalysis();
//                status = 0;
//            } else if (status != 3) {
//                System.out.println("wrong command");
//            }
//        }
//        if (jNetPcap.isAlive()) {
//            jNetPcap.interrupt();
//        }
//        System.out.println("\nprogram finished, " + PacketsBuffer.getPacketsList().size() + " packages captured");
//
//        //jNetPcap.runCapture();
//        //jNetPcap.dispatch();
//        //jNetPcap.close();
//
//    }
    public static void runAnalysis() {

        PacketsBuffer.prepareAnalisysVector();
        System.out.println("running analisis total size " + PacketsBuffer.getPacketsList().size() + " analysis size " + PacketsBuffer.getPacketsAnalysisList().size());
        PacketAnalyser analyser = new PacketAnalyser(0, PacketsBuffer.getPacketsList().size());
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(analyser);
        PacketsBuffer.removeAnalysVector();
        System.out.println("Analysis finished, total size " + PacketsBuffer.getPacketsList().size() + " analysis size " + PacketsBuffer.getPacketsAnalysisList().size());
        System.out.println("Compute size " + PacketsBuffer.getPacketsList().size());
        CountersBuffer.printValues();
        CountersBuffer.zeroCounters();
    }
}
