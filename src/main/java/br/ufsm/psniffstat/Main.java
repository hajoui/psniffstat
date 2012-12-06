/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufsm.psniffstat;

/**
 *
 * @author Tulkas
 */
public class Main extends Thread {
    
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
