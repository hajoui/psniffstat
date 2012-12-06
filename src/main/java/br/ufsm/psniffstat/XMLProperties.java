/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufsm.psniffstat;

import java.io.File;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Tulkas
 */
public class XMLProperties {

    // SAXParser
    SAXParserFactory saxFactory;
    SAXParser saxParser;
    
    // Sniffer related
    private FiltersStatus filters;
    private int intervalOfAnalysis;
    private int ammountOfIntervals;
    private int portToFilter;
    
    // Database related
    private String dbName;
    private String dbHostName;
    private String dbPort;
    private String dbLocation;
    private String dbUsername;
    private String dbPassword;
    private boolean dbFatOn;
    private String dbFatName; // Fast Access Table = FAT
    private boolean dbArchiveOn;
    private String dbArchiveTableName;
    
    // Socket related
    private boolean socketsOn;
    private int socketsPort;
    
    public XMLProperties() {
        filters = new FiltersStatus();
        readXML();
    }
    
    private class XMLReader extends DefaultHandler {
        
        private boolean onSniffer, onDatabase, onFilters, onSockets;
        private boolean snPort, snInterval, snAmmount;
        private boolean dbNameF, dbHostF, dbPortF, dbLocationF, dbUsernameF, dbPasswordF, 
                dbStatusFatF, dbFatF, dbStatusArchiveF, dbArchiveF;
        private boolean fsTcp, fsUdp, fsIcmp, fsTcpAck, fsTcpFin, fsTcpSyn;
        private boolean soStatus, soPort;
        
        @Override
        public void startElement(String namespaceURI, String localName,
                           String qName, Attributes atts) {
            if (qName.equals("Sniffer")) {
                onSniffer = true;
            }
            else if (qName.equals("Database")) {
                onDatabase = true;
            }
            else if (qName.equals("Filters")) {
                onFilters = true;
            }
            else if (qName.equals("Sockets")) {
                onSockets = true;
            }
            else {
                if (onSniffer) {
                    if (qName.equals("port")) {
                        snPort = true;
                    }
                    else if (qName.equals("intervalOfAnalysis")) {
                        snInterval = true;
                    }
                    else if (qName.equals("ammountOfIntervals")) {
                        snAmmount = true;
                    }
                }
                if (onDatabase) {
                    if (qName.equals("dbName")) {
                        dbNameF = true;
                    }
                    else if (qName.equals("hostName")) {
                        dbHostF = true;
                    }
                    else if (qName.equals("port")) {
                        dbPortF = true;
                    }
                    else if (qName.equals("location")) {
                        dbLocationF = true;
                    }
                    else if (qName.equals("username")) {
                        dbUsernameF = true;
                    }
                    else if (qName.equals("password")) {
                        dbPasswordF = true;
                    }
                    else if (qName.equals("statusFat")) {
                        dbStatusFatF = true;
                    }
                    else if (qName.equals("fatTableName")) {
                        dbFatF = true;
                    }
                    else if (qName.equals("statusArchive")) {
                        dbStatusArchiveF = true;
                    }
                    else if (qName.equals("archiveTableName")) {
                        dbArchiveF = true;
                    }
                }
                if (onFilters) {
                    if (qName.equals("tcp")) {
                        fsTcp = true;
                    }
                    else if (qName.equals("udp")) {
                        fsUdp = true;
                    }
                    else if (qName.equals("icmp")) {
                        fsIcmp = true;
                    }
                    else if (qName.equals("tcpAck")) {
                        fsTcpAck = true;
                    }
                    else if (qName.equals("tcpFin")) {
                        fsTcpFin = true;
                    }
                    else if (qName.equals("tcpSyn")) {
                        fsTcpSyn = true;
                    }
                }
                if (onSockets) {
                    if (qName.equals("status")) {
                        soStatus = true;
                    }
                    else if (qName.equals("port")) {
                        soPort = true;
                    }
                }
            }
        }
        
