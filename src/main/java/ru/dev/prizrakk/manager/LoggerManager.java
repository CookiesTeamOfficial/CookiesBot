
package ru.dev.prizrakk.manager;

import java.util.Date;
import java.text.SimpleDateFormat;

public class LoggerManager
{
    ColorManager color;
    
    public LoggerManager() {
        this.color = new ColorManager();
    }
    
    public void info(String message) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss ");
        String timestamp = dateFormat.format(new Date());
        String logLine = "[" + timestamp + color.ANSI_GREEN + "INFO" + color.ANSI_RESET + "] " + message;
        System.out.println(logLine);
    }
    
    public void error(String message) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss ");
        String timestamp = dateFormat.format(new Date());
        String logLine = "[" + timestamp + color.ANSI_RED + "ERROR" + color.ANSI_RESET + "] " + message;
        System.out.println(logLine);
    }
    
    public void warn(String message) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss ");
        String timestamp = dateFormat.format(new Date());
        String logLine = "[" + timestamp + color.ANSI_YELLOW + "WARN" + color.ANSI_RESET + "] " + message;
        System.out.println(logLine);
    }
    
    public void command(String event, String message) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss ");
        String timestamp = dateFormat.format(new Date());
        String logLine = "[" + timestamp + color.ANSI_CYAN + event + color.ANSI_RESET + "] " + message;
        System.out.println(logLine);
    }
}
