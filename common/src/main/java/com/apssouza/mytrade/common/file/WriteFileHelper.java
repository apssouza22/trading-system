package com.apssouza.mytrade.common.file;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class WriteFileHelper {

    public static void write(String filepath, String content) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filepath));
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void append(String filepath, String content) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(filepath, true));
            writer.append(content);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
