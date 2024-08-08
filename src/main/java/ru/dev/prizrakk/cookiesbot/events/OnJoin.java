package ru.dev.prizrakk.cookiesbot.events;

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ru.dev.prizrakk.cookiesbot.database.Database;
import ru.dev.prizrakk.cookiesbot.database.DatabaseUtils;
import ru.dev.prizrakk.cookiesbot.database.ExpVariable;

import java.sql.SQLException;

import static ru.dev.prizrakk.cookiesbot.util.Utils.getLogger;

public class OnJoin extends ListenerAdapter {
    Database database;
    DatabaseUtils databaseUtils = new DatabaseUtils(database);
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        ExpVariable expVariable;
        try {
            expVariable = databaseUtils.getPlayerStatsFromDatabase(event.getMember().getId(), event.getGuild().getId());
            expVariable.setExp(expVariable.getExp() + 1);
            database.updateUserExpStats(expVariable);
        } catch (SQLException e) {
            getLogger().error("", e);
        }
    }
}
