package com.picasso.Data;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Logger class is used to log messages to the log files.
 * It uses three queues for three different types of log messages. 
 * Each queue is processed by a separate thread.
 */
public class Logger {
    // Queue for transitions log messages
    private static final BlockingQueue<LogEntry> transitionsQueue = new LinkedBlockingQueue<>();
    // Queue for system log messages
    private static final BlockingQueue<LogEntry> systemQueue = new LinkedBlockingQueue<>();
    // Queue for statistics log messages
    private static final BlockingQueue<LogEntry> statisticsQueue = new LinkedBlockingQueue<>();
    // Queue for artist log messages
    private static final BlockingQueue<LogEntry> artistQueue = new LinkedBlockingQueue<>();
    // Queue for timed out of windows results
    private static final BlockingQueue<LogEntry> timedQueue = new LinkedBlockingQueue<>();

    // Directory where log files are stored
    private static final String LOG_DIR = "data/log";
    
    // Log file for transitions
    private static final String LOG_TRANSITIONS = "transitions.log";
    // Log file for system
    private static final String LOG_SYSTEM = "system.log";
    // Log file for statistics
    private static final String LOG_STATISTICS = "statistics.log";
    // Log file for artists
    private static final String LOG_ARTIST = "artist.log";
    // log timed out of windows results
    private static final String LOG_TIMED = "timed.log";

    /**
     * Static block to initialize log files and start threads for processing queues.
     */
    static {
        initializeLogDirectory();
        initializeLogFile(LOG_TRANSITIONS);
        initializeLogFile(LOG_SYSTEM);
        initializeLogFile(LOG_STATISTICS);
        initializeLogFile(LOG_ARTIST);
        initializeLogFile(LOG_TIMED);

        Thread transitionsThread = new Thread(() -> processQueue(transitionsQueue, LOG_TRANSITIONS), "[Logger Transitions - Thread 0]");
        transitionsThread.setDaemon(true);
        transitionsThread.start();

        Thread systemThread = new Thread(() -> processQueue(systemQueue, LOG_SYSTEM), "[Logger System - Thread 1]");
        systemThread.setDaemon(true);
        systemThread.start();

        Thread statisticsThread = new Thread(() -> processQueue(statisticsQueue, LOG_STATISTICS), "[Logger Statistics - Thread 2]");
        statisticsThread.setDaemon(true);
        statisticsThread.start();

        Thread artistThread = new Thread(() -> processQueue(artistQueue, LOG_ARTIST), "[Logger Artist - Thread 3]");
        artistThread.setDaemon(true);
        artistThread.start();

        Thread timedThread = new Thread(() -> processQueue(timedQueue, LOG_TIMED), "[Logger Timed - Thread 4]");
        timedThread.setDaemon(true);
        timedThread.start();
    }

    /**
     * Logs a transition message to the transitions log file.
     * @param message Message to be logged.
     */
    public static void logTransition(String message) {
        transitionsQueue.offer(new LogEntry(message, LOG_TRANSITIONS));
    }

    /**
     * Logs a system message to the system log file.
     * @param message Message to be logged.
     */
    public static void logSystem(String message) {
        systemQueue.offer(new LogEntry(message, LOG_SYSTEM));
    }

    /**
     * Logs a statistics message to the statistics log file.
     * @param message Message to be logged.
     */
    public static void logStatistics(String message) {
        statisticsQueue.offer(new LogEntry(message, LOG_STATISTICS));
    }

    /**
     * Logs an artist message to the artist log file.
     * @param message Message to be logged.
     */
    public static void logArtist(String message) {
        artistQueue.offer(new LogEntry(message, LOG_ARTIST));
    }

    /**
     * Logs a timed out of windows result to the timed log file.
     * @param message Message to be logged.
     */
    public static void logTimed(String message) {
        timedQueue.offer(new LogEntry(message, LOG_TIMED));
    }

    /**
     * Shuts down the logger.
     * It processes the queues and writes the remaining log entries to the log files.
     */
    public static void shutdown() {
        shutdownQueue(transitionsQueue, LOG_TRANSITIONS);
        shutdownQueue(systemQueue, LOG_SYSTEM);
        shutdownQueue(statisticsQueue, LOG_STATISTICS);
        shutdownQueue(artistQueue, LOG_ARTIST);
        shutdownQueue(timedQueue, LOG_TIMED);
    }

    /**
     * Initializes the log directory.
     * If the directory does not exist, it creates it.
     */
    private static void initializeLogDirectory() {
        try {
            Files.createDirectories(Paths.get(LOG_DIR));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the log file.
     * @param fileName Name of the log file.
     */
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

    /**
     * Processes the queue. It takes the log entry from the queue and writes it to the log file.
     * @param logQueue Queue to be processed.
     * @param fileName Name of the log file.
     */
    private static void processQueue(BlockingQueue<LogEntry> logQueue, String fileName) {
        try {
            while (!Thread.interrupted()) {
                LogEntry logEntry = logQueue.take();
                writeToFile(logEntry, fileName);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); 
            shutdown(); 
        }
        finally {
            Logger.logSystem(String.format("FINISHED -> %-35s", Thread.currentThread().getName()));
        }
    }

    /**
     * Writes the log entry to the log file.
     * @param logEntry Log entry to be written.
     * @param fileName Name of the log file.
     */
    private static void writeToFile(LogEntry logEntry, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Paths.get(LOG_DIR, fileName).toString(), true))) {
            writer.write(logEntry.getMessage());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Processes the queue and writes the remaining log entries to the log file.
     * @param logQueue Queue to be processed.
     * @param fileName Name of the log file.
     */
    private static void shutdownQueue(BlockingQueue<LogEntry> logQueue, String fileName) {
        while (!logQueue.isEmpty()) {
            LogEntry logEntry = logQueue.poll();
            if (logEntry != null) {
                writeToFile(logEntry, fileName);
            }
        }
    }
}

/**
 * LogEntry class represents a log entry.
 * It contains the message and the name of the log file.
 */
class LogEntry {
    // Message to be logged
    private final String message;
    // Name of the log file
    private final String fileName;

    /**
     * Constructor for LogEntry class.
     * @param message Message to be logged.
     * @param fileName Name of the log file.
     */
    public LogEntry(String message, String fileName) {
        this.message = message;
        this.fileName = fileName;
    }

    /**
     * Returns the message.
     * @return String Message to be logged.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the name of the log file.
     * @return String Name of the log file.
     */
    public String getFileName() {
        return fileName;
    }
}
