package main.java.com.picasso.Data;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Logger {
    private static final BlockingQueue<LogEntry> transitionsQueue = new LinkedBlockingQueue<>();
    private static final BlockingQueue<LogEntry> systemQueue = new LinkedBlockingQueue<>();
    private static final BlockingQueue<LogEntry> statisticsQueue = new LinkedBlockingQueue<>();

    private static final String LOG_DIR = "data/log";
    private static final String LOG_TRANSITIONS = "transitions.log";
    private static final String LOG_SYSTEM = "system.log";
    private static final String LOG_STATISTICS = "statistics.log";

    static {
        initializeLogDirectory();
        initializeLogFile(LOG_TRANSITIONS);
        initializeLogFile(LOG_SYSTEM);
        initializeLogFile(LOG_STATISTICS);

        Thread transitionsThread = new Thread(() -> processQueue(transitionsQueue, LOG_TRANSITIONS), "[Logger Transitions - Thread 0]");
        transitionsThread.setDaemon(true);
        transitionsThread.start();

        Thread systemThread = new Thread(() -> processQueue(systemQueue, LOG_SYSTEM), "[Logger System - Thread 1]");
        systemThread.setDaemon(true);
        systemThread.start();

        Thread statisticsThread = new Thread(() -> processQueue(statisticsQueue, LOG_STATISTICS), "[Logger Statistics - Thread 2]");
        statisticsThread.setDaemon(true);
        statisticsThread.start();
    }

    public static void logTransition(String message) {
        transitionsQueue.offer(new LogEntry(message, LOG_TRANSITIONS));
    }

    public static void logSystem(String message) {
        systemQueue.offer(new LogEntry(message, LOG_SYSTEM));
    }

    public static void logStatistics(String message) {
        statisticsQueue.offer(new LogEntry(message, LOG_STATISTICS));
    }

    private static void initializeLogDirectory() {
        try {
            Files.createDirectories(Paths.get(LOG_DIR));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initializeLogFile(String fileName) {
        try {
            Path filePath = Paths.get(LOG_DIR, fileName);
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            } else {
                Files.write(filePath, "".getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processQueue(BlockingQueue<LogEntry> logQueue, String fileName) {
        try {
            while (!Thread.interrupted()) {
                LogEntry logEntry = logQueue.take();
                writeToFile(logEntry, fileName);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();  
        }
        finally {
            Logger.logSystem(String.format("FINISHED -> %-35s", Thread.currentThread().getName()));
        }
    }

    private static void writeToFile(LogEntry logEntry, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Paths.get(LOG_DIR, fileName).toString(), true))) {
            writer.write(logEntry.getMessage());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void shutdown() {
        shutdownQueue(transitionsQueue, LOG_TRANSITIONS);
        shutdownQueue(systemQueue, LOG_SYSTEM);
        shutdownQueue(statisticsQueue, LOG_STATISTICS);
    }

    private static void shutdownQueue(BlockingQueue<LogEntry> logQueue, String fileName) {
        while (!logQueue.isEmpty()) {
            LogEntry logEntry = logQueue.poll();
            if (logEntry != null) {
                writeToFile(logEntry, fileName);
            }
        }
    }
}

class LogEntry {
    private final String message;
    private final String fileName;

    public LogEntry(String message, String fileName) {
        this.message = message;
        this.fileName = fileName;
    }

    public String getMessage() {
        return message;
    }

    public String getFileName() {
        return fileName;
    }
}
