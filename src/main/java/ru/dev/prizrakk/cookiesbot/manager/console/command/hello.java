package ru.dev.prizrakk.cookiesbot.manager.console.command;

import ru.dev.prizrakk.cookiesbot.manager.console.ConsoleCommand;

public class hello implements ConsoleCommand {
    @Override
    public String getName() {
        return "hello";
    }

    @Override
    public String getDescription() {
        return "hello";
    }

    @Override
    public void execute(String[] args) {
    }
}
