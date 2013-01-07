package br.ufsm.psniffstat;

import br.ufsm.psniffstat.database.DBDataSync;
import br.ufsm.psniffstat.database.DataManager;
import br.ufsm.psniffstat.sniffer.SnifferCMD;

/**
 *
 * @author Tulkas
 */
public class Main{
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        XMLProperties xmlProps = new XMLProperties();
        DBDataSync dbds = new DBDataSync(xmlProps);
        SnifferCMD snifferCMDThread = new SnifferCMD(xmlProps, dbds);
        DataManager dataManager = new DataManager(xmlProps, dbds);
        snifferCMDThread.start();
        dataManager.start();
    }

}
