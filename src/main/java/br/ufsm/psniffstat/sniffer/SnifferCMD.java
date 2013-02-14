package br.ufsm.psniffstat.sniffer;

import br.ufsm.psniffstat.XMLProperties;
import br.ufsm.psniffstat.buffer.DBDataBuffer;
import br.ufsm.psniffstat.thread.JNetPcap;

/**
 * This class represents sniffing thread and uses DBDataSync to store in database
 * @author Adler
 */
public class SnifferCMD extends Thread {
    
    private XMLProperties xmlProps;
    private DBDataBuffer dbDataSync;
    private JNetPcap jNetPcap;
    
    public SnifferCMD(XMLProperties xmlProps, DBDataBuffer dbDataSync) {
        this.xmlProps = xmlProps;
        this.dbDataSync = dbDataSync;
        jNetPcap = new JNetPcap(xmlProps);
    }
    
    @Override
    public void run() {
        /*System.out.println("SnifferCMD: Started!");
        DBData dbData;
        int i = 0;
        if (xmlProps.getAmmountIntervals() == -1) {
            i = -2;
        }
        jNetPcap.openNetworkDevice();
        do {
            // System.out.println("SnifferCMD: Iniciando um ciclo de captura.");
            Date today = new Date();
            Timestamp tsp = new Timestamp(today.getTime());
            
            // Ordena o início da captura dos pacotes
            jNetPcap.dispatch();
            
            // Cria uma estrutura de dados pré-inserção no DB
            dbData = new DBData(xmlProps.getFilters().getNumberOfActivatedFilters());
            dbData.setTimestamp(tsp);
            dbData.setCounters(jNetPcap.getCounters());
            dbDataSync.addItem(dbData);//Storage action
            jNetPcap.zeroCounters();
            if (xmlProps.getAmmountIntervals() != -1) {
                i++;
            }
            // System.out.println("SnifferCMD: Ciclo de captura concluído.");
            // System.out.println("tspsniffcmd: " + tsp.toString());
        } while (i < xmlProps.getAmmountIntervals());
        System.out.println("SnifferCMD: Finished...");*/
    }
}

