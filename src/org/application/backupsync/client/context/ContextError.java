/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.application.backupsync.client.context;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author enrico
 */
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
