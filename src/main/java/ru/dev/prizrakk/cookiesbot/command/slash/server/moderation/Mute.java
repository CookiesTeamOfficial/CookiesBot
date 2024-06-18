package ru.dev.prizrakk.cookiesbot.command.slash.server.moderation;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.dev.prizrakk.cookiesbot.command.CommandCategory;
import ru.dev.prizrakk.cookiesbot.command.ICommand;
import ru.dev.prizrakk.cookiesbot.command.CommandStatus;
import ru.dev.prizrakk.cookiesbot.util.Config;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Mute implements ICommand {
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
    public CommandStatus getStatus() {
        return CommandStatus.OK;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws SQLException {
        Member member = event.getOption("user").getAsMember();
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR) || !event.getMember().hasPermission(Permission.BAN_MEMBERS) || !event.getMember().hasPermission(Permission.KICK_MEMBERS)) {
            event.reply("У вас не достаточно прав для совершения данного действия!").queue();
            return;
        }
        if (member == null) {
            event.reply("Не удалось найти пользователя.").setEphemeral(true).queue();
            return;
        }

        String time = event.getOption("duration").getAsString();
        if (time == null || time.isEmpty()) {
            event.reply("Необходимо указать длительность.").setEphemeral(true).queue();
            return;
        }

        long milliseconds = parseDurationToMilliseconds(time);
        if (milliseconds <= 0) {
            event.reply("Некорректное время.").setEphemeral(true).queue();
            return;
        }

        String reason = event.getOption("reason").getAsString();
        if (reason == null || reason.isEmpty()) {
            event.reply("Необходимо указать причину.").setEphemeral(true).queue();
            return;
        }

        Config config = new Config();

        try {
            member.timeoutFor(milliseconds, TimeUnit.MILLISECONDS).reason(reason).queue();
        } catch (Exception e) {
            event.reply("Не удалось замутить пользователя: " + e.getMessage()).setEphemeral(true).queue();
            return;
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Пользователь замучен!");
        embed.addField("Нарушитель", member.getAsMention(), true);
        embed.addField("Время наказания", time, true);
        embed.addField("Причина", reason, true);
        embed.setFooter(config.years_author);
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
                        System.err.println("Invalid time unit: " + unit);
                }
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.err.println("Error parsing time: " + e.getMessage());
            return -1;
        }

        return milliseconds;
    }

    public void sendAudit(SlashCommandInteractionEvent event) {
        Member member = event.getOption("user").getAsMember();
        if (member == null) return;

        Config config = new Config();
        String logChannelId = "1209231369243332738";
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Пользователь замучен!");
        embed.addField("Нарушитель", member.getAsMention(), true);
        embed.addField("Время наказания", event.getOption("duration").getAsString(), true);
        embed.addField("Причина", event.getOption("reason").getAsString(), true);
        embed.addField("Модератор", event.getMember().getAsMention(), true);
        embed.setFooter(config.years_author);

        event.getGuild().getTextChannelById(logChannelId).sendMessageEmbeds(embed.build()).queue();
    }
}