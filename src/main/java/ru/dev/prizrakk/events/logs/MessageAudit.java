package ru.dev.prizrakk.events.logs;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MessageAudit extends ListenerAdapter {
    private final String logChannelId = "1209231314373447792";
    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        // Получаем информацию о сообщении
        String deletedBy = event.getGuild().retrieveAuditLogs().type(net.dv8tion.jda.api.audit.ActionType.MESSAGE_DELETE)
                .complete().get(0).getUser().getAsMention();
        String channelId = event.getChannel().getId();
        String messageId = event.getMessageId();
        String deletionTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

        // Создаем Embed для сообщения
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Сообщение было удалено");
        embed.setColor(java.awt.Color.RED);
        embed.addField("Автор сообщения", deletedBy, false);
        embed.addField("Контекст", "Error 404: Not Found" , true);
        embed.addField("ID канала", channelId, false);
        embed.addField("ID сообщения", messageId, false);
        embed.addField("Время удаления", deletionTime, false);

        // Отправляем Embed в отдельный канал
        TextChannel logChannel = event.getGuild().getTextChannelById(logChannelId);
        if (logChannel != null) {
            logChannel.sendMessageEmbeds(embed.build()).queue();
        } else {
            System.out.println("Ошибка: Не удалось найти канал с ID " + logChannelId);
        }
    }

    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {
        // Ваш код для обработки события редактирования сообщения
        System.out.println("Сообщение было отредактировано: " + event.getMessageId());
    }
}
