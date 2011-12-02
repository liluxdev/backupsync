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
import org.application.backupsync.client.fetcher.FetchFile;
import org.json.JSONException;
import org.json.JSONObject;

public class Serve implements AutoCloseable {

    private Integer port;
    private BufferedReader in;
    private ServerSocket socket;
    private PrintWriter out;

    public Serve(Integer port) {
        this.port = port;
    }

    public Boolean listen() throws UnknownHostException, IOException {
        Boolean exit;

        Socket connection;
        JSONObject inJSON, outJSON, errJSON;

        exit = Boolean.FALSE;
        connection = socket.accept();

        in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        out = new PrintWriter(connection.getOutputStream(), true);

        try {
            inJSON = new JSONObject(in.readLine());
            switch (inJSON.getString("context")) {
                case "file":
                    switch (inJSON.getString("command")) {
                        case "list":
                            // TODO: write code for list files and directories (remember to calculate MD5 hash)
                            outJSON = new FetchFile(inJSON.getString("directory"), inJSON.getBoolean("acl")).getJSON();
                            outJSON.append("result", "ok");
                            out.println(outJSON.toString());
                            break;
                        default:
                            errJSON = new JSONObject();
                            errJSON.append("result", "error");
                            errJSON.append("message", "Command not found");
                            out.println(errJSON.toString());
                            break;
                    }
                    break;
                case "system":
                    switch (inJSON.getString("command")) {
                        case "exit":
                            exit = Boolean.TRUE;
                            break;
                        default:
                            errJSON = new JSONObject();
                            errJSON.append("result", "error");
                            errJSON.append("message", "Command not found");
                            out.println(errJSON.toString());
                            break;
                    }
                    break;
                default:
                    errJSON = new JSONObject();
                    errJSON.append("result", "error");
                    errJSON.append("message", "Context not found");
                    out.println(errJSON.toString());
                    break;
            }
        } catch (JSONException | FileNotFoundException | IllegalArgumentException | NullPointerException ex) {
            try {
                errJSON = new JSONObject();
                errJSON.append("result", "error");
                
                if (ex instanceof FileNotFoundException ||
                    ex instanceof IllegalArgumentException) {
                    errJSON.append("message", "File or path not found");
                } else if (ex instanceof NullPointerException) {
                    errJSON.append("message", "Buffer error");
                } else {
                    errJSON.append("message", "Malformed command");
                }
                errJSON.append("error", ex.getCause());
                out.println(errJSON.toString());
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
        this.in.close();
        this.out.close();
        this.socket.close();
    }
}
