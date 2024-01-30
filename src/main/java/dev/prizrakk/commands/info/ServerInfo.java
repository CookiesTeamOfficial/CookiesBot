package dev.prizrakk.commands.info;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import dev.prizrakk.ICommand;

import java.awt.*;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ServerInfo implements ICommand {
    @Override
    public String getName() {
        return "serverinfo";
    }

    @Override
    public String getDescription() {
        return "Показывает информацию о боте";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        OffsetDateTime createTime = event.getGuild().getTimeCreated();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedCreateTime = createTime.format(formatter);

        String boostTier;
        if (event.getGuild().getBoostTier().toString() == "NONE") {
            boostTier = "Отсутсвует";
        } else {
            boostTier = event.getGuild().getBoostTier().toString();
        }
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(0, 0, 0));
        embed.setTitle("Информация");
        embed.setThumbnail(event.getGuild().getIconUrl());
        embed.addField("Основное", "Название: " + "`" + event.getGuild().getName() + "`"
                + "\n" + "Дата создания: " + "`" + formattedCreateTime + "`"
                + "\n" + "Создатель: " + event.getGuild().getOwner().getAsMention()
                + "\n" + "Количество бустов: " + "`" + event.getGuild().getBoostCount() + "`"
                + "\n" + "Уровень бустов:" + "`" + boostTier + "`"
                + "\n" + "Регион: " + "`" + event.getGuildLocale().getLanguageName() + "`", true);
        embed.addField("Участники", "Всего: " + "`" + event.getGuild().getMemberCount() + "`"
                + "\n" + "Ботов: " + "`" + "unkonwn"+ "`", true);
        event.replyEmbeds(embed.build()).queue();
    }
}
