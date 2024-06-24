package ru.dev.prizrakk.cookiesbot.manager;

import java.io.Console;

import net.dv8tion.jda.api.JDA;
import ru.dev.prizrakk.cookiesbot.util.Utils;

public class ConsoleManager extends Utils
{
    public JDA jda;
    Console console = System.console();
    public void console() {
        Console console = System.console();
        if (console == null) {
            getLogger().warn(LangManager.getMessage("console.not-work.message"));
            return;
        }

        // Пример команды с табуляцией
        getLogger().info("Example command with tabs:%n");
        getLogger().info("Command\tDescription%n");
        getLogger().info("-------\t----------%n");
        getLogger().info("help\tShow list of commands%n");
        getLogger().info("info\tShow information%n");
        getLogger().info("exit\tExit program%n");

        while (true) {
            // Считывание введенной команды
            String input = console.readLine("$: ");
            switch (input.toLowerCase()) {
                case "help":
                    getLogger().info("List of available commands:%n");
                    getLogger().info("help\tShow list of commands%n");
                    getLogger().info("info\tShow information%n");
                    getLogger().info("exit\tExit program%n");
                    break;
                case "info":
                    getLogger().info("Info: This is an example console program%n");
                    break;
                case "exit":
                    getLogger().info("Program completed%n");
                    System.exit(0);
                    break;
                default:
                    getLogger().info("Invalid command%n");
                    break;
            }
        }
    }
}
