package com.apssouza.mytrade.trading.misc.helper.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReadFileHelper {

    public static Stream<String> getStream(String path) {
        try (BufferedReader br = Files.newBufferedReader(Paths.get(path))) {
            return br.lines();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getList(String path) {
        Stream<String> stream = getStream(path);
        return stream.collect(Collectors.toList());
    }

    public static String getString(String path) {
        Stream<String> stream = getStream(path);
        return stream.collect(Collectors.joining("\n"));
    }

    public static Stream<String> readFromInputStream(InputStream inputStream) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            return br.lines();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
