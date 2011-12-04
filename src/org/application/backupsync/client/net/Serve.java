/*
Copyright (C) 2011 Enrico Bianchi (enrico.bianchi@ymail.com)
Project       BackupSYNC
Description   A backup system
License       GPL version 2 (see GPL.txt for details)
 */
package org.application.backupsync.client.net;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import org.application.backupsync.client.commands.CommandFile;
import org.json.JSONException;
import org.json.JSONObject;

public class Serve implements AutoCloseable {

    private Integer port;
    private ServerSocket socket;

    public Serve(Integer port) {
        this.port = port;
    }

    private void cmdListFile(Socket connection, String directory, Boolean acl) throws JSONException, IOException {
        JSONObject result;
        PrintWriter out;
        
        out = new PrintWriter(connection.getOutputStream(), true);
        result = new CommandFile(directory, acl).get();
        result.append("result", "ok");
        out.println(result.toString());
    }
    
    private void cmdGetFile(Socket connection, String fileName) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void cmdError(Socket connection, String message) throws IOException, JSONException {
        JSONObject result;
        PrintWriter out;
        
        out = new PrintWriter(connection.getOutputStream(), true);
        result = new JSONObject();
        result.append("result", "error");
        result.append("message", message);
        out.println(result.toString());
    }

    private void contextFile(Socket connection, JSONObject command) throws JSONException, IOException {
        switch (command.getString("name")) {
            case "list":
                this.cmdListFile(connection, command.getString("directory"), command.getBoolean("acl"));
                break;
            case "get":
                this.cmdGetFile(connection, command.getString("file"));
                break;
            default:
                this.cmdError(connection, "Command not found");
                break;
        }
    }

    private Boolean contextSystem(Socket connection, JSONObject command) throws JSONException, IOException {
        Boolean exit;
        
        switch (command.getString("name")) {
            case "exit":
                exit = Boolean.TRUE;
                break;
            default:
                this.cmdError(connection, "Command not found");
                exit = Boolean.FALSE;
                break;
        }
        
        return exit;
    }

    public Boolean listen() throws UnknownHostException, IOException {
        Boolean exit;
        BufferedReader in;
        Socket connection;
        JSONObject inJSON;

        exit = Boolean.FALSE;
        connection = socket.accept();

        in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        try {
            inJSON = new JSONObject(in.readLine());
            switch (inJSON.getString("context")) {
                case "file":
                    this.contextFile(connection, inJSON.getJSONObject("command"));
                    break;
                case "system":
                    exit = this.contextSystem(connection, inJSON.getJSONObject("command"));
                    break;
                default:
                    this.cmdError(connection, "Context not found");
                    break;
            }
        } catch (JSONException | FileNotFoundException | IllegalArgumentException | NullPointerException ex) {
            try {
                if (ex instanceof FileNotFoundException ||
                    ex instanceof IllegalArgumentException) {
                    this.cmdError(connection, "File or path not found");
                } else if (ex instanceof NullPointerException) {
                    this.cmdError(connection, "Buffer error");
                } else {
                    this.cmdError(connection, "Malformed command");
                }
            } catch (JSONException ex2) {
            }
        }
        connection.close();

        return exit;
    }

    public void open() throws IOException {
        this.socket = new ServerSocket(port);
    }

    public void close() throws IOException {
        this.socket.close();
    }
}
