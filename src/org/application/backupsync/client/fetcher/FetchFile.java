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
import org.application.backupsync.WalkFileTree;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author ebianchi
 */
public class FetchFile extends AbstractFetch {
    
    public FetchFile(String aDirectory, Boolean acl) throws JSONException, IOException {
        this.json = new JSONObject();
        this.json = this.list(aDirectory, acl);
    }
    
    private JSONObject computeAttrs(File fileName) {
        JSONObject result;

        result = new JSONObject();

        // TODO: write code to retrieve attributes

        return result;
    }
    
    private JSONObject computeACL(File fileName) {
        JSONObject result;
        result = new JSONObject();

        // TODO: write code to retrieve ACL

        return result;
    }
    
    private JSONObject list(String directory, Boolean acl) throws JSONException, FileNotFoundException, IOException {
        JSONObject result;
        JSONObject data;

        result = new JSONObject();
        for (File item : (new WalkFileTree(directory)).get()) {
            if (item.isDirectory()) {
                data = new JSONObject();
                data.append("type", "directory");
            } else {
                data = new JSONObject();
                data.append("type", "file");
                try {
                    data.append("hash", this.hash(item.getAbsolutePath()));
                } catch (NoSuchAlgorithmException ex) {
                    data.append("hash", "");
                } catch (IOException ex) {
                    data.append("hash", "");
                }
            }
            data.append("attrs", this.computeAttrs(item));
            
            if (acl) {
                data.append("acl", this.computeACL(item));
            }            
            result.append(item.getAbsolutePath(), data.toString());
        }
        return result;
    }
}
