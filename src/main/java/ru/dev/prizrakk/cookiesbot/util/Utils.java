package ru.dev.prizrakk.cookiesbot.util;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.dev.prizrakk.cookiesbot.database.*;
import ru.dev.prizrakk.cookiesbot.manager.ConfigManager;
import ru.dev.prizrakk.cookiesbot.manager.LangManager;
import ru.dev.prizrakk.cookiesbot.manager.LoggerManager;

import java.sql.SQLException;
import java.util.Objects;

public class Utils {
    private static DatabaseUtils databaseUtils;

    public Utils(Database database) {
        databaseUtils = new DatabaseUtils(database);
    }
    public Utils() {
    }
    public static LoggerManager getLogger() {
        return new LoggerManager();
    }
    public static GuildVariable getGuildInDatabase(Guild guild) {
        GuildVariable guildVariable;
        try {
            guildVariable = databaseUtils.getGuildFromDatabase(guild);
            return guildVariable;
        } catch (SQLException e) {
            getLogger().error("", e);
            return null;
        }
    }
    public static ExpVariable getUserLevel(User user, Guild guild) {
        ExpVariable expVariable;
        try {
            expVariable = databaseUtils.getPlayerStatsFromDatabase(user.getId(), guild.getId());
            return expVariable;
        } catch (SQLException e) {
            getLogger().error("", e);
            return null;
        }
    }
    public static UserVariable getUserInDatabase(User user) {
        UserVariable userVariable;
        try {
            userVariable = databaseUtils.getUserFromDatabase(user);
            return userVariable;
        } catch (SQLException e) {
            getLogger().error("", e);
            return null;
        }
    }
    public static String getLangMessage(User user, Guild guild, String path) {
        // 1. Получаем язык пользователя, если есть
        String userLang = (user != null) ? LangManager.getMessage(getUserInDatabase(user).getLang(), path) : null;
        // Если у пользователя есть локализация и строка найдена — используем её
        if (userLang != null && !userLang.isEmpty() && !(userLang.equals("null"))) {
            return userLang;
        }

        // 2. Если пользователь в гильдии, используем локализацию гильдии
        if (guild != null) {
            String guildLang = LangManager.getMessage(Objects.requireNonNull(getGuildInDatabase(guild)).getLang(), path);
            if (guildLang != null && !guildLang.isEmpty()) {
                return guildLang;
            } else {
                getLogger().error("Guild error: " + guild.getName() + " error occurred: localization key '" + path + "' not found");
            }
        }

        // 3. Если пользователь в личных сообщениях или язык не найден — используем дефолт
        return LangManager.getMessage("Russia", path); // Дефолтный язык (английский)
    }

    public static ConfigManager getConfig() {
        return new ConfigManager();
    }


}
