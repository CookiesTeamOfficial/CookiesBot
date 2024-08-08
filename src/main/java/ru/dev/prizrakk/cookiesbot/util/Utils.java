package ru.dev.prizrakk.cookiesbot.util;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.dev.prizrakk.cookiesbot.database.Database;
import ru.dev.prizrakk.cookiesbot.database.DatabaseUtils;
import ru.dev.prizrakk.cookiesbot.database.ExpVariable;
import ru.dev.prizrakk.cookiesbot.database.GuildVariable;
import ru.dev.prizrakk.cookiesbot.manager.LangManager;
import ru.dev.prizrakk.cookiesbot.manager.LoggerManager;

import java.sql.SQLException;

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
    public static GuildVariable getGuildOnSlash(Guild guild) {
        GuildVariable guildVariable;
        try {
            guildVariable = databaseUtils.getGuildFromDatabase(guild);
            return guildVariable;
        } catch (SQLException e) {
            getLogger().error("", e);
            return null;
        }
    }
    public static GuildVariable getGuildOnPrefix(MessageReceivedEvent event) {
        GuildVariable guildVariable;
        try {
            guildVariable = databaseUtils.getGuildFromDatabase(event.getGuild());
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
    public static String getLangMessage(Guild guild, String path) {
        return LangManager.getMessage(getGuildOnSlash(guild).getLang(), path);
    }

}
