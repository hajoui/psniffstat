package br.ufsm.psniffstat.database;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Represents database table structure with the structure
 * <p>
 * tsp|counter[0]|counter[1]|...
 * @author Tulkas
 */
public class DBData implements Serializable {

    private Timestamp tsp;
    private int[] counters;
    
    public DBData(int numFiltersActive) {
        tsp = null;
        counters = new int[numFiltersActive];
    }
    
    public void setTimestamp(Timestamp tsp) {
        this.tsp = tsp;
    }
    
    public void setCounters(int[] counters) {
        for (int i = 0; i < counters.length; i++) {
            this.counters[i] = counters[i];
        }
    }
    
    public Timestamp getTimestamp() {
        return tsp;
    }
    
    public int[] getCounters() {
        return counters;
    }
    
}
