/*
    Copyright (C) 2011 Enrico Bianchi (enrico.bianchi@ymail.com)
    Project       BackupSYNC
    Description   A backup system
    License       GPL version 2 (see GPL.txt for details)
 */

package org.application.backupsync.client.context;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import org.json.JSONException;
import org.json.JSONObject;

public class ContextError extends AbstractContext {
    public ContextError(Socket connection) {
        this.connection = connection;
    }

    @Override
    public Boolean parse(JSONObject command) throws JSONException, IOException {
        Boolean exit;
        JSONObject result;
        PrintWriter out;

        out = new PrintWriter(connection.getOutputStream(), true);
        result = new JSONObject();
        exit = Boolean.FALSE;

        result.append("result", "error");
        result.append("message", command.get("message"));
        out.println(result.toString());

        return exit;
    }
}
