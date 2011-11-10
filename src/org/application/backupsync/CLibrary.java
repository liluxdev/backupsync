/*
    Copyright (C) 2011 Enrico Bianchi (enrico.bianchi@gmail.com)
    Project       BackupSYNC
    Description   A backup system
    License       GPL version 2 (see GPL.txt for details)
 */

package org.application.backupsync;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

public interface CLibrary extends Library {
    CLibrary INSTANCE = (CLibrary)
        Native.loadLibrary((Platform.isWindows() ? "msvcrt" : "c"),
                           CLibrary.class);
    public int link(String fromFile, String toFile);
    public int chmod(String path, int mode);
    public int lstat(String path, StatStructure stat);
    public int stat(String path, StatStructure stat);

}