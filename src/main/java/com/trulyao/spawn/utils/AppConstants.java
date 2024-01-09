package com.trulyao.spawn.utils;

import java.io.File;
import java.nio.file.FileSystems;

final public class AppConstants {

    public enum PathKey {
        DATA_DIR,
        LOG_DIR
    }
    private static final String fileSeparator = FileSystems.getDefault().getSeparator();

    public static String getPath(PathKey key) {

        String path = switch (key) {
            case DATA_DIR -> "data";
            case LOG_DIR -> "logs";
        };

        return AppConstants.getRootAppDirectory() + path;
    }

    private static String getRootAppDirectory() {
        String homeDir = System.getProperty("user.home");
        return String.format("%s%sspawn%s", homeDir, fileSeparator, fileSeparator);
    }

    public static String getFullFilePath(String slugifiedFilename) {
        return getPath(PathKey.DATA_DIR) + fileSeparator + slugifiedFilename;
    }

    public static boolean appDirectoriesExist() {
        File dataDir = new File(getPath(PathKey.DATA_DIR));
        File logDir = new File(getPath(PathKey.LOG_DIR));
        File rootDir = new File(getRootAppDirectory());

        return dataDir.exists() && logDir.exists() && rootDir.exists();
    }

    public static void createAppDirectories() {
        // Create all the directories we need for the app to run at startup
        File rootDir = new File(getRootAppDirectory());
        if (!rootDir.mkdirs()) {
            System.out.println("Failed to create root directory.");
        }

        File dataDir = new File(getPath(PathKey.DATA_DIR));
        if(!dataDir.mkdirs()) {
            System.out.println("Failed to create data directory.");
        }

        File logDir = new File(getPath(PathKey.LOG_DIR));
        if (!logDir.mkdirs()) {
            System.out.println("Failed to create log directory.");
        }
    }
}
