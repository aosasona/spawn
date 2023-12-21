package com.trulyao.spawn.utils;

import java.nio.file.FileSystems;

final public class AppConstants {
    public static final String APP_NAME = "Seki";
    public static final String APP_VERSION = "0.0.1";
    public static final String APP_AUTHOR = "Ayodeji Osasona";
    public static final String APP_AUTHOR_EMAIL = "ayodeji@trulyao.dev";
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
            case STORE -> ".stores";
        };

        return String.format("%s%s.seki/%s", homeDir, fileSeparator, path);
    }

    public static String getStorePath(String storeName) {
        return String.format("%s%s%s.json", getPath(PathKey.STORE), fileSeparator, storeName);
    }
}
