/*
    Copyright (C) 2011 Enrico Bianchi (enrico.bianchi@ymail.com)
    Project       BackupSYNC
    Description   A backup system
    License       GPL version 2 (see GPL.txt for details)
 */

package org.application.backupsync.server;

import java.io.FileInputStream;
import java.io.IOException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.ini4j.Wini;

public class Main {
    private Options opts;
    private String mode;
    private Wini cfg;
    
    public static final String VERSION = "1.0";
    public static Logger logger = Logger.getLogger("BackupSYNC");

    public Main() {
        this.opts = new Options();

        this.opts.addOption("h", "help", false, "Print this help");
        this.opts.addOption(OptionBuilder.withLongOpt("cfg")
                .withDescription("Use the specified configuration file")
                .hasArg()
                .withArgName("CFGFILE")
                .create("c")
        );
        this.opts.addOption("H", false, "Hourly backup is executed");
        this.opts.addOption("D", false, "Daily backup is executed");
        this.opts.addOption("W", false, "Weekly backup is executed");
        this.opts.addOption("M", false, "Monthly backup is executed");
        
    }
    
    /**
     * Create log via log4j
     */
    private void setLog() {
        Appender appender;
        
        try {
            appender = new FileAppender(new PatternLayout("%d %-5p %c - %m%n"),
                                        this.cfg.get("general", "log_file"));
            Main.logger.addAppender(appender);
            Main.logger.setLevel(Level.toLevel(this.cfg.get("general", "log_level")));
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.FATAL, null, ex);
            System.exit(2);
        } catch (SecurityException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.FATAL, null, ex);
            System.exit(2);
        }
    }
    
    /**
     * Open the configuration file
     * @param cfgFile a configuration file
     */
    private void setCfg(String cfgFile) {
        try {
            this.cfg = new Wini();
            this.cfg.load(new FileInputStream(cfgFile));
        } catch (IOException ex) {
            Main.logger.error(null, ex);
            System.exit(1);
        }
    }

    public void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("BackupSYNC", this.opts);
        System.exit(0);
    }
    
    public void go(String[] args) throws ParseException {
        CommandLine cmd;
        CommandLineParser parser;
        
        parser = new PosixParser();
        cmd = parser.parse(this.opts, args);

        if (cmd.hasOption("h") || cmd.hasOption("help")) {
            this.printHelp();
        }

        if (!cmd.hasOption("cfg")) {
            System.out.println("No configuration file defined (see help)");
            System.exit(1);
        } else {
            this.setCfg(cmd.getOptionValue("cfg"));
        }

        if (cmd.hasOption("H")) {
            this.mode = "hour";
        }
        else if (cmd.hasOption("D")) {
            this.mode = "day";
        } else if (cmd.hasOption("W")) {
            this.mode = "week";
        }
        else if (cmd.hasOption("M")) {
            this.mode = "month";
        }
            
        this.setLog();
        Main.logger.info("BackupSYNC " + VERSION);
    }
    
    public static void main(String[] args) {
        Main m;
        
        m = new Main();

        if (args.length == 0) {
            m.printHelp();
        }
        
        try {
            m.go(args);
        } catch (ParseException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.FATAL, null, ex);
            System.exit(2);
        }
    }
}