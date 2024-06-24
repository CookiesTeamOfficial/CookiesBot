package ru.dev.prizrakk.cookiesbot.manager;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

public class LoggerManager {
    private final ColorManager color = new ColorManager();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss ");
    private final String timestamp = dateFormat.format(new Date());
    public void info(String message) {
        System.out.println("[" + timestamp + color.ANSI_GREEN + "INFO" + color.ANSI_RESET + "] " + message);
    }
    public void debug(String message) {
        System.out.println("[" + timestamp + color.ANSI_BLUE + "DEBUG" + color.ANSI_RESET + "] " + message);
    }
    public void warn(String message) {
        System.out.println("[" + timestamp + color.ANSI_YELLOW + "WARN" + color.ANSI_RESET + "] " + message);
    }
    public void error(String message) {
        System.out.println("[" + timestamp + color.ANSI_RED + "ERROR" + color.ANSI_RESET + "] " + message);
    }
    public void criticalError(String message) {
        System.out.println( "[" + timestamp + color.ANSI_RED + "!!!ALERT!!!" + color.ANSI_RESET + "] " + message);
    }
}
