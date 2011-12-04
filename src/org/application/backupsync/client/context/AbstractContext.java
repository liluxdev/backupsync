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
public abstract class AbstractContext {
    
    protected Socket connection;
    
    public abstract Boolean parse(JSONObject command) throws JSONException, IOException;
}
