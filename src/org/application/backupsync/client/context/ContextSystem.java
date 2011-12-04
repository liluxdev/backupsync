/*
    Copyright (C) 2011 Enrico Bianchi (enrico.bianchi@ymail.com)
    Project       BackupSYNC
    Description   A backup system
    License       GPL version 2 (see GPL.txt for details)
 */

package org.application.backupsync.client.context;

import java.io.IOException;
import java.net.Socket;
import org.json.JSONException;
import org.json.JSONObject;

public class ContextSystem extends AbstractContext {

    public ContextSystem(Socket connection) {
        this.connection = connection;
    }
    
    @Override
    public Boolean parse(JSONObject command) throws JSONException, IOException {
        Boolean exit;
        ContextError error;
        
        switch (command.getString("name")) {
            case "exit":
                exit = Boolean.TRUE;
                break;
            default:
                error = new ContextError(this.connection);
                error.parse(new JSONObject().append("message", "Command not found"));
                exit = Boolean.FALSE;
                break;
        }
        
        return exit;
    }
}
