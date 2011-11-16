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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ebianchi
 */
public class WalkFileTree {

    private List<File> list;

    public WalkFileTree(String directory) throws FileNotFoundException, IOException {
        this.validate(directory);
        this.list = walk(directory);
    }

    private void validate(String directory) throws FileNotFoundException {
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

    private List<File> walk(String aDirectory) throws IOException {
        final List<File> result;
        
        result = new ArrayList<>();
        
        Path start = FileSystems.getDefault().getPath(aDirectory);
        Path res = Files.walkFileTree(start, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                result.add(file.toFile());
                return FileVisitResult.CONTINUE;
            }
        });
                
        return result;
    }
    
    public List<File> get() {
        return this.list;
    }
}
