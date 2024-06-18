package ru.dev.prizrakk.cookiesbot.manager;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import ru.dev.prizrakk.cookiesbot.database.Database;
import ru.dev.prizrakk.cookiesbot.database.DatabaseUtils;
import ru.dev.prizrakk.cookiesbot.database.ExpVariable;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Random;

public class MessageManager extends ListenerAdapter {
    private Database database;
    private DatabaseUtils databaseUtils;

    public MessageManager(Database database) {
        this.database = database;
        this.databaseUtils = new DatabaseUtils(database);
    }
    ExpVariable expVariable;
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!(event.getMember()).getUser().isBot()) {
            try {
                expVariable = databaseUtils.getPlayerStatsFromDatabase(event.getMember().getId(), event.getGuild().getId());
                Random r = new Random();
                int exp = r.nextInt(3);

                expVariable.setExp(expVariable.getExp() + exp);
                if (expVariable.getExp() >= expVariable.getMaxExp()) {
                    expVariable.setExp(0);
                    expVariable.setLevel(expVariable.getLevel() + 1);
                    expVariable.setMaxExp(expVariable.getMaxExp() + 25);
                    User user = event.getMember().getUser();
                    user.openPrivateChannel().queue(privateChannel -> {
                        privateChannel.sendMessage("Поздравляю вы получили " + expVariable.getLevel() + " уровень!").queue();
                    });
                }
                database.updateUserExpStats(expVariable);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
