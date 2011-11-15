/*
    Copyright (C) 2011 Enrico Bianchi (enrico.bianchi@ymail.com)
    Project       BackupSYNC
    Description   A backup system
    License       GPL version 2 (see GPL.txt for details)
 */

package org.application.backupsync.client.fetcher;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.json.JSONObject;

/**
 *
 * @author ebianchi
 */
public abstract class AbstractFetch {

    protected JSONObject json;

    protected String hash(String aFile) throws NoSuchAlgorithmException, IOException {
        FileInputStream fis;
        MessageDigest md;
        String hex;
        StringBuffer hexString;
        byte[] dataBytes;
        int nread;

        md = MessageDigest.getInstance("MD5");
        fis = new FileInputStream(aFile);
        dataBytes = new byte[4096];
        hexString = new StringBuffer();

        nread = 0;
        while ((nread = fis.read(dataBytes)) != -1) {
            md.update(dataBytes, 0, nread);
        };
        byte[] mdbytes = md.digest();

        for (int i = 0; i < mdbytes.length; i++) {
            hex = Integer.toHexString(0xff & mdbytes[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }

    public JSONObject getJSON() {
        return this.json;
    }
}
