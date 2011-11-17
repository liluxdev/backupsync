/*
Copyright (C) 2011 Enrico Bianchi (enrico.bianchi@ymail.com)
Project       BackupSYNC
Description   A backup system
License       GPL version 2 (see GPL.txt for details)
 */
package org.application.backupsync;

import java.io.File;
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
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author ebianchi
 */
public class FileUtils {

    public static void validateDir(String directory) throws FileNotFoundException {
        File aDirectory;

        aDirectory = new File(directory);

        if (aDirectory == null) {
            throw new IllegalArgumentException("Directory should not be null.");
        }
        if (!aDirectory.exists()) {
            throw new FileNotFoundException("Directory does not exist: " + aDirectory);
        }
        if (!aDirectory.isDirectory()) {
            throw new IllegalArgumentException("Is not a directory: " + aDirectory);
        }
        if (!aDirectory.canRead()) {
            throw new IllegalArgumentException("Directory cannot be read: " + aDirectory);
        }
    }

    public static List<File> walk(String aDirectory) throws IOException {
        final List<File> result;
        Path start;
        Path res;

        result = new ArrayList<>();
        
        validateDir(aDirectory);

        start = FileSystems.getDefault().getPath(aDirectory);
        res = Files.walkFileTree(start, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                result.add(file.toFile());
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

    public static JSONObject computeACL(File fileName) {
        JSONObject result;
        result = new JSONObject();

        // TODO: write code to retrieve ACL

        return result;
    }
}
