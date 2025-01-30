package ru.dev.prizrakk.cookiesbot.manager.console;

public interface ConsoleCommand {
    /**
     * Имя команды.
     */
    String getName();

    /**
     * Описание команды.
     */
    String getDescription();

    /**
     * Выполнение команды.
     *
     * @param args аргументы команды
     */
    void execute(String[] args);
}
