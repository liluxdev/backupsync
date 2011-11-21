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
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author ebianchi
 */
public class FileUtils {

    public static String hashFile(File aFile) throws NoSuchAlgorithmException, IOException {
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
    
    public static void validateDir(File directory) throws FileNotFoundException {

        if (directory == null) {
            throw new IllegalArgumentException("Directory should not be null.");
        }
        if (!directory.exists()) {
            throw new FileNotFoundException("Directory does not exist: " + directory);
        }
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Is not a directory: " + directory);
        }
        if (!directory.canRead()) {
            throw new IllegalArgumentException("Directory cannot be read: " + directory);
        }
    }

    public static List<File> walk(File aDirectory) throws IOException {
        final List<File> result;
        Path start;

        result = new ArrayList<>();
        
        validateDir(aDirectory);

        start = FileSystems.getDefault().getPath(aDirectory.getAbsolutePath());
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

    public static JSONObject computeAttrs(File fileName) throws IOException, JSONException {
        JSONObject result;
        BasicFileAttributes attr;
        DosFileAttributes dosAttr;
        PosixFileAttributes posixAttr;

        result = new JSONObject();
        attr = Files.readAttributes(fileName.toPath(), BasicFileAttributes.class);

        result.append("ctime", attr.creationTime().toMillis());
        result.append("mtime", attr.lastModifiedTime().toMillis());
        result.append("symlink", attr.isSymbolicLink());
        result.append("size", attr.size());

        if (System.getProperty("os.name").startsWith("Windows")) {
            dosAttr = Files.readAttributes(fileName.toPath(), DosFileAttributes.class);

            result.append("dos:archive", dosAttr.isArchive());
            result.append("dos:hidden", dosAttr.isHidden());
            result.append("dos:readonly", dosAttr.isReadOnly());
            result.append("dos:system", dosAttr.isSystem());
        } else {
            posixAttr = Files.readAttributes(fileName.toPath(), PosixFileAttributes.class);

            result.append("posix:symlink", posixAttr.isSymbolicLink());
            result.append("posix:owner", posixAttr.owner());
            result.append("posix:group", posixAttr.group());
            result.append("posix:permission", PosixFilePermissions.toString(posixAttr.permissions()));
        }

        return result;
    }

    public static JSONObject computeACL(File fileName) throws IOException, JSONException {
        UserDefinedFileAttributeView acls;
        JSONObject result;

        result = new JSONObject();
        if (Files.getFileStore(fileName.toPath()).supportsFileAttributeView(UserDefinedFileAttributeView.class)) {
            acls = Files.getFileAttributeView(fileName.toPath(), UserDefinedFileAttributeView.class);
            result.append("name", acls.toString());
        }
        return result;
    }
}
