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

public abstract class AbstractContext {
    
    protected Socket connection;
    
    public abstract Boolean parse(JSONObject command) throws JSONException, IOException;
}
