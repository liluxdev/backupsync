/*
Copyright (C) 2011 Enrico Bianchi (enrico.bianchi@ymail.com)
Project       BackupSYNC
Description   A backup system
License       GPL version 2 (see GPL.txt for details)
 */
package org.application.backupsync;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
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

    private File file;

    public PathName(File fileName) {
        this.file = fileName;
    }

    private void validateDir() throws FileNotFoundException, IllegalArgumentException {

        if (this.file == null) {
            throw new IllegalArgumentException("Directory should not be null.");
        }
        if (!this.file.exists()) {
            throw new FileNotFoundException("Directory does not exist: " + this.file.getAbsolutePath());
        }
        if (!this.file.isDirectory()) {
            throw new IllegalArgumentException("Is not a directory: " + this.file.getAbsolutePath());
        }
        if (!this.file.canRead()) {
            throw new IllegalArgumentException("Directory cannot be read: " + this.file.getAbsolutePath());
        }
    }

    private void aclFromWindows() {
        // TODO: write code for Windows ACL extraction
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void aclFromLinux() {
        // TODO: write code for Linux ACL extraction
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public String hash() throws NoSuchAlgorithmException, IOException {
        FileInputStream fis;
        MessageDigest md;
        String hex;
        StringBuffer hexString;
        byte[] dataBytes;
        int nread;

        md = MessageDigest.getInstance("MD5");
        fis = new FileInputStream(this.file);
        dataBytes = new byte[4096];
        hexString = new StringBuffer();

        nread = 0;
        while ((nread = fis.read(dataBytes)) != -1) {
            md.update(dataBytes, 0, nread);
        }

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

    public List<File> walk() throws IOException {
        final List<File> result;
        Path start;

        result = new ArrayList<>();

        this.validateDir();

        start = FileSystems.getDefault().getPath(this.file.getAbsolutePath());
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                result.add(file.toFile());
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException ex) {
                result.add(dir.toFile());
                return FileVisitResult.CONTINUE;
            }
        });
        return result;
    }

    public JSONObject getAttrs() throws IOException, JSONException {
        JSONObject result;
        BasicFileAttributes attr;
        DosFileAttributes dosAttr;
        PosixFileAttributes posixAttr;

        result = new JSONObject();
        attr = Files.readAttributes(this.file.toPath(), BasicFileAttributes.class);

        result.append("ctime", attr.creationTime().toMillis());
        result.append("mtime", attr.lastModifiedTime().toMillis());
        //result.append("symlink", attr.isSymbolicLink()); //Redundant
        result.append("size", attr.size());

        if (System.getProperty("os.name").startsWith("Windows")) {
            dosAttr = Files.readAttributes(this.file.toPath(), DosFileAttributes.class);

            result.append("dos:archive", dosAttr.isArchive());
            result.append("dos:hidden", dosAttr.isHidden());
            result.append("dos:readonly", dosAttr.isReadOnly());
            result.append("dos:system", dosAttr.isSystem());
        } else {
            posixAttr = Files.readAttributes(this.file.toPath(), PosixFileAttributes.class);

            result.append("posix:symlink", posixAttr.isSymbolicLink());
            result.append("posix:owner", posixAttr.owner());
            result.append("posix:group", posixAttr.group());
            result.append("posix:permission", PosixFilePermissions.toString(posixAttr.permissions()));
        }

        return result;
    }
    
    public JSONObject getAcl() throws JSONException {
        JSONObject result;
        
        result = new JSONObject();
        if (System.getProperty("os.name").startsWith("Windows")) {
            aclFromWindows();
        } else {
            aclFromLinux();
        }
        
        // TODO: Populate the JSON object
        
        return result;
    }
}
