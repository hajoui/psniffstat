/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufsm.psniffstat;

/**
 *
 * @author Tulkas
 */
public class DBDataSync {

    private XMLProperties xmlProps;
    private DBData dbData;
    private int numActiveFilters;
    private boolean valueWritten;
    
    public DBDataSync(XMLProperties xmlProps) {
        this.xmlProps = xmlProps;
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
