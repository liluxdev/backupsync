/*
Copyright (C) 2011 Enrico Bianchi (enrico.bianchi@ymail.com)
Project       BackupSYNC
Description   A backup system
License       GPL version 2 (see GPL.txt for details)
 */
package org.application.backupsync.client.listener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import org.application.backupsync.client.fetcher.FetchFile;
import org.json.JSONException;
import org.json.JSONObject;

public class Serve {

    private Integer port;

    public Serve(Integer port) {
        this.port = port;
    }

    public boolean go() throws UnknownHostException, IOException {
        Boolean exit;
        BufferedReader in;
        PrintWriter out;
        ServerSocket socket;
        Socket connection;
        JSONObject inJSON, outJSON, errJSON;

        socket = new ServerSocket(port);
        exit = Boolean.FALSE;

        connection = socket.accept();

        in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        out = new PrintWriter(connection.getOutputStream(), true);

        try {
            inJSON = new JSONObject(in.readLine());

            if (inJSON.getString("command").equals("exit")) {
                exit = Boolean.TRUE;
            } else if (inJSON.getString("command").equals("list")) {
                // TODO: write code for list files and directories (remember to calculate MD5 hash)
                outJSON = new FetchFile(inJSON.getString("directory"), inJSON.getBoolean("acl")).getJSON();
                outJSON.append("result", "ok");
                out.println(outJSON.toString());
            }
        } catch (JSONException ex) {
            try {
                errJSON = new JSONObject();
                errJSON.append("result", "error");
                errJSON.append("message", "Malformed command");
                errJSON.append("error", ex.getCause());
                out.println(errJSON.toString());
            } catch (JSONException ex2) {
            }
        } catch (FileNotFoundException ex) {
            try {
                errJSON = new JSONObject();
                errJSON.append("result", "error");
                errJSON.append("message", "File or path not found");
                errJSON.append("error", ex.getCause());
                out.println(errJSON.toString());
            } catch (JSONException ex2) {
            }
        } catch (NullPointerException ex) {
            try {
                errJSON = new JSONObject();
                errJSON.append("result", "error");
                errJSON.append("message", "Buffer error");
                out.println(errJSON.toString());
            } catch (JSONException ex2) {
            }
        }

        in.close();
        out.close();
        connection.close();
        socket.close();
        
        return exit;
    }
}
