/*
    Copyright (C) 2011 Enrico Bianchi (enrico.bianchi@ymail.com)
    Project       BackupSYNC
    Description   A backup system
    License       GPL version 2 (see GPL.txt for details)
 */

package org.application.backupsync.client.fetcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import org.application.backupsync.PathName;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author ebianchi
 */
public class FetchFile {
    
    private JSONObject json;

    public FetchFile(String aDirectory, Boolean acl) throws JSONException, IOException {
        this.json = new JSONObject();
        this.json = this.list(aDirectory, acl);
    }
    
    private JSONObject list(String directory, Boolean acl) throws JSONException, FileNotFoundException, IOException {
        JSONObject result;
        JSONObject data;

        result = new JSONObject();
        for (File item : new PathName(new File(directory)).walk()) {
            if (item.isDirectory()) {
                data = new JSONObject();
                data.append("type", "directory");
            } else {
                data = new JSONObject();
                data.append("type", "file");
                try {
                    data.append("hash", new PathName((item)).hash());
                } catch (NoSuchAlgorithmException | IOException ex) {
                    data.append("hash", "");
                }
            }
            data.append("attrs", new PathName(item).getAttrs());
            
            if (acl) {
                data.append("acl", new PathName(item).getAcl());
            }            
            result.append(item.getAbsolutePath(), data.toString());
        }
        return result;
    }
    
    public JSONObject getJSON() {
        return this.json;
    }
}
