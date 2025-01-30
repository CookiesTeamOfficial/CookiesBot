package ru.dev.prizrakk.cookiesbot.manager.console.command;

import ru.dev.prizrakk.cookiesbot.manager.LangManager;
import ru.dev.prizrakk.cookiesbot.manager.console.ConsoleCommand;
import ru.dev.prizrakk.cookiesbot.util.Utils;

public class ReloadLang extends Utils implements ConsoleCommand {
    @Override
    public String getName() {
        return "reload-lang";
    }

    @Override
    public String getDescription() {
        return "Reload lang resource";
    }

    @Override
    public void execute(String[] args) {
        LangManager.reloadLanguages();
        getLogger().info("Successful reload language!");
    }
}
