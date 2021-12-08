package com.apssouza.mytrade.common.file;


import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * A helper for working with files
 */
public class FileDiskHelper {

    private static final Logger LOG = Logger.getLogger(FileDiskHelper.class.getSimpleName());

    public static void createFolder(String path) {
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    public static boolean isFile(String path) {
        File file = new File(path);
        return file.isFile();
    }

    public static boolean isDirectory(String path) {
        File file = new File(path);
        return file.isDirectory();
    }

    public static boolean delete(String filename) {
        try {
            File file = new File(filename);
            return file.delete();
        } catch (Exception e) {
            LOG.severe("Error in deleting the file " + filename);
        }
        return false;
    }

    private static void close(Closeable o) {
        try {
            if (o != null) {
                o.close();
            }
        } catch (IOException ex) {
            LOG.severe("Error in closing resource" + ex.getMessage());
        }
    }

}