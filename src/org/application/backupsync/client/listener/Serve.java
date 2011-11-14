/*
    Copyright (C) 2011 Enrico Bianchi (enrico.bianchi@ymail.com)
    Project       BackupSYNC
    Description   A backup system
    License       GPL version 2 (see GPL.txt for details)
 */

package org.application.backupsync.client.listener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import org.json.JSONException;
import org.json.JSONObject;

public class Serve {
    private Integer port;
    
    public Serve(Integer port) {
        this.port = port;
    }
    
    public void go() throws UnknownHostException, JSONException, IOException {
        Boolean exit;
        BufferedReader in;
        PrintWriter out;
        ServerSocket socket;
        Socket connection;
        JSONObject json;
        
        socket = new ServerSocket(port);
        exit = Boolean.FALSE;
        
        connection = socket.accept();
        while (!exit) {
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            out = new PrintWriter(connection.getOutputStream(), true);

            json = new JSONObject(in.readLine());
            
            if (json.getString("command").equals("exit")) {
                exit = Boolean.TRUE;
                in.close();
                out.close();
                connection.close();
            } else if (json.getString("command").equals("list")) {
                // TODO: write code for list files and directories (remember to calculate MD5 hash)
            }
            
        }
        socket.close();
    }
}
