package ru.dev.prizrakk.cookiesbot.command.prefix.system;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ru.dev.prizrakk.cookiesbot.command.CommandCategory;
import ru.dev.prizrakk.cookiesbot.command.CommandManager;
import ru.dev.prizrakk.cookiesbot.command.ICommand;
import ru.dev.prizrakk.cookiesbot.command.slash.CommandStatus;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Diagnostics extends ListenerAdapter {
    private List<ICommand> commands = CommandManager.commands;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String[] message = event.getMessage().getContentRaw().split(" ", 2);
        if (message[0].startsWith("!")) { // Замените "!" на ваш префикс
            String command = message[0].substring(1).toLowerCase(); // Убираем префикс и приводим к нижнему регистру
            String[] args = message.length > 1 ? message[1].split(" ") : new String[0];
            switch (command) {
                case "botstats":
                    handleBotStats(event);
                    break;
            }
        }
    }

    private void handleBotStats(MessageReceivedEvent event) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Статусы команд")
                .setColor(Color.BLUE);

        boolean allCommandsOK = true; // Флаг для проверки всех команд в категории
        boolean allCategoriesEmpty = true; // Флаг для проверки, есть ли команды в категории

        for (CommandCategory category : CommandCategory.values()) {
            List<ICommand> commandsInCategory = getCommandsInCategory(category);

            // Если в категории есть команды
            if (!commandsInCategory.isEmpty()) {
                allCategoriesEmpty = false; // Категория не пустая

                boolean categoryOK = true; // Флаг для проверки статуса всех команд в категории
                List<String> errorCommands = new ArrayList<>(); // Список команд с ошибками

                // Проверяем статусы команд в категории
                for (ICommand command : commandsInCategory) {
                    CommandStatus status = command.getStatus();
                    if (status != CommandStatus.OK) {
                        categoryOK = false;
                        errorCommands.add(command.getName());
                    }
                }

                // Определяем цвет для категории
                Color categoryColor;
                if (categoryOK) {
                    categoryColor = Color.GREEN; // Все команды в категории OK
                } else if (!errorCommands.isEmpty()) {
                    categoryColor = Color.ORANGE; // Часть команд в категории с ошибками
                } else {
                    categoryColor = Color.RED; // Нет команд в категории
                }

                // Добавляем информацию о категории в Embed
                embed.addField(category.toString(), "Статус: " + (categoryOK ? "OK" : "ERROR"), false);

                // Добавляем список команд с ошибками
                if (!errorCommands.isEmpty()) {
                    embed.addField("Команды с ошибками", String.join(", ", errorCommands), false);
                }
            }
        }

        // Если все категории пустые
        if (allCategoriesEmpty) {
            embed.setColor(Color.GRAY);
            embed.setDescription("Нет команд в категориях");
        }

        // Отправляем Embed
        event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }

    private List<ICommand> getCommandsInCategory(CommandCategory category) {
        List<ICommand> commandsInCategory = new ArrayList<>();
        for (ICommand command : commands) {
            if (command.getCategory() == category) {
                commandsInCategory.add(command);
            }
        }
        return commandsInCategory;
    }
}
