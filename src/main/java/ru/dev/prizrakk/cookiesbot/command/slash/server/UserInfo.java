package ru.dev.prizrakk.cookiesbot.command.slash.server;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.dev.prizrakk.cookiesbot.command.CommandCategory;
import ru.dev.prizrakk.cookiesbot.command.CommandStatus;
import ru.dev.prizrakk.cookiesbot.util.Config;
import ru.dev.prizrakk.cookiesbot.command.ICommand;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class UserInfo implements ICommand {
    @Override
    public String getName() {
        return "userinfo";
    }

    @Override
    public String getDescription() {
        return "Показывает информацию о пользователе";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.USER, "user", "Позволяет узнать инфу пользователя", false));
        return options;
    }
    @Override
    public CommandCategory getCategory() {
        return CommandCategory.USER;
    }

    @Override
    public CommandStatus getStatus() {
        return CommandStatus.OK;
    }

    Config config = new Config();
    @Override
    public void execute(SlashCommandInteractionEvent event) throws SQLException {
        User user = null;
        Member member = null;
        String status = null;
        if (event.getOption("user") != null) {
            user = event.getOption("user").getAsUser();
            member = event.getOption("user").getAsMember();
        } else {
            user = event.getUser();
            member = event.getMember();
        }

        switch (member.getOnlineStatus()) {
            case UNKNOWN:
                status = "<:Offline:1209207265165316187> Не известно";
                break;
            case IDLE:
                status = "<:Idle:1209207266620866620> Не активен";
                break;
            case ONLINE:
                status = "<:Online:1209207268281942047> Онлайн";
                break;
            case OFFLINE:
                status = "<:Offline:1209207265165316187> Оффлайн";
                break;
            case INVISIBLE:
                status = "<:Offline:1209207265165316187> Неведимый";
                break;
            case DO_NOT_DISTURB:
                status = "<:Dnd:1209207263802171402> Занят";
                break;
        }
        OffsetDateTime createTime = user.getTimeCreated();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedCreateTime = createTime.format(formatter);
        OffsetDateTime createTimeG = member.getTimeJoined();
        String formattedCreateTimeG = createTimeG.format(formatter);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Информация о пользователе");
        embed.setThumbnail(user.getAvatarUrl());
        embed.setDescription("Вы можете добавить свой статус кастомный!");
        embed.addField("Основная информация","**Имя:** " + user.getGlobalName() + " (" + user.getName() + ")"
                + "\n" + "**Дата создания:** " + formattedCreateTime
                + "\n" + "**Присоединился:** " + formattedCreateTimeG
                + "\n" + "**Статус:** " + status
                + "\n" + "**Имя пользователя:** " + user.getGlobalName()
                + "\n" + "", false);
        embed.addField("Репутация", "0", true);
        embed.addField("Уровень", "0", true);
        embed.addField("Опыт", "0", true);
        embed.setFooter(config.years_author);
        event.replyEmbeds(embed.build()).queue();
    }

}
