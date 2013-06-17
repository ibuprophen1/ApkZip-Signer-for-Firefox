/*
 *    Copyright (C) 2012 Hai Bison
 *
 *    See the file LICENSE at the root directory of this project for copying
 *    permission.
 */
package group.pals.android.utils.apksigner.utils;

import java.io.File;
import java.net.URLDecoder;

/**
 * System utilities.
 *
 * @author Hai Bison
 */
public class Sys {

    /**
     * Gets the startup directory.
     *
     * @return the startup directory, or {@code null} if any error occurred.
     */
    public static File getStartupDir() {
        try {
            String file = Sys.class.getProtectionDomain().getCodeSource().getLocation().getFile();
            return new File(URLDecoder.decode(file, "utf-8")).getParentFile();
        } catch (Exception e) {
            return null;
        }
    }//getStartupDir()
}