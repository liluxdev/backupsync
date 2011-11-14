/*
    Copyright (C) 2011 Enrico Bianchi (enrico.bianchi@ymail.com)
    Project       BackupSYNC
    Description   A backup system
    License       GPL version 2 (see GPL.txt for details)
 */

package org.application.backupsync.client;

import java.io.IOException;
import java.net.UnknownHostException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.application.backupsync.client.listener.Serve;
import org.json.JSONException;

public class Main {
    
    private Options opts;
    
    public Main() {
        this.opts = new Options();

        this.opts.addOption("h", "help", false, "Print this help");
        this.opts.addOption(OptionBuilder.withLongOpt("port")
                .withDescription("Set port number")
                .hasArg()
                .withArgName("PORT")
                .create("p")
        );
    }
    
    public void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("BackupSYNC", this.opts);
        System.exit(0);
    }
    
    public void go(String[] args) throws ParseException, ClassCastException {
        CommandLine cmd;
        CommandLineParser parser;
        Serve serve;

        parser = new PosixParser();

        cmd = parser.parse(this.opts, args);

        if (cmd.hasOption("h") || cmd.hasOption("help")) {
            this.printHelp();
        }

        if (!cmd.hasOption("p")) {
            throw new ParseException("No port defined!");
        }

        serve = new Serve(Integer.parseInt(cmd.getOptionValue("p")));
        try {
            serve.go();
        } catch (UnknownHostException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.FATAL, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.FATAL, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.FATAL, null, ex);
        }
    }
    
    public static void main(String[] args) {
        Main m;
        
        m = new Main();
        try {
            m.go(args);
        } catch (ParseException ex) {
            System.err.println("Error when passing command: " + ex.getMessage());
            System.exit(2);
        } catch (ClassCastException ex) {
            System.err.println("Port not valid");
            System.exit(3);
        }
    }
}
