package ru.dev.prizrakk.cookiesbot.util;

import net.dv8tion.jda.api.entities.Guild;
import ru.dev.prizrakk.cookiesbot.manager.ConfigManager;


import java.util.Collection;

public class Values {
    public static ConfigManager config = new ConfigManager();
    public static String connectURL = config.getProperty("jdbc");
    public static String password = config.getProperty("password");
    public static String login = config.getProperty("login");
    public static long guildCount = 0;
    public static double versionBot = 0.2;
    public static long globalUserCount = 0;
    public static Collection<Guild> guilds;
    public static boolean isRepeat = false;

}
