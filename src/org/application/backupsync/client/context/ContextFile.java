/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.application.backupsync.client.context;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import org.application.backupsync.client.context.commands.CommandFile;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author enrico
 */
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

        exit = Boolean.FALSE;
        switch (command.getString("name")) {
            case "list":
                this.cmdListFile(command.getString("directory"), command.getBoolean("acl"));
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
