package ru.dev.prizrakk.cookiesbot.command.slash.server.moderation;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.dev.prizrakk.cookiesbot.command.CommandCategory;
import ru.dev.prizrakk.cookiesbot.command.ICommand;
import ru.dev.prizrakk.cookiesbot.command.slash.CommandStatus;
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
        return CommandStatus.ERROR;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws SQLException {
        Member member = event.getOption("user").getAsMember();
        long milliseconds = 0;
        String time = event.getOption("duration").getAsString();

        // Разделение строки времени на составляющие
        String[] parts = time.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");

        // Преобразование каждой части в миллисекунды и добавление к общему времени
        for (int i = 0; i < parts.length; i += 2) {
            long value = Long.parseLong(parts[i]); // Значение времени
            String unit = parts[i + 1].toLowerCase(); // Единица измерения времени

            switch (unit) {
                case "w": // Недели
                    milliseconds += value * 7 * 24 * 60 * 60 * 1000;
                    break;
                case "d": // Дни
                    milliseconds += value * 24 * 60 * 60 * 1000;
                    break;
                case "h": // Часы
                    milliseconds += value * 60 * 60 * 1000;
                    break;
                case "m": // Минуты
                    milliseconds += value * 60 * 1000;
                    break;
                case "s": // Секунды
                    milliseconds += value * 1000;
                    break;
                default:
                    // Обработка некорректного формата времени
                    System.err.println("Invalid time unit: " + unit);
            }
        }
        Config config = new Config();

        member.timeoutFor(milliseconds, TimeUnit.MILLISECONDS).reason(event.getOption("reason").getAsString()).queue();
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Пользователь замучен!");
        embed.addField("Нарушитель",  member.getAsMention(), true);
        embed.addField("Время наказания", event.getOption("duration").getAsString(), true);
        embed.addField("Причина", event.getOption("reason").getAsString(), true);
        embed.setFooter(config.years_author);
        sendAudit(event);
        event.replyEmbeds(embed.build()).setEphemeral(true).queue();
    }
    public void sendAudit(SlashCommandInteractionEvent event) {
        Member member = event.getOption("user").getAsMember();
        Config config = new Config();
        String logChannelId = "1209231369243332738";
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Пользователь замучен!");
        embed.addField("Нарушитель",  member.getAsMention(), true);
        embed.addField("Время наказания", event.getOption("duration").getAsString(), true);
        embed.addField("Причина", event.getOption("reason").getAsString(), true);
        embed.addField("Модератор", event.getMember().getAsMention(), true);
        embed.setFooter(config.years_author);
        event.getGuild().getTextChannelById(logChannelId).sendMessageEmbeds(embed.build()).queue();
    }
}
