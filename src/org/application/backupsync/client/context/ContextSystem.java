/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.application.backupsync.client.context;

import java.io.IOException;
import java.net.Socket;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author enrico
 */
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
