package ru.dev.prizrakk.manager;

import net.dv8tion.jda.api.entities.Guild;

import java.io.Console;
import java.util.Scanner;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ConsoleManager extends ListenerAdapter
{
    public JDA jda;
    LoggerManager log;
    
    public ConsoleManager() {
        this.log = new LoggerManager();
    }
    Console console = System.console();
    public void console() {
        Console console = System.console();
        if (console == null) {
            System.out.println("Консоль не доступна!");
            return;
        }

        // Пример команды с табуляцией
        console.printf("Пример команды с табуляцией:%n");
        console.printf("Команда\tОписание%n");
        console.printf("-------\t-----------%n");
        console.printf("help\tПоказать список команд%n");
        console.printf("info\tПоказать информацию%n");
        console.printf("exit\tВыйти из программы%n");

        while (true) {
            // Считывание введенной команды
            String input = console.readLine("$: ");
            switch (input.toLowerCase()) {
                case "help":
                    console.printf("Список доступных команд:%n");
                    console.printf("help\tПоказать список команд%n");
                    console.printf("info\tПоказать информацию%n");
                    console.printf("exit\tВыйти из программы%n");
                    break;
                case "info":
                    console.printf("Информация: Это пример консольной программы%n");
                    break;
                case "exit":
                    console.printf("Программа завершена%n");
                    System.exit(0);
                    break;
                default:
                    console.printf("Неверная команда%n");
                    break;
            }
        }
    }
}
