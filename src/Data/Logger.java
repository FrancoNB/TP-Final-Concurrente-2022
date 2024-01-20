package Data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Logger class is used to log data to files
 */
public class Logger {  
    // Log transitions file
    private final String LOG_TRANSITION = "data/log/logFired.log";
    // Log places file
    private final String LOG_PLACES = "data/log/logInvP.log";
    // Log time file
    private final String LOG_TIME = "data/log/logTimes.log";

    /**
     * Logs data to a file 
     * @param data Data to log
     * @param file File to log to
     * @param append True  if the data should be appended to the file
     *               False otherwise
     */
    private void log(String data, String file, boolean append) {
        try {
            PrintWriter printer = new PrintWriter(new FileWriter(file, append));
            printer.printf("%s-", data);
            printer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clears the log file
     * @param file File to clear
     */
    private void clearLog(String file) {
        log("", file, false);
    }

    /**
     * Creates a directory if it doesn't exist
     * @param dir Directory to create
     */
    private void initDir(String dir) {
        new File(dir).mkdirs();
    }

    /**
     * Creates a log file if it doesn't exist
     * @param file File to create
     */
    private void initLog(String file) {
        try {
            new File(file).createNewFile();
            clearLog(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor for Logger class
     */
    public Logger() {
        initDir("data/log");
        initLog(LOG_TRANSITION);
        initLog(LOG_PLACES);
        initLog(LOG_TIME);
    }

    /**
     * Logs data to the transitions log file
     * @param data Data to log
     */
    public void logTransitions(String data) {
        log(data, LOG_TRANSITION, true);
    }

    /**
     * Logs data to the places log file
     * @param data Data to log
     */
    public void logInvPlaces(String data) {
        log(data, LOG_PLACES, true);
    }

    /**
     * Logs data to the time log file
     * @param data Data to log
     */
    public void logTimed(String data) {
        log(data, LOG_TIME, true);
    }
}
