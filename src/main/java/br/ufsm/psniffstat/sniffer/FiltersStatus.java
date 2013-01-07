package br.ufsm.psniffstat.sniffer;

/**
 * This class holds configuration flags for Sniffer configuration
 * @author Tulkas
 */
public class FiltersStatus {

    private boolean tcp;
    private boolean udp;
    private boolean icmp;
    private boolean tcpACK;
    private boolean tcpFIN;
    private boolean tcpSYN;
    private int numberOfActiveFilters;
    
    public FiltersStatus() {
        tcp = false;
        udp = false;
        icmp = false;
        tcpSYN = false;
        tcpFIN = false;
        tcpACK = false;
        numberOfActiveFilters = 0;
    }
    
    /* ***************************************
     * Funções para alterar status dos filtros
     * ***************************************/
    
    public void activateTCP() {
        if (!tcp) {
            tcp = true;
            numberOfActiveFilters++;
        }
    }
    
    public void deactivateTCP() {
        if (tcp) {
            tcp = false;
            numberOfActiveFilters--;
        }
    }
    
    public void activateUDP() {
        if (!udp) {
            udp = true;
            numberOfActiveFilters++;
        }
    }
    
    public void deactivateUDP() {
        if (udp) {
            udp = false;
            numberOfActiveFilters--;
        }
    }
    
    public void activateICMP() {
        if (!icmp) {
            icmp = true;
            numberOfActiveFilters++;
        }
    }
    
    public void deactivateICMP() {
        if (icmp) {
            icmp = false;
            numberOfActiveFilters--;
        }
    }
    
    public void activateTCPSYN() {
        if (!tcpSYN) {
            tcpSYN = true;
            numberOfActiveFilters++;
        }
    }
    
    public void deactivateTCPSYN() {
        if (tcpSYN) {
            tcpSYN = false;
            numberOfActiveFilters--;
        }
    }
    
    public void activateTCPFIN() {
        if (!tcpFIN) {
            tcpFIN = true;
            numberOfActiveFilters++;
        }
    }
    
    public void deactivateTCPFIN() {
        if (tcpFIN) {
            tcpFIN = false;
            numberOfActiveFilters--;
        }
    }
    
    public void activateTCPACK() {
        if (!tcpACK) {
            tcpACK = true;
            numberOfActiveFilters++;
        }
    }
    
    public void deactivateTCPACK() {
        if (tcpACK) {
            tcpACK = false;
            numberOfActiveFilters--;
        }
    }
    
    /*******************************************
     * Funções para retornar valores dos filtros
     *******************************************/
    
    public boolean isTCPActivated() {return tcp;}
    public boolean isUDPActivated() {return udp;}
    public boolean isICMPActivated() {return icmp;}
    public boolean isTCPSYNActivated() {return tcpSYN;}
    public boolean isTCPFINActivated() {return tcpFIN;}
    public boolean isTCPACKActivated() {return tcpACK;}
    public int getNumberOfActivatedFilters() {return numberOfActiveFilters;}
    
}
