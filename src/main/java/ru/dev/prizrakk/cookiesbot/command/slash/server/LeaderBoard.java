package ru.dev.prizrakk.cookiesbot.command.slash.server;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.dev.prizrakk.cookiesbot.command.CommandCategory;
import ru.dev.prizrakk.cookiesbot.command.ICommand;
import ru.dev.prizrakk.cookiesbot.command.CommandStatus;
import ru.dev.prizrakk.cookiesbot.database.Database;
import ru.dev.prizrakk.cookiesbot.database.DatabaseUtils;
import ru.dev.prizrakk.cookiesbot.database.ExpVariable;
import ru.dev.prizrakk.cookiesbot.util.Utils;

import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeaderBoard extends Utils implements ICommand {
    private static final int USERS_PER_PAGE = 10;

    private DatabaseUtils databaseUtils;

    public LeaderBoard(Database database) {
        this.databaseUtils = new DatabaseUtils(database);
    }

    @Override
    public String getName() {
        return "leaderboard";
    }

    @Override
    public String getDescription() {
        return "Отображает топ пользователей по уровню";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.SERVER;
    }

    @Override
    public CommandStatus getStatus() {
        return CommandStatus.OK;
    }

    @Override
    public List<Permission> getRequiredPermissions() {
        return List.of(Permission.MESSAGE_SEND);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws SQLException {
        if (event.getChannelType() != ChannelType.TEXT) {
            event.reply(getLangMessage(event.getGuild(), "command.doNotSendPrivateMessagesToTheBot")).setEphemeral(true).queue();
            return;
        }
        String guildId = event.getGuild().getId();
        List<ExpVariable> allStats = new ArrayList<>();

        // Получение статистики всех пользователей гильдии
        for (String memberId : getAllGuildMembers(event)) {
            allStats.add(databaseUtils.getPlayerStatsFromDatabase(memberId, guildId));
        }

        // Сортировка списка лидерборда сначала по уровню, затем по опыту
        Collections.sort(allStats, (a, b) -> {
            int levelComparison = Integer.compare(b.getLevel(), a.getLevel());
            if (levelComparison != 0) {
                return levelComparison;
            } else {
                return Integer.compare(b.getExp(), a.getExp());
            }
        });

        // Поиск ранга текущего пользователя
        int userRank = -1;
        for (int i = 0; i < allStats.size(); i++) {
            if (allStats.get(i).getUserID().equals(event.getMember().getId())) {
                userRank = i + 1;
                break;
            }
        }

        // Отображение первой страницы
        displayLeaderboardPage(event, allStats, 0, userRank);
    }

    private List<String> getAllGuildMembers(SlashCommandInteractionEvent event) {
        // Этот метод должен возвращать список всех ID участников гильдии
        // Временная реализация
        List<String> memberIds = new ArrayList<>();
        event.getGuild().getMembers().forEach(member -> memberIds.add(member.getId()));
        return memberIds;
    }

    //TODO: fix broken button
    private void displayLeaderboardPage(SlashCommandInteractionEvent event, List<ExpVariable> allStats, int pageIndex, int userRank) {
        int startIndex = pageIndex * USERS_PER_PAGE;
        int endIndex = Math.min(startIndex + USERS_PER_PAGE, allStats.size());

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(getLangMessage(event.getGuild(), "command.slash.leaderboard.embed.title.message"));
        embed.setColor(Color.ORANGE);
        embed.setDescription(getLangMessage(event.getGuild(), "command.slash.leaderboard.embed.description.message").replace("%userRank%", userRank + ""));

        for (int i = startIndex; i < endIndex; i++) {
            ExpVariable stats = allStats.get(i);
            Member member = event.getGuild().getMemberById(stats.getUserID());
            if (!member.getUser().isBot()) {
                embed.addField((i + 1) + ". " + member.getEffectiveName(),
                        getLangMessage(event.getGuild(), "command.slash.leaderboard.embed.field.message")
                                .replace("%userLevel%", stats.getLevel() + "")
                                .replace("%userExp%", stats.getExp() + ""),false);
            }
        }

        List<Button> buttons = new ArrayList<>();
        if (pageIndex > 0) {
            buttons.add(Button.primary("leaderboard_prev_" + (pageIndex - 1), getLangMessage(event.getGuild(), "command.slash.leaderboard.embed.button.prev.message")));
        }
        buttons.add(Button.danger("leaderboard_delete", getLangMessage(event.getGuild(), "command.slash.leaderboard.embed.button.delete.message")));
        if (endIndex < allStats.size()) {
            buttons.add(Button.primary("leaderboard_next_" + (pageIndex + 1), getLangMessage(event.getGuild(), "command.slash.leaderboard.embed.button.next.message")));
        }

        event.replyEmbeds(embed.build()).addActionRow(buttons).queue();
    }


}
