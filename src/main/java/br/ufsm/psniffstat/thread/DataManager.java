package br.ufsm.psniffstat.thread;

import br.ufsm.psniffstat.buffer.DBDataBuffer;
import br.ufsm.psniffstat.XMLProperties;
import br.ufsm.psniffstat.database.DBArchive;
import br.ufsm.psniffstat.database.DBData;
import br.ufsm.psniffstat.database.DBFastAccess;
import br.ufsm.psniffstat.sniffer.SocketServer;

/**
 * DataManager is the thread in charge of counters storage<p>
 * Designed to read values from DBDataBuffer and store them into
 * configured database
 * @see DBDataBuffer
 * @author Tulkas
 */
public class DataManager extends Thread {
    
    private XMLProperties xmlProps;
    private DBDataBuffer dbds;
    //DAO representations
    private DBFastAccess dbFastAccess = null;
    private DBArchive dbArchive = null;
    
    //Socket representation
    private SocketServer socketServer = null;
    
    //Fast access timeout
    private boolean over30Min = false;
    
    public DataManager(XMLProperties xmlProps, DBDataBuffer dbds) {
        this.xmlProps = xmlProps;
        this.dbds = dbds;
        //Active fastAccess
        if (xmlProps.isFatOn()) {
            dbFastAccess = new DBFastAccess(xmlProps);
        }
        
        //Active historical access
        if (xmlProps.isArchiveOn()) {
            dbArchive = new DBArchive(xmlProps);
        }
        
        //Active socket access
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
