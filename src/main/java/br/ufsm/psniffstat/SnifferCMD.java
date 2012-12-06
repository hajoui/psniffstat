/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufsm.psniffstat;

import java.sql.Timestamp;
import java.util.Date;

/**
 *
 * @author Adler
 */
public class SnifferCMD extends Thread {
    
    private XMLProperties xmlProps;
    private DBDataSync dbDataSync;
    private JNetPcap jNetPcap;
    
    public SnifferCMD(XMLProperties xmlProps, DBDataSync dbDataSync) {
        this.xmlProps = xmlProps;
        this.dbDataSync = dbDataSync;
        jNetPcap = new JNetPcap(xmlProps);
    }
    
    @Override
    public void run() {
        System.out.println("SnifferCMD: Started!");
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
            dbDataSync.addItem(dbData);
            jNetPcap.zeroCounters();
            if (xmlProps.getAmmountIntervals() != -1) {
                i++;
            }
            // System.out.println("SnifferCMD: Ciclo de captura concluído.");
            // System.out.println("tspsniffcmd: " + tsp.toString());
        } while (i < xmlProps.getAmmountIntervals());
        System.out.println("SnifferCMD: Finished...");
    }
}

