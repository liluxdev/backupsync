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
import org.application.backupsync.FileListing;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author ebianchi
 */
public class FetchFile extends AbstractFetch {
    
    public FetchFile(String aDirectory) throws JSONException, FileNotFoundException {
        this.json = new JSONObject();
        this.json = this.list(aDirectory);
    }
    
    
    
    private JSONObject list(String directory) throws JSONException, FileNotFoundException {
        JSONObject result;
        JSONObject data;

        result = new JSONObject();
        for (File item : FileListing.getFileListing(new File(directory))) {
            if (item.isDirectory()) {
                data = new JSONObject();
                data.append("type", "directory");
                result.append(item.getAbsolutePath(), data.toString());
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
                result.append(item.getAbsolutePath(), data.toString());
            }
        }
        return result;
    }
}
