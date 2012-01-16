/*
Copyright (C) 2011 Enrico Bianchi (enrico.bianchi@ymail.com)
Project       BackupSYNC
Description   A backup system
License       GPL version 2 (see GPL.txt for details)
 */
package org.application.backupsync;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author enrico
 */
public class PathName {

    private Path path;

    public PathName(Path aPath) {
        this.path = aPath;
    }

    private JSONObject aclFromWindows() throws JSONException {
        // TODO: write code for Windows ACL extraction
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private JSONObject aclFromLinux() throws JSONException, IOException, InterruptedException {
        // NOTE: Need to manage case when external command fail
        BufferedReader bri;
        //BufferedReader bre;
        JSONObject groups;
        JSONObject users;
        JSONObject result;
        Process p;
        String line;

        result = new JSONObject();
        users = new JSONObject();
        groups = new JSONObject();

        p = Runtime.getRuntime().exec("getfacl " + this.path.toString());
        bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
        //bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));

        while ((line = bri.readLine()) != null) {
            if (line.startsWith("user") && !line.contains("::")) {
                users.append("uid", line.split(":")[1]);
                users.append("attrs", line.split(":")[2]);
            } else if (line.startsWith("group") && !line.contains("::")) {
                groups.append("gid", line.split(":")[1]);
                groups.append("attrs", line.split(":")[2]);
            }
        }
        bri.close();

        /*
        while ((line = bre.readLine()) != null) {
            System.out.println(line);
        }
        bre.close();
        */

        p.waitFor();
        //p.exitValue()

        result.append("users", users);
        result.append("groups", groups);

        return result;
    }

    public String hash() throws NoSuchAlgorithmException, IOException {
        FileInputStream fis;
        MessageDigest md;
        String hex;
        StringBuffer hexString;
        byte[] dataBytes;
        int nread;

        md = MessageDigest.getInstance("MD5");
        fis = new FileInputStream(this.path.toFile());
        dataBytes = new byte[65536];
        hexString = new StringBuffer();

        nread = 0;
        try {
            while ((nread = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            }
        } finally {
            fis.close();
        }
        
        byte[] mdbytes = md.digest();

        for (int i = 0; i < mdbytes.length; i++) {
            hex = Integer.toHexString(0xff & mdbytes[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        fis.close();

        return hexString.toString();
    }

    public List<PathName> walk() throws IOException {
        final List<PathName> result;

        result = new ArrayList<>();

        if (Files.isReadable(this.path)) {
            Files.walkFileTree(this.path, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    result.add(new PathName(file));
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException ex) {
                    result.add(new PathName(dir));
                    return FileVisitResult.CONTINUE;
                }
            });
        } else {
            throw new IllegalArgumentException("Directory cannot be read: " + this.path.toString());
        }
        return result;
    }

    public JSONObject getAttrs() throws IOException, JSONException {
        JSONObject result;
        BasicFileAttributes attr;
        DosFileAttributes dosAttr;
        PosixFileAttributes posixAttr;

        result = new JSONObject();
        attr = Files.readAttributes(this.path, BasicFileAttributes.class);

        result.append("ctime", attr.creationTime().toMillis());
        result.append("mtime", attr.lastModifiedTime().toMillis());
        //result.append("symlink", attr.isSymbolicLink()); //Redundant
        result.append("size", attr.size());

        if (System.getProperty("os.name").startsWith("Windows")) {
            dosAttr = Files.readAttributes(this.path, DosFileAttributes.class);

            result.append("dos:archive", dosAttr.isArchive());
            result.append("dos:hidden", dosAttr.isHidden());
            result.append("dos:readonly", dosAttr.isReadOnly());
            result.append("dos:system", dosAttr.isSystem());
        } else {
            posixAttr = Files.readAttributes(this.path, PosixFileAttributes.class);

            result.append("posix:symlink", posixAttr.isSymbolicLink());
            result.append("posix:owner", posixAttr.owner());
            result.append("posix:group", posixAttr.group());
            result.append("posix:permission", PosixFilePermissions.toString(posixAttr.permissions()));
        }

        return result;
    }

    public JSONObject getAcl() throws JSONException, IOException {
        JSONObject result;

        try {
            if (System.getProperty("os.name").startsWith("Windows")) {
                result = aclFromWindows();
            } else {
                result = aclFromLinux();
            }
        } catch (InterruptedException ex) {
            result = null;
        }

        return result;
    }
    
    public Boolean isDirectory() throws IllegalArgumentException, FileNotFoundException {
        return Files.isDirectory(this.path);
    }
    
    public String getAbsolutePath() {
        return this.path.toString();
    }
}
