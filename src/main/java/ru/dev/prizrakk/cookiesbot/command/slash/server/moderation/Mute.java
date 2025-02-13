package ru.dev.prizrakk.cookiesbot.command.slash.server.moderation;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
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

public class Mute extends Utils implements ICommand {
    @Override
    public String getName() {
        return "mute";
    }

    @Override
    public String getDescription() {
        return "Мутит пользователя";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.USER, "user", "Позволяет узнать инфу пользователя", true));
        options.add(new OptionData(OptionType.STRING, "duration", "подсказка: 1w - 1 неделя | 1d - 1 день | 1h - 1 час| 1m - 1 мин.  1s - 1 сек.", true));
        options.add(new OptionData(OptionType.STRING, "reason", "причина", true));
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
        if (event.getChannelType() != ChannelType.TEXT) {
            event.reply(getLangMessage(event.getMember().getUser(),event.getGuild(), "command.doNotSendPrivateMessagesToTheBot")).setEphemeral(true).queue();
            return;
        }
        Member member = event.getOption("user").getAsMember();
        if (member == null) {
            event.reply(getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.mute.message.notFoundMember")).setEphemeral(true).queue();
            return;
        }

        String time = event.getOption("duration").getAsString();
        if (time == null || time.isEmpty()) {
            event.reply(getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.mute.message.incorrectDuration")).setEphemeral(true).queue();
            return;
        }

        long milliseconds = parseDurationToMilliseconds(time);
        if (milliseconds <= 0) {
            event.reply(getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.mute.message.incorrectTime")).setEphemeral(true).queue();
            return;
        }

        String reason = event.getOption("reason").getAsString();
        if (reason == null || reason.isEmpty()) {
            event.reply(getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.mute.message.incorrectReason")).setEphemeral(true).queue();
            return;
        }

        try {
            member.timeoutFor(milliseconds, TimeUnit.MILLISECONDS).reason(reason).queue();
        } catch (Exception e) {
            event.reply(getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.mute.message.errorMuteUser").replace("%errorLog%", e.getMessage())).setEphemeral(true).queue();
            return;
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.mute.successfulMute.title"));
        embed.addField(getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.mute.successfulMute.field.breaker"), member.getAsMention(), true);
        embed.addField(getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.mute.successfulMute.field.timePunishment"), time, true);
        embed.addField(getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.mute.successfulMute.field.reason"), reason, true);
        //embed.setFooter(config.years_author);
        //sendAudit(event);
        event.replyEmbeds(embed.build()).setEphemeral(true).queue();
    }

    private long parseDurationToMilliseconds(String time) {
        long milliseconds = 0;
        try {
            String[] parts = time.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");

            for (int i = 0; i < parts.length; i += 2) {
                long value = Long.parseLong(parts[i]);
                String unit = parts[i + 1].toLowerCase();

                switch (unit) {
                    case "w":
                        milliseconds += value * 7 * 24 * 60 * 60 * 1000;
                        break;
                    case "d":
                        milliseconds += value * 24 * 60 * 60 * 1000;
                        break;
                    case "h":
                        milliseconds += value * 60 * 60 * 1000;
                        break;
                    case "m":
                        milliseconds += value * 60 * 1000;
                        break;
                    case "s":
                        milliseconds += value * 1000;
                        break;
                    default:
                        getLogger().error("Invalid time unit: " + unit);
                }
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            getLogger().error("Error parsing time:", e);
            return -1;
        }

        return milliseconds;
    }
}