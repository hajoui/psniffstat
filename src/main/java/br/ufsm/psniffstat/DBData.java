/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufsm.psniffstat;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 *
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
