package ru.dev.prizrakk.cookiesbot.command.slash.server.moderation;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.dev.prizrakk.cookiesbot.command.CommandCategory;
import ru.dev.prizrakk.cookiesbot.command.ICommand;
import ru.dev.prizrakk.cookiesbot.util.Utils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UnMute extends Utils implements ICommand {
    @Override
    public String getName() {
        return "unmute";
    }

    @Override
    public String getDescription() {
        return "Размучивает пользователя";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.USER, "user", "Позволяет узнать инфу пользователя", true));
        return options;
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.ADMINISTRATION;
    }

    @Override
    public List<Permission> getRequiredPermissions() {
        return List.of(Permission.MESSAGE_SEND, Permission.KICK_MEMBERS);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws SQLException {
        Member member = event.getOption("user").getAsMember();
        if (member == null) {
            event.reply(getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.unmute.message.notFoundMember")).setEphemeral(true).queue();
            return;
        }
        try {
            member.timeoutFor(0, TimeUnit.MILLISECONDS).queue();
        } catch (Exception e) {
            event.reply(getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.unmute.message.errorUnMuteUser").replace("%errorLog%", e.getMessage())).setEphemeral(true).queue();
            return;
        }
        event.reply(getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.unmute.message.successfulUnMute")).setEphemeral(true).queue();
    }
}
