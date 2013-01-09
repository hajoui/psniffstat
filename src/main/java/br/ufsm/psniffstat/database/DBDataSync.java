package br.ufsm.psniffstat.database;

import br.ufsm.psniffstat.XMLProperties;

/**
 * DBDataSync shared space
 * @author Tulkas
 */
public class DBDataSync {
    private DBData dbData;
    private int numActiveFilters;
    private boolean valueWritten;
    
    /**
     * @param xmlProps sniffer configuration properties
     */
    public DBDataSync(XMLProperties xmlProps) {
        numActiveFilters = xmlProps.getFilters().getNumberOfActivatedFilters();
        dbData = new DBData(numActiveFilters);
        valueWritten = false;
    }
    
    public synchronized void addItem(DBData dbd) {
        if(valueWritten) {
            try {
                wait();
            } catch(InterruptedException e) {
                System.out.println("InterruptedException caught");
            }
        }
        dbData.setCounters(dbd.getCounters());
        dbData.setTimestamp(dbd.getTimestamp());
        valueWritten = true;
        notify();
    }
    
    public synchronized DBData removeItem() {
        if(!valueWritten) {
            try {
                wait();
            } catch(InterruptedException e) {
                System.out.println("InterruptedException caught");
            }
        }
        valueWritten = false;
        notify();
        return dbData;
    }
    
}
