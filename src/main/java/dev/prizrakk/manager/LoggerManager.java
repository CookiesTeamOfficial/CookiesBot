
package dev.prizrakk.manager;

import java.util.Date;
import java.text.SimpleDateFormat;

public class LoggerManager
{
    ColorManager color;
    
    public LoggerManager() {
        color = new ColorManager();
    }
    public void log (String level, String message) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss ");
        String timestamp = dateFormat.format(new Date());
        switch (level) {
            case "info" :
                String info = "[" + timestamp + "] "+ color.ANSI_GREEN + "INFO " + color.ANSI_RESET  + message;
                System.out.println(info);
                break;
            case "error" :
                String error = "[" + timestamp + "] " + color.ANSI_RED + "ERROR " + color.ANSI_RESET  + message;
                System.out.println(error);
                break;
            case "warn" :
                String warn = "[" + timestamp + "] " + color.ANSI_YELLOW + "WARN " + color.ANSI_RESET  + message;
                System.out.println(warn);
                break;
        }
    }
    public void command( String event,  String message) {
         SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss ");
         String timestamp = dateFormat.format(new Date());
         String logLine = "[" + timestamp + "] " + color.ANSI_CYAN + event + " " + color.ANSI_RESET  + message;
        System.out.println(logLine);
    }
}
