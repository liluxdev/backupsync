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
import org.application.backupsync.client.context.commands.CommandFile;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ContextFile extends AbstractContext {
    public ContextFile(Socket connection) {
        this.connection = connection;
    }
    
    private void cmdListFile(String directory, Boolean acl) throws JSONException, IOException {
        JSONObject result;
        PrintWriter out;
        
        out = new PrintWriter(this.connection.getOutputStream(), true);
        result = new CommandFile(directory, acl).get();
        result.append("result", "ok");
        out.println(result.toString());
    }
    
    private void cmdGetFile(String fileName) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    @Override
    public Boolean parse(JSONObject command) throws JSONException, IOException {
        Boolean exit;
        ContextError error;
        JSONArray paths;

        exit = Boolean.FALSE;
        switch (command.getString("name")) {
            case "list":
                paths = command.getJSONArray("directory");
                
                if (paths.length() == 0) {
                    throw new JSONException("List not definied");
                }
                
                for (int item = 0; item <= paths.length(); item++) {
                    this.cmdListFile(paths.getString(item), command.getBoolean("acl"));
                }
                break;
            case "get":
                this.cmdGetFile(command.getString("file"));
                break;
            default:
                error = new ContextError(this.connection);
                error.parse(new JSONObject().append("message", "Command not found"));
                break;
        }

        return exit;
    }
}
