package ru.dev.prizrakk.cookiesbot.database;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.sql.SQLException;

public class DatabaseUtils {
    private final Database database;
    public DatabaseUtils(Database database) {
        this.database = database;
    }
    public ExpVariable getPlayerStatsFromDatabase(String userID, String guildID) throws SQLException {
        ExpVariable expVariable = database.findExpStatsByNICK(userID, guildID);
        if (expVariable == null) {
            expVariable = new ExpVariable(userID, guildID, 0, 175, 0);
            database.createUserExpStats(expVariable);
        }
        return expVariable;
    }
    public GuildVariable getGuildFromDatabase(Guild guild) throws SQLException {
        GuildVariable guildVariable = database.findGuild(guild.getId());
        if(guildVariable == null) {
            guildVariable = new GuildVariable(guild.getId(), "false", guild.getOwnerId(), "English", null, null, null, "0", 0);
            database.createGuildStats(guildVariable);
        }
        return guildVariable;
    }
    public UserVariable getUserFromDatabase(User user) throws SQLException {
        UserVariable userVariable = database.findUserStats(user.getId());
        if(userVariable == null) {
            userVariable = new UserVariable(user.getId(), "none", "", "Russia", 0,0, 0);
            database.createUserStats(userVariable);
        }
        return userVariable;
    }
}
