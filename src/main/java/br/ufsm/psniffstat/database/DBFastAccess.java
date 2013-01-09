/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufsm.psniffstat.database;


import br.ufsm.psniffstat.sniffer.FiltersStatus;
import br.ufsm.psniffstat.XMLProperties;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tulkas
 */
public class DBFastAccess {

    private XMLProperties xmlProps;
    private final int THIRTY_MINUTES = 30 * 60 * 1000; // Em milisecs
    
    public DBFastAccess(XMLProperties xmlProps) {
        this.xmlProps = xmlProps;
        Connection conn = null;
        try {
            //System.out.println(xmlProps.getDBClassName());
            //System.out.println(xmlProps.getDBName());
            Class.forName(xmlProps.getDBClassName());
            conn = DriverManager.getConnection(
            "jdbc:" + xmlProps.getDBName() + "://" +
                   xmlProps.getDbHostName() + ":" + xmlProps.getDbPort() +
                   "/" + xmlProps.getDbLocation(), xmlProps.getDbUsername(),
                   xmlProps.getDbPassword());
            if (shouldICreateFAT(conn)) {
                buildFastAccessTable(conn);
            }
            else {
                cleanFastAccessTable(conn);
            }
        } catch (ClassNotFoundException e) {
            System.out.println("ClassNotFound...");  
            e.printStackTrace();  
        } catch (SQLException e) {
            System.out.println("sql exception");
            System.out.println(e);
        } finally {  
            try {
                conn.close();  
            } catch (SQLException onConClose) {  
                System.out.println("Error on closing");  
                onConClose.printStackTrace();  
            }
        }
    }
    
    private void cleanFastAccessTable(Connection conn) {
        try {
            Date today = new Date();
            Timestamp tsp = new Timestamp(today.getTime());
            String query = "DELETE FROM " + xmlProps.getDbFatName() + " WHERE TSMP > ?;";
            PreparedStatement forwardRows = conn.prepareStatement(query);
            forwardRows.setTimestamp(1, tsp);
            forwardRows.execute();
            forwardRows.close();
            
            query = "DELETE FROM " + xmlProps.getDbFatName() + " WHERE TSMP < ?;";
            tsp.setTime(tsp.getTime() - THIRTY_MINUTES);
            PreparedStatement backwardRows = conn.prepareStatement(query);
            backwardRows.setTimestamp(1, tsp);
            backwardRows.execute();
            backwardRows.close();
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }
    
    private void buildFastAccessTable(Connection conn) {
        try {
            FiltersStatus fs = xmlProps.getFilters();
            String createTable = "CREATE TABLE " + xmlProps.getDbFatName() + 
                    "(tsmp TIME NOT NULL, ";// PRIMARY KEY
            if (fs.isTCPActivated()) {
                createTable += "tcp INTEGER NOT NULL, ";
            }
            if (fs.isUDPActivated()) {
                createTable += "udp INTEGER NOT NULL, ";
            }
            if (fs.isICMPActivated()) {
                createTable += "icmp INTEGER NOT NULL, ";
            }
            if (fs.isTCPACKActivated()) {
                createTable += "tcpack INTEGER NOT NULL, ";
            }
            if (fs.isTCPFINActivated()) {
                createTable += "tcpfin INTEGER NOT NULL, ";
            }
            if (fs.isTCPSYNActivated()) {
                createTable += "tcpsyn INTEGER NOT NULL, ";
            }
            createTable = createTable.substring(0, createTable.length() - 2);
            createTable += ");";
            System.out.println(createTable);
            PreparedStatement pstm = conn.prepareStatement(createTable);
            pstm.execute();
            pstm.close();
            System.out.println("InsertDB: Criada a nova Tabela de Dados.");
        } catch (SQLException ex) {
            System.out.println("Tabela descrita já existe.");
            Logger.getLogger(DBFastAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void removeOldRows() {
        Connection conn = null;
        try {
            Class.forName(xmlProps.getDBClassName());
            conn = DriverManager.getConnection(
            "jdbc:" + xmlProps.getDBName() + "://" +
                   xmlProps.getDbHostName() + ":" + xmlProps.getDbPort() +
                   "/" + xmlProps.getDbLocation(), xmlProps.getDbUsername(),
                   xmlProps.getDbPassword());
            Date today = new Date();
            Timestamp tsp = new Timestamp(today.getTime());
            tsp.setTime(tsp.getTime() - THIRTY_MINUTES);
            String query = "DELETE FROM " + xmlProps.getDbFatName() + " WHERE TSMP < ?;";
            PreparedStatement backwardRows = conn.prepareStatement(query);
            backwardRows.setTimestamp(1, tsp);
            backwardRows.execute();
            backwardRows.close();
        } catch (SQLException ex) {
            System.out.println("InsertDB: Não foi possível inserir uma linha na FAT.");
            Logger.getLogger(DBFastAccess.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException e) {
                System.out.println("ClassNotFound...");  
                e.printStackTrace();  
        } finally {  
            try {
                conn.close();  
            } catch (SQLException onConClose) {  
                System.out.println("Error on closing");  
                onConClose.printStackTrace();  
            }
        }
    }
    
    public void insertItem(DBData dbd) {
        Connection conn = null;
        try {
            Class.forName(xmlProps.getDBClassName());
            conn = DriverManager.getConnection(
            "jdbc:" + xmlProps.getDBName() + "://" +
                   xmlProps.getDbHostName() + ":" + xmlProps.getDbPort() +
                   "/" + xmlProps.getDbLocation(), xmlProps.getDbUsername(),
                   xmlProps.getDbPassword());
            String insertRow = "INSERT INTO " +
                     xmlProps.getDbFatName() + " VALUES(?,";
            int[] counters = dbd.getCounters();
            for (int i = 0; i < xmlProps.getFilters().getNumberOfActivatedFilters(); i++) {
                insertRow += counters[i] + ",";
            }
            insertRow = insertRow.substring(0, insertRow.length() - 1);
            insertRow += ");";
            //System.out.println("InsertDB: " + insertRow);
            PreparedStatement pstm = conn.prepareStatement(insertRow);
            pstm.setTimestamp(1, dbd.getTimestamp());
            pstm.execute();
        } catch (SQLException ex) {
            System.out.println("InsertDB: Não foi possível inserir uma linha na FAT.");
            Logger.getLogger(DBFastAccess.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException e) {
                System.out.println("ClassNotFound...");  
                e.printStackTrace();  
        } finally {  
            try {
                conn.close();  
            } catch (SQLException onConClose) {  
                System.out.println("Error on closing");  
                onConClose.printStackTrace();  
            }
        }
    }
    
    private boolean shouldICreateFAT(Connection conn) {
         try {
             PreparedStatement pstm = conn.prepareStatement("SELECT COUNT(tsmp) FROM " + 
                     xmlProps.getDbFatName() + ";");
             ResultSet rs = pstm.executeQuery();
             return false;
         } catch (SQLException ex) {
             return true;
         }
    }    
}
