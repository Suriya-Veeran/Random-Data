package com.randomdata.demo.utility;

import java.io.File;
import java.io.IOException;

import static java.lang.System.out;

public class FileUtils {

    public static void checkCreateDirectory(String fileDir) throws IOException {
        out.println("CreateDirectory = " + fileDir);
        if (!checkForDirectory(fileDir))
            createDir(fileDir);
    }

    public static boolean checkForDirectory(String fileDir) {
        File f;
        f = new File(fileDir);
        return f.isDirectory();
    }

    public static synchronized void createDir(String dir) throws IOException {
        File tmpDir = new File(dir);
        if (!tmpDir.exists()) {
            if (!tmpDir.mkdirs()) {
                throw new IOException("Could not create temporary directory: " + tmpDir.getAbsolutePath());
            }
        } else {
            out.println("Not creating directory, " + dir + ", this directory already exists.");
        }
    }
    public static File createFile(String path) {
        return new File(path);
    }
}
