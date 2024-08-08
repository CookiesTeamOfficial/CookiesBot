package ru.dev.prizrakk.cookiesbot.command.slash.user;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.dev.prizrakk.cookiesbot.command.CommandCategory;
import ru.dev.prizrakk.cookiesbot.command.CommandStatus;
import ru.dev.prizrakk.cookiesbot.command.ICommand;
import ru.dev.prizrakk.cookiesbot.util.Utils;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class UserInfo extends Utils implements ICommand {
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

    @Override
    public void execute(SlashCommandInteractionEvent event) throws SQLException {
        if (event.getChannelType() != ChannelType.TEXT) {
            event.reply(getLangMessage(event.getGuild(), "command.doNotSendPrivateMessagesToTheBot")).setEphemeral(true).queue();
            return;
        }
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
            case UNKNOWN -> status = getLangMessage(event.getGuild(), "command.slash.userInfo.unknown.message");
            case IDLE -> status = getLangMessage(event.getGuild(), "command.slash.userInfo.idle.message");
            case ONLINE -> status = getLangMessage(event.getGuild(), "command.slash.userInfo.online.message");
            case OFFLINE -> status = getLangMessage(event.getGuild(), "command.slash.userInfo.offline.message");
            case INVISIBLE -> status = getLangMessage(event.getGuild(), "command.slash.userInfo.invisible.message");
            case DO_NOT_DISTURB -> status = getLangMessage(event.getGuild(), "command.slash.userInfo.dnd.message");
        }
        OffsetDateTime createTime = user.getTimeCreated();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedCreateTime = createTime.format(formatter);
        OffsetDateTime createTimeG = member.getTimeJoined();
        String formattedCreateTimeG = createTimeG.format(formatter);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(getLangMessage(event.getGuild(), "command.slash.userInfo.embed.title.message"));
        embed.setThumbnail(user.getAvatarUrl());
        embed.setDescription(getLangMessage(event.getGuild(), "command.slash.userInfo.embed.description.message"));
        embed.addField("Основная информация",getLangMessage(event.getGuild(), "command.slash.userInfo.embed.field.description.userInfo.message")
                .replace("%userGlobal%", user.getGlobalName()).replace("%user%", user.getName())
                .replace("%formattedCreateTime%", formattedCreateTime)
                .replace("%formattedCreateTimeGuild%", formattedCreateTimeG)
                .replace("%status%", status), false);
        embed.addField(getLangMessage(event.getGuild(), "command.slash.userInfo.embed.field.title.rep.message"), getLangMessage(event.getGuild(), "command.slash.userInfo.embed.field.description.rep.message"), true);
        embed.addField(getLangMessage(event.getGuild(), "command.slash.userInfo.embed.field.title.level.message"), getLangMessage(event.getGuild(), "command.slash.userInfo.embed.field.description.level.message").replace("%level%",getUserLevel(event.getUser(), event.getGuild()).getLevel() + "") + "", true);
        embed.addField(getLangMessage(event.getGuild(), "command.slash.userInfo.embed.field.title.experience.message"), getLangMessage(event.getGuild(), "command.slash.userInfo.embed.field.description.experience.message").replace("%experience%", getUserLevel(event.getUser(), event.getGuild()).getExp() + "") + "", true);
        event.replyEmbeds(embed.build()).queue();
    }

}
