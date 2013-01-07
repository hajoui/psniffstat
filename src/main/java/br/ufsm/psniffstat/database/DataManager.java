package br.ufsm.psniffstat.database;

import br.ufsm.psniffstat.XMLProperties;
import br.ufsm.psniffstat.sniffer.SocketServer;

/**
 * DataManager is designed to read values from DBDataSync and store them into
 * the configured database
 * @author Tulkas
 */
public class DataManager extends Thread {
    
    private XMLProperties xmlProps;
    private DBDataSync dbds;
    private DBFastAccess dbFastAccess = null;
    private DBArchive dbArchive = null;
    private SocketServer socketServer = null;
    private boolean over30Min = false;
    
    public DataManager(XMLProperties xmlProps, DBDataSync dbds) {
        this.xmlProps = xmlProps;
        this.dbds = dbds;
        //Sets fastAccess
        if (xmlProps.isFatOn()) {
            dbFastAccess = new DBFastAccess(xmlProps);
        }
        
        //Initialize historical access
        if (xmlProps.isArchiveOn()) {
            dbArchive = new DBArchive(xmlProps);
        }
        
        //Initialize socket access
        if (xmlProps.isSocketOn()) {
            socketServer = new SocketServer(xmlProps.getSocketPort());
        }
    }
    
    public boolean isSocketConnected() {
        if (socketServer == null) {
            return false;
        }
        return socketServer.isConnected();
    }
    
    @Override
    public void run() {
        System.out.println("DataManager: Started!");
        int i = 0;
        if (xmlProps.getAmmountIntervals() == -1) {
            i = -2;
        }
        do {
            if (xmlProps.getAmmountIntervals() != -1) {
                i++;
            }
            DBData dbd = dbds.removeItem();
            if (dbFastAccess != null) {
                dbFastAccess.insertItem(dbd);
                if (over30Min) {
                    dbFastAccess.removeOldRows();
                }
                // 1.800 = Meia hora em segundos, ou seja, já passou meia hora
                if ((i * xmlProps.getInterval()) > 1800) {
                    // Inicia a remover as linhas desnecessárias da FAT
                    over30Min = true;
                }
            }
            if (dbArchive != null) {
                dbArchive.insertItem(dbd);
            }
            if (socketServer != null) {
                socketServer.sendData(dbd);
                // Sinaliza que uma estrutura terminou de ser transferida e haverá outras
                if (i < xmlProps.getAmmountIntervals()) {
                    socketServer.sendData(-1);
                }
                // Sinaliza que a estrutura enviada foi a última
                else {
                    socketServer.sendData(-2);
                }
            }
        } while (i < xmlProps.getAmmountIntervals());
        System.out.println("gotout");
        if (socketServer != null) {
            socketServer.closeSocket();
            socketServer.closeServer();
        }
        System.exit(-1);
    }

}
