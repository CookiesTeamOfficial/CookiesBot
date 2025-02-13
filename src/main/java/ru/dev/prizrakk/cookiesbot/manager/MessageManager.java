package ru.dev.prizrakk.cookiesbot.manager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import ru.dev.prizrakk.cookiesbot.database.Database;
import ru.dev.prizrakk.cookiesbot.database.DatabaseUtils;
import ru.dev.prizrakk.cookiesbot.database.ExpVariable;
import ru.dev.prizrakk.cookiesbot.database.GuildVariable;
import ru.dev.prizrakk.cookiesbot.util.Utils;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Random;

import static ru.dev.prizrakk.cookiesbot.util.Utils.getLogger;

public class MessageManager extends ListenerAdapter {
    private Database database;
    private DatabaseUtils databaseUtils;

    public MessageManager(Database database) {
        this.database = database;
        this.databaseUtils = new DatabaseUtils(database);
    }
    ExpVariable expVariable;
    GuildVariable guildVariable;
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!(event.getMember().getUser().isBot())) {
            try {
                expVariable = databaseUtils.getPlayerStatsFromDatabase(event.getMember().getId(), event.getGuild().getId());
                guildVariable = databaseUtils.getGuildFromDatabase(event.getGuild());
                Random r = new Random();
                int exp = r.nextInt(3);

                expVariable.setExp(expVariable.getExp() + exp);
                if (expVariable.getExp() >= expVariable.getMaxExp()) {
                    expVariable.setExp(0);
                    expVariable.setLevel(expVariable.getLevel() + 1);
                    expVariable.setMaxExp(expVariable.getMaxExp() + 25);
                    User user = event.getMember().getUser();
                    user.openPrivateChannel().queue(privateChannel -> {
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setTitle(LangManager.getMessage(guildVariable.getLang(), "event.level.up.embed.title"));
                        embed.setThumbnail(event.getGuild().getIconUrl());
                        embed.setDescription(Utils.getLangMessage(event.getMember().getUser(),event.getGuild(), "event.level.up.embed.description")
                                .replace("%level%",expVariable.getLevel() + "")
                                .replace("%serverName%",event.getGuild().getName()));
                        privateChannel.sendMessageEmbeds(embed.build()).queue();
                    });
                }
                database.updateUserExpStats(expVariable);

            } catch (SQLException e) {
                getLogger().error("", e);
            }
        }
    }
}
