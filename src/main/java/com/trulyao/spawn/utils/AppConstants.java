package com.trulyao.spawn.utils;

import java.nio.file.FileSystems;

final public class AppConstants {

    public enum PathKey {
        DATA_DIR,
        LOG_DIR,
        STORE

    }
    private static final String fileSeparator = FileSystems.getDefault().getSeparator();

    public static String getPath(PathKey key) {
        String homeDir = System.getProperty("user.home");

        String path = switch (key) {
            case DATA_DIR -> "data";
            case LOG_DIR -> "logs";
            case STORE -> "data.sqlite";
        };

        return String.format("%s%sspawn/%s", homeDir, fileSeparator, path);
    }

    public static String getFullFilePath(String slugifiedFilename) {
        return getPath(PathKey.DATA_DIR) + fileSeparator + slugifiedFilename;
    }
}
