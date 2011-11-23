/*
    Copyright (C) 2011 Enrico Bianchi (enrico.bianchi@ymail.com)
    Project       BackupSYNC
    Description   A backup system
    License       GPL version 2 (see GPL.txt for details)
 */

package org.application.backupsync.server;

import org.ini4j.Wini;

public class Sync {
    private String mode;
    private Wini cfg;
    
    public Sync(Wini cfg) {
        this.cfg = cfg;
    }

    /**
     * @return the mode
     */
    public String getMode() {
        return mode;
    }

    /**
     * @param mode the mode to set
     */
    public void setMode(String mode) {
        this.mode = mode;
    }
    
    public void go() {
        
    }
}
