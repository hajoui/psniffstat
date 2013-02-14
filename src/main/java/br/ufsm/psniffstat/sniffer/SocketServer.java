package br.ufsm.psniffstat.sniffer;

import br.ufsm.psniffstat.database.DBData;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tulkas
 */
public class SocketServer {
    
    ServerSocket server = null;
    Socket socket = null;
    DataOutputStream out = null;
    private boolean isConnected = false;
    
    public SocketServer(int port) {
        try {
            server = new ServerSocket(port);
        } catch (IOException ex) {
            Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        waitForConnection();
    }
    
    private void waitForConnection() {
        System.out.println("Esperando a conexão de um cliente por sockets...");
        try {
            socket = server.accept();
            isConnected = true;
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Cliente conectado.");
    }
    
    public void closeSocket() {
        try {
            out.close();
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void closeServer() {
        try {
            server.close();
        } catch (IOException ex) {
            Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean isConnected() {
        return isConnected;
    }
    
    public boolean sendData(int flag) {
        if (isConnected) {
            try {
                out.writeInt(flag);
            } catch (IOException ex) {
                Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }
    
    public boolean sendData(DBData dbd) {
        if (isConnected) {
            try {
                out.writeUTF(dbd.getTimestamp().toString());
                int[] counters = dbd.getCounters();
                for (int i = 0; i < counters.length; i++) {
                    out.writeInt(counters[i]);
                }
            } catch (IOException ex) {
                Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            return true;
        }
        return false;
        
    }
}
