package ru.dev.prizrakk.cookiesbot.command.slash.user;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.dev.prizrakk.cookiesbot.command.CommandCategory;
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
    public List<Permission> getRequiredPermissions() {
        return List.of(Permission.MESSAGE_SEND);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws SQLException {
        if (event.getChannelType() != ChannelType.TEXT) {
            event.reply(getLangMessage(event.getMember().getUser(),event.getGuild(), "command.doNotSendPrivateMessagesToTheBot")).setEphemeral(true).queue();
            return;
        }
        User user;
        Member member;
        String status = null;
        if (event.getOption("user") != null) {
            user = event.getOption("user").getAsUser();
            member = event.getOption("user").getAsMember();
        } else {
            user = event.getUser();
            member = event.getMember();
        }

        switch (member.getOnlineStatus()) {
            case UNKNOWN -> status = getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.userInfo.unknown");
            case IDLE -> status = getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.userInfo.idle");
            case ONLINE -> status = getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.userInfo.online");
            case OFFLINE -> status = getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.userInfo.offline");
            case INVISIBLE -> status = getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.userInfo.invisible");
            case DO_NOT_DISTURB -> status = getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.userInfo.dnd");
        }
        OffsetDateTime createTime = user.getTimeCreated();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedCreateTime = createTime.format(formatter);
        OffsetDateTime createTimeG = member.getTimeJoined();
        String formattedCreateTimeG = createTimeG.format(formatter);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.userInfo.embed.title"));
        embed.setThumbnail(user.getAvatarUrl());
        embed.setDescription(getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.userInfo.embed.description"));
        embed.addField("Основная информация",getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.userInfo.embed.field.description.userInfo")
                .replace("%userGlobal%", user.getGlobalName()).replace("%user%", user.getName())
                .replace("%formattedCreateTime%", formattedCreateTime)
                .replace("%formattedCreateTimeGuild%", formattedCreateTimeG)
                .replace("%status%", status), false);
        embed.addField(getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.userInfo.embed.field.title.rep"), getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.userInfo.embed.field.description.rep"), true);
        embed.addField(getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.userInfo.embed.field.title.level"), getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.userInfo.embed.field.description.level").replace("%level%",getUserLevel(event.getUser(), event.getGuild()).getLevel() + "") + "", true);
        embed.addField(getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.userInfo.embed.field.title.experience"), getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.userInfo.embed.field.description.experience").replace("%experience%", getUserLevel(event.getUser(), event.getGuild()).getExp() + "") + "", true);
        event.replyEmbeds(embed.build()).queue();
    }

}
