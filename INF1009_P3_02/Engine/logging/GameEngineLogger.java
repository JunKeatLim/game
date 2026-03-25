package INF1009_P3_02.Engine.logging;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GameEngineLogger {
private String lastMessage = "";

    public enum LogLevel {
        DEBUG(0), INFO(1), WARNING(2), ERROR(3);
        
        public final int level;
        LogLevel(int level) { this.level = level; }
    }

    private PrintWriter writer;
    private final String logFilePath;
    private boolean fileLoggingEnabled;
    private boolean consoleLoggingEnabled;
    private LogLevel minLogLevel; // Only log messages at this level or higher

    public GameEngineLogger(String logFilePath) {
        this.logFilePath = logFilePath;
        this.fileLoggingEnabled = true;
        this.consoleLoggingEnabled = false; // Don't spam console by default
        this.minLogLevel = LogLevel.INFO;  // Only log INFO and above by default
        open();
    }

    private void open() {
        try {
            writer = new PrintWriter(new FileWriter(logFilePath, true)); // append mode
        } catch (IOException e) {
            System.err.println("[EngineLogger] Failed to open log file: " + logFilePath);
            fileLoggingEnabled = false;
        }
    }

    /**
     * Core logging method - filtered by minimum level
     */
    public void log(LogLevel level, String message) {
        if (level.level < minLogLevel.level) return;
        if (!fileLoggingEnabled && !consoleLoggingEnabled) return;
        if (message.equals(lastMessage)) return;

        lastMessage = message;

        String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        String entry = String.format("[%s] [%s] %s", timestamp, level.name(), message);

        if (fileLoggingEnabled && writer != null) {
            writer.println(entry);
            writer.flush();
        }
        if (consoleLoggingEnabled) {
            System.out.println(entry);
        }
    }

    // Convenience methods
    public void debug(String message) {
        log(LogLevel.DEBUG, message);
    }

    public void info(String message) {
        log(LogLevel.INFO, message);
    }

    public void warning(String message) {
        log(LogLevel.WARNING, message);
    }

    public void error(String message) {
        log(LogLevel.ERROR, message);
    }

    // Control what gets logged
    public void setMinLogLevel(LogLevel level) {
        this.minLogLevel = level;
    }

    public void setFileLoggingEnabled(boolean enabled) {
        this.fileLoggingEnabled = enabled;
    }

    public void setConsoleLoggingEnabled(boolean enabled) {
        this.consoleLoggingEnabled = enabled;
    }

    public boolean isFileLoggingEnabled() {
        return fileLoggingEnabled;
    }

    public boolean isConsoleLoggingEnabled() {
        return consoleLoggingEnabled;
    }

    public void close() {
        if (writer != null) {
            writer.flush();
            writer.close();
        }
    }
}
