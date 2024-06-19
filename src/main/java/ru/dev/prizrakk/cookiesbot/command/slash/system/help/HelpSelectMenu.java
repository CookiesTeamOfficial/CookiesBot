package ru.dev.prizrakk.cookiesbot.command.slash.system.help;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import ru.dev.prizrakk.cookiesbot.command.CommandCategory;
import ru.dev.prizrakk.cookiesbot.command.CommandManager;
import ru.dev.prizrakk.cookiesbot.command.ICommand;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HelpSelectMenu extends ListenerAdapter {
    public List<ICommand> commands = CommandManager.commands; // Список всех команд

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss ");
        String timestamp = dateFormat.format(new Date());
        if (event.getComponentId().equals("helpmenu")) {
            String selectedValue = event.getValues().get(0); // Получение первого выбранного значения

            if (selectedValue.equals("info")) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("Информация о CookiesBot");
                embed.setColor(new Color(255, 104, 0));
                embed.setDescription("CookiesBot был создан в инструмент который может вам помочь всем чем может и часто обновляется имея открытый код");
                embed.addField("Разработчик", "<@579683756789727243>", true);
                embed.addField("Язык Программирования", "`java 8`", true);
                embed.addField("Библиотека Discord", "`JDA 5.0.0-beta.20`", true);
                embed.addField("Версия бота", "`pre-release 0.4-rework`", true);
                embed.setFooter("Команда вызвана: " + timestamp);
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
            } else if (selectedValue.equals("command")) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(new Color(255, 104, 0));
                embed.setTitle("Выберите категорию, которая вам нужна");
                embed.setFooter("Команда вызвана: " + timestamp);
                event.replyEmbeds(embed.build()).setActionRow(
                        StringSelectMenu.create("helpcommand")
                                .addOptions(
                                        SelectOption.of("Серверные", "server")
                                                .withDescription("Покажет все серверные команды")
                                                .withEmoji(Emoji.fromUnicode("🏠")),
                                        SelectOption.of("Администрация", "admin")
                                                .withDescription("Покажет команды доступные администрации")
                                                .withEmoji(Emoji.fromUnicode("🔧")),
                                        SelectOption.of("Развлекательные", "fun")
                                                .withDescription("Развлекательные команды")
                                                .withEmoji(Emoji.fromUnicode("🎉")),
                                        SelectOption.of("Музыка", "music")
                                                .withDescription("Музыкальные команды")
                                                .withEmoji(Emoji.fromUnicode("🎵")),
                                        SelectOption.of("Префиксные команды", "prefix")
                                                .withDescription("Команды с префиксом")
                                                .withEmoji(Emoji.fromUnicode("🔤")),
                                        SelectOption.of("Команды пользователя", "user")
                                                .withDescription("Команды с префиксом")
                                                .withEmoji(Emoji.fromUnicode("🔤")),
                                        SelectOption.of("Прочее", "other")
                                                .withDescription("Остальные команды")
                                                .withEmoji(Emoji.fromUnicode("❓"))
                                ).build()
                ).setEphemeral(true).queue();
            }
        }
        if (event.getComponentId().equals("helpcommand")) {
            String selectedValue = event.getValues().get(0); // Получение первого выбранного значения

            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(new Color(255, 104, 0));
            embed.setTitle("Команды категории: " + selectedValue.toUpperCase());

            CommandCategory category;
            switch (selectedValue) {
                case "server":
                    category = CommandCategory.SERVER;
                    break;
                case "admin":
                    category = CommandCategory.ADMINISTRATION;
                    break;
                case "fun":
                    category = CommandCategory.FUN;
                    break;
                case "music":
                    category = CommandCategory.MUSIC;
                    break;
                case "prefix":
                    category = CommandCategory.PREFIX;
                    break;
                case "other":
                    category = CommandCategory.OTHER;
                    break;
                default:
                    embed.setDescription("Неизвестная категория...");
                    category = null;
                    break;
            }

            if (category != null) {
                for (ICommand command : commands) {
                    if (command.getCategory() == category) {

                        StringBuilder optionsDescription = new StringBuilder();
                        List<OptionData> options = command.getOptions();
                        if (options != null) {
                            for (OptionData option : options) {
                                optionsDescription.append(option.getName())
                                        .append(": ")
                                        .append(option.getDescription())
                                        .append("\n");
                            }
                        }
                        embed.addField("Название","> " + command.getName(), false);
                        embed.addField("Описание","> " + command.getDescription(), true);
                        embed.addField("Опции", "> " + (optionsDescription.length() > 0 ? optionsDescription.toString() : "Нет опций"), true);
                    }
                }
            }
            embed.setFooter("Команда вызвана: " + timestamp);
            event.replyEmbeds(embed.build()).setEphemeral(true).queue();
        }
    }
}
