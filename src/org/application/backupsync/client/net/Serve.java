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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import org.application.backupsync.client.context.AbstractContext;
import org.application.backupsync.client.context.ContextError;
import org.application.backupsync.client.context.ContextFile;
import org.application.backupsync.client.context.ContextSystem;
import org.json.JSONException;
import org.json.JSONObject;

public class Serve implements AutoCloseable {

    private Integer port;
    private ServerSocket socket;

    public Serve(Integer port) {
        this.port = port;
    }

    public Boolean listen() throws UnknownHostException, IOException {
        AbstractContext context;
        Boolean exit;
        BufferedReader in;
        Socket connection;
        JSONObject inJSON;
        JSONObject mex;

        connection = socket.accept();

        in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        try {
            inJSON = new JSONObject(in.readLine());
            switch (inJSON.getString("context")) {
                case "file":
                    context = new ContextFile(connection);
                    exit = context.parse(inJSON.getJSONObject("command"));
                    break;
                case "system":
                    context = new ContextSystem(connection);
                    exit = context.parse(inJSON.getJSONObject("command"));
                    break;
                default:
                    context = new ContextError(connection);
                    exit = context.parse(new JSONObject().append("message", "Context not found"));
                    break;
            }
        } catch (JSONException | FileNotFoundException | IllegalArgumentException | NullPointerException ex) {
            try {
                context = new ContextError(connection);
                mex = new JSONObject();

                if (ex instanceof FileNotFoundException
                        || ex instanceof IllegalArgumentException) {
                    mex.append("message", "Context not found");
                } else if (ex instanceof NullPointerException) {
                    mex.append("message", "Buffer error");
                } else {
                    mex.append("message", "Malformed command");
                }
                exit = context.parse(mex);
            } catch (JSONException ex2) {
                exit = Boolean.FALSE;
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
