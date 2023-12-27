package com.trulyao.spawn.utils;

import java.nio.file.FileSystems;

final public class AppConstants {
    public static final String APP_NAME = "Spawn";
    public static final String APP_VERSION = "0.0.1";
    public static final String APP_AUTHOR = "Ayodeji Osasona";
    public static final String APP_AUTHOR_EMAIL = "ayodeji@trulyao.dev";
    public static final int HEIGHT = 768;
    public static final int WIDTH = 1200;

    public enum PathKey {
        DATA_DIR,
        LOG_DIR,
        STORE;

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
}
