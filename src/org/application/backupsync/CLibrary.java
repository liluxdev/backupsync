/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.application.backupsync;

/**
 *
 * @author ebianchi
 */

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

