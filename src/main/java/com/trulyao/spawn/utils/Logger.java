package com.trulyao.spawn.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class Logger implements AutoCloseable {

    enum LogLevel {
        DEBUG,
        INFO,
        WARNING,
        ERROR,
        FATAL;
    }

    private static Logger instance;
    private static final String LOG_DIR = AppConstants.getPath(AppConstants.PathKey.LOG_DIR);
    private String logFilePath;
    private FileWriter fileWriter;

    Logger() {
        this.logFilePath = this.getAbsoluteCurrentLogFilename();
    }

    @Override
    public void close() throws Exception {
        this.logFilePath = null;
        this.fileWriter.flush();
        this.fileWriter.close();
    }

    public static Logger getSharedInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public void debug(String message, HashMap<String, String> metadata) {
        this.log(LogLevel.DEBUG, message, metadata);
    }

    public void debug(String message) {
        this.debug(message, null);
    }

    public void info(String message, HashMap<String, String> metadata) {
        this.log(LogLevel.INFO, message, metadata);
    }

    public void info(String message) {
        this.log(LogLevel.INFO, message, null);
    }

    public void warning(String message, HashMap<String, String> metadata) {
        this.log(LogLevel.WARNING, message, metadata);
    }

    public void warning(String message) {
        this.log(LogLevel.WARNING, message, null);
    }

    public void error(String message, HashMap<String, String> metadata) {
        this.log(LogLevel.ERROR, message, metadata);
    }

    public void error(String message) {
        this.log(LogLevel.ERROR, message, null);
    }

    public void fatal(String message, HashMap<String, String> metadata) {
        this.log(LogLevel.FATAL, message, metadata);
    }

    public void fatal(String message) {
        this.log(LogLevel.FATAL, message, null);
    }

    private String getAbsoluteCurrentLogFilename() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = dateFormat.format(now);
        return String.format("%s/%s.log", LOG_DIR, dateString);
    }

    // Keeps the current log file until the next call to openLogFile() or the object is closed (by exiting the program)
    private void openLogFile() {
        try {
            File logDir = new File(LOG_DIR);
            if (!logDir.exists()) {
                if (!logDir.mkdirs()) {
                    throw new IOException("Failed to create log directory");
                }
            }

            File logFile = new File(this.logFilePath);
            if (!logFile.exists()) {
                if (!logFile.createNewFile()) {
                    throw new IOException("Failed to create log file");
                }
            }
            this.fileWriter = new FileWriter(logFile, true);
        } catch (Exception e) {
            System.err.println("Failed to open log file: " + e.getMessage());
            System.exit(1);
        }
    }


    // Writes log to file and stdout or stderr (as needed)
    private void log(LogLevel level, String message, HashMap<String, String> metadata) {
        try {
            Objects.requireNonNull(level);
            Objects.requireNonNull(message);

            if (this.fileWriter == null) {
                this.openLogFile();
            }

            StringBuilder metaString = new StringBuilder();
            if (metadata != null) {
                for (var entry : metadata.entrySet()) {
                    metaString.append(String.format(" %s=%s", entry.getKey(), entry.getValue()));
                }
            }
            String logMessage = String.format("[%s] %s %s", level, message, metaString);

            switch (level) {
                case DEBUG, INFO -> System.out.println(logMessage);
                case WARNING, ERROR, FATAL -> System.err.println(logMessage);
            }

            this.appendToFile(logMessage + "\n");
        } catch (Exception e) {
            System.err.println("Failed to log message: " + e.getMessage());
            System.exit(1); // exit the program to prevent further damage
        }
    }

    private void appendToFile(String message) throws IOException {
        this.fileWriter.append(message);
        this.fileWriter.flush();
    }
}