        @Override
        public void endElement(String uri, String localName, String qName) {
            if (qName.equals("Sniffer")) {
                onSniffer = false;
            }
            else if (qName.equals("Database")) {
                onDatabase = false;
            }
            else if (qName.equals("Filters")) {
                onFilters = false;
            }
            else if (qName.equals("Sockets")) {
                onSockets = false;
            }
            else {
                if (onSniffer) {
                    if (qName.equals("port")) {
                        snPort = false;
                    }
                    else if (qName.equals("intervalOfAnalysis")) {
                        snInterval = false;
                    }
                    else if (qName.equals("ammountOfIntervals")) {
                        snAmmount = false;
                    }
                }
                if (onDatabase) {
                    if (qName.equals("dbName")) {
                        dbNameF = false;
                    }
                    else if (qName.equals("hostName")) {
                        dbHostF = false;
                    }
                    else if (qName.equals("port")) {
                        dbPortF = false;
                    }
                    else if (qName.equals("location")) {
                        dbLocationF = false;
                    }
                    else if (qName.equals("username")) {
                        dbUsernameF = false;
                    }
                    else if (qName.equals("password")) {
                        dbPasswordF = false;
                    }
                    else if (qName.equals("statusFat")) {
                        dbStatusFatF = false;
                    }
                    else if (qName.equals("fatTableName")) {
                        dbFatF = false;
                    }
                    else if (qName.equals("statusArchive")) {
                        dbStatusArchiveF = false;
                    }
                    else if (qName.equals("archiveTableName")) {
                        dbArchiveF = false;
                    }
                }
                if (onFilters) {
                    if (qName.equals("tcp")) {
                        fsTcp = false;
                    }
                    else if (qName.equals("udp")) {
                        fsUdp = false;
                    }
                    else if (qName.equals("icmp")) {
                        fsIcmp = false;
                    }
                    else if (qName.equals("tcpAck")) {
                        fsTcpAck = false;
                    }
                    else if (qName.equals("tcpFin")) {
                        fsTcpFin = false;
                    }
                    else if (qName.equals("tcpSyn")) {
                        fsTcpSyn = false;
                    }
                }
                if (onSockets) {
                    if (qName.equals("status")) {
                        soStatus = false;
                    }
                    else if (qName.equals("port")) {
                        soPort = false;
                    }
                }
            }
        }
        
        @Override
        public void characters(char ch[], int start, int length) {
            String s = new String(ch, start, length);
            s = s.replaceAll("\n", "").trim();
            if (onSniffer) {
                if (snPort) {
                    portToFilter = Integer.parseInt(s);
                }
                else if (snInterval) {
                    intervalOfAnalysis = Integer.parseInt(s);
                }
                else if (snAmmount) {
                    ammountOfIntervals = Integer.parseInt(s);
                }
            }
            else if (onDatabase) {
                if (dbNameF) {
                    dbName = s;
                }
                if (dbHostF) {
                    dbHostName = s;
                }
                else if (dbPortF) {
                    dbPort = s;
                }
                else if (dbLocationF) {
                    dbLocation = s;
                }
                else if (dbUsernameF) {
                    dbUsername = s;
                }
                else if (dbPasswordF) {
                    dbPassword = s;
                }
                else if (dbStatusFatF) {
                    if (Integer.parseInt(s) == 1) {
                        dbFatOn = true;
                    }
                    else {
                        dbFatOn = false;
                    }
                }
                else if (dbFatF) {
                    dbFatName = s;
                }
                else if (dbStatusArchiveF) {
                    if (Integer.parseInt(s) == 1) {
                        dbArchiveOn = true;
                    }
                    else {
                        dbArchiveOn = false;
                    }
                }
                else if (dbArchiveF) {
                    dbArchiveTableName = s;
                }
            }
            else if (onFilters) {
                if (fsTcp) {
                    int i = Integer.parseInt(s);
                     if (i == 0) {
                         filters.deactivateTCP();
                     }
                     else {
                         filters.activateTCP();
                     }
                }
                else if (fsUdp) {
                    int i = Integer.parseInt(s);
                    if (i == 0) {
                         filters.deactivateUDP();
                     }
                     else {
                         filters.activateUDP();
                     }
                }
                else if (fsIcmp) {
                    int i = Integer.parseInt(s);
                    if (i == 0) {
                         filters.deactivateICMP();
                     }
                     else {
                         filters.activateICMP();
                     }
                }
                else if (fsTcpAck) {
                    int i = Integer.parseInt(s);
                    if (i == 0) {
                         filters.deactivateTCPACK();
                     }
                     else {
                         filters.activateTCPACK();
                     }
                }
                else if (fsTcpFin) {
                    int i = Integer.parseInt(s);
                    if (i == 0) {
                         filters.deactivateTCPFIN();
                     }
                     else {
                         filters.activateTCPFIN();
                     }
                }
                else if (fsTcpSyn) {
                    int i = Integer.parseInt(s);
                    if (i == 0) {
                         filters.deactivateTCPSYN();
                     }
                     else {
                         filters.activateTCPSYN();
                     }
                }
            }
            else if (onSockets) {
                if (soStatus) {
                    if (Integer.parseInt(s) == 1) {
                        socketsOn = true;
                    }
                    else {
                        socketsOn = false;
                    }
                }
                else if (soPort) {
                    socketsPort = Integer.parseInt(s);
                }
            }
        }
    }
    
