package ru.dev.prizrakk.cookiesbot.manager.console;
import ru.dev.prizrakk.cookiesbot.util.Utils;

import java.util.HashMap;
import java.util.Map;

public class ConsoleManager extends Utils {
    private final Map<String, ConsoleCommand> commands = new HashMap<>();

    /**
     * Регистрирует команду.
     *
     * @param command команда для регистрации
     */
    public void registerCommand(ConsoleCommand command) {
        commands.put(command.getName().toLowerCase(), command);
    }

    /**
     * Выполняет команду на основе ввода пользователя.
     *
     * @param input строка, введённая пользователем
     */
    public void executeCommand(String input) {
        if (input == null || input.isBlank()) {
            return;
        }

        // Разбиваем ввод на команду и аргументы
        String[] parts = input.split("\\s+");
        String commandName = parts[0].toLowerCase();
        String[] args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, args.length);

        // Ищем команду по имени
        ConsoleCommand command = commands.get(commandName);
        if (command != null) {
            try {
                command.execute(args);
            } catch (Exception e) {
                getLogger().error("Error executing command: ", e);
            }
        } else {
            getLogger().error("Unknown command: " + commandName);
        }
    }

    /**
     * Выводит список доступных команд.
     */
    public void listCommands() {
        getLogger().info("Available commands:");
        for (ConsoleCommand command : commands.values()) {
            getLogger().info("- " + command.getName() + ": " + command.getDescription());
        }
    }
}
