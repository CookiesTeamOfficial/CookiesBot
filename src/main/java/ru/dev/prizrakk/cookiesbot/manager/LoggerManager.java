package ru.dev.prizrakk.cookiesbot.manager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LoggerManager {
    private static final LoggerManager instance = new LoggerManager(); // Синглтон экземпляр
    private final ColorManager color = new ColorManager();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    // Приватный конструктор для запрета создания экземпляра класса
    public LoggerManager() {}

    // Глобальный доступ к экземпляру логгера
    public static LoggerManager getInstance() {
        return instance;
    }

    private String getTimestamp() {
        return dateFormat.format(new Date());
    }

    private void log(String level, String colorCode, String message) {
        String timestamp = getTimestamp();
        String[] lines = message.split("\n");
        for (String line : lines) {
            System.out.println("[" + timestamp + " " + colorCode + level + color.ANSI_RESET + "] " + line);
        }
    }

    public void info(String message) {
        log("INFO", color.ANSI_GREEN, message);
    }

    public void debug(String message) {
        log("DEBUG", color.ANSI_BLUE, message);
    }

    public void warn(String message) {
        log("WARN", color.ANSI_YELLOW, message);
    }

    public void error(String message) {
        log("ERROR", color.ANSI_RED, message);
    }

    public void error(String message, Exception e) {
        error(message);
        if (e != null) {
            log("ERROR", color.ANSI_RED, e.toString());
            for (StackTraceElement element : e.getStackTrace()) {
                log("ERROR", color.ANSI_RED, "\tat " + element.toString());
            }
        }
    }
    public void criticalError(String message) {
        log("!!!ALERT!!!", color.ANSI_RED, message);
    }
}