    private void readXML() {
        /*
         * XML PARSER
         */
        saxFactory = SAXParserFactory.newInstance();
        try {
            saxParser = saxFactory.newSAXParser();
            saxParser.parse(new File(new URI (getClass().getResource("XMLProperties.xml").toString())), 
                                     new XMLReader());
        } catch (Throwable err) {
            err.printStackTrace();
        }
        /*System.out.println("snPort: " + portToFilter +
                "\nsnInterval: " + intervalOfAnalysis +
                "\nsnAmmount: " + ammountOfIntervals);
        System.out.println("dbStatusFat: " + dbFatOn +
                "\ndbFatName: " + dbFatName +
                "\ndbArchive: " + dbArchiveTableName);
        System.out.println("socketsOn: " + socketsOn +
                "\nsocketsPort: " + socketsPort);*/
    }
    
    public FiltersStatus getFilters() {
        return filters;
    }

    public int getPortToFilter() {
        return portToFilter;
    }
    
    public int getInterval() {
        return intervalOfAnalysis;
    }
    
    public int getAmmountIntervals() {
        return ammountOfIntervals;
    }

    public String getDbFatName() {
        if (portToFilter == -1) {
            return dbFatName + "_ALLP";
        } else {
            return dbFatName + "_ONP_" + String.valueOf(portToFilter);
        }
    }

    public boolean isFatOn() {
        return dbFatOn;
    }
    
    public boolean isSocketOn() {
        return socketsOn;
    }
    
    public int getSocketPort() {
        return socketsPort;
    }
    
    public String getDbLocation() {
        return dbLocation;
    }

    public String getDBName() {
        if (dbName.equals("firebird")) {
            return dbName + "sql";
        }
        else if (dbName.equals("mysql")) {
            return dbName;
        }
        else if (dbName.equals("postgre")) {
            return dbName + "sql";
        }
        else {
            System.out.println("Nome do banco inválido. firebird, mysql ou postgre.");
            System.exit(-2);
            return "err";
        }
    }
    
    public String getDBClassName() {
        if (dbName.equals("firebird")) {
            return "org.firebirdsql.jdbc.FBDriver";
        }
        else if (dbName.equals("mysql")) {
            return "com.mysql.jdbc.Driver";
        }
        else if (dbName.equals("postgre")) {
            return "org.postgresql.Driver";
        }
        else {
            System.out.println("Nome do banco inválido. firebird, mysql ou postgre.");
            System.exit(-2);
            return "err";
        }
    }
    
    public String getDbHostName() {
        return dbHostName;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public String getDbPort() {
        return dbPort;
    }

    public boolean isArchiveOn() {
        return dbArchiveOn;
    }
    
    public String getDbArchiveTableName() {
        Date dateNow = new Date();
        SimpleDateFormat dateformatYYYYMMDD = new SimpleDateFormat("yyyy_MM_dd");
        StringBuilder nowYYYYMMDD = new StringBuilder(dateformatYYYYMMDD.format(dateNow));
        if (portToFilter == -1) {
            return dbArchiveTableName + "_ALLP_" + nowYYYYMMDD;
        } else {
            return dbArchiveTableName + "_ONP_" + String.valueOf(portToFilter) + "_" + nowYYYYMMDD;
        }
    }
}
