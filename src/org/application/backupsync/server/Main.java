/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.application.backupsync.server;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 *
 * @author ebianchi
 */
public class Main {
    private Options opts;

    public Main() {
        this.opts = new Options();

        this.opts.addOption("h", "help", false, "Print this help");
        this.opts.addOption(OptionBuilder.withLongOpt("c")
                .withDescription("Use the specified configuration file")
                .hasArg()
                .withArgName("CFGFILE")
                .create()
        );
        this.opts.addOption("H", false, "Hourly backup is executed");
        this.opts.addOption("D", false, "Daily backup is executed");
        this.opts.addOption("M", false, "Monthly backup is executed");
        
    }
    
    public void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("BackupSYNC", this.opts);
        System.exit(0);
    }
    
    public void go(String[] args) {
        CommandLine cmd;
        CommandLineParser parser;
        
        parser = new PosixParser();
        
        try {
            cmd = parser.parse(this.opts, args);
            
            if (cmd.hasOption("h") || cmd.hasOption("help")) {
                this.printHelp();
            }
            
        } catch (ParseException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args) {
        Main m;
        
        m = new Main();
        
        if (args.length > 0) {
            m.printHelp();
        }
        m.go(args);
    }
}