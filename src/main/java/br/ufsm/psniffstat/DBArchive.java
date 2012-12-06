/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufsm.psniffstat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tulkas
 */
public class DBArchive {
    
    private XMLProperties xmlProps;
    private String currentTableName;
    
    public DBArchive(XMLProperties xmlProps) {
        this.xmlProps = xmlProps;
        currentTableName = xmlProps.getDbArchiveTableName();
        Connection conn = null;
        try {
            Class.forName(xmlProps.getDBClassName());
            conn = DriverManager.getConnection(
            "jdbc:" + xmlProps.getDBName() + "://" +
                   xmlProps.getDbHostName() + ":" + xmlProps.getDbPort() +
                   "/" + xmlProps.getDbLocation(), xmlProps.getDbUsername(),
                   xmlProps.getDbPassword());
            if (shouldICreateArchiveTable(conn)) {
                createNewTable(conn);
            }
        } catch (ClassNotFoundException e) {
            System.out.println("ClassNotFound...");  
            e.printStackTrace();  
        } catch (SQLException e) {
        } finally {  
            try {
                conn.close();  
            } catch (SQLException onConClose) {  
                System.out.println("Error on closing");  
                onConClose.printStackTrace();  
            }
        }
    }

    private boolean shouldICreateArchiveTable(Connection conn) {
        try {
             PreparedStatement pstm = conn.prepareStatement("SELECT COUNT(tsmp) FROM " + 
                     currentTableName + ";");
             ResultSet rs = pstm.executeQuery();
             return false;
         } catch (SQLException ex) {
             return true;
         }
    }
    
    private void createNewTable(Connection conn) {
        try {
            FiltersStatus fs = xmlProps.getFilters();
            String createTable = "CREATE TABLE " + currentTableName + 
                    "(tsmp TIME NOT NULL PRIMARY KEY, ";
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
            System.out.println("ArchiveDB: Criada a nova Tabela de Dados.");
        } catch (SQLException ex) {
            System.out.println("Tabela descrita já existe.");
            Logger.getLogger(DBFastAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void testIfDayHasChanged(Connection conn) {
        if (!currentTableName.equals(xmlProps.getDbArchiveTableName())) {
            currentTableName = xmlProps.getDbArchiveTableName();
            createNewTable(conn);
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
            testIfDayHasChanged(conn);
            String insertRow = "INSERT INTO " +
                     currentTableName + " VALUES(?,";
            int[] counters = dbd.getCounters();
            for (int i = 0; i < xmlProps.getFilters().getNumberOfActivatedFilters(); i++) {
                insertRow += counters[i] + ",";
            }
            insertRow = insertRow.substring(0, insertRow.length() - 1);
            insertRow += ");";
            PreparedStatement pstm = conn.prepareStatement(insertRow);
            pstm.setTimestamp(1, dbd.getTimestamp());
            pstm.execute();
        } catch (SQLException ex) {
            System.out.println("ArchiveDB: Não foi possível inserir uma linha.");
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
    
}
