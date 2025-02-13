package ru.dev.prizrakk.cookiesbot;

import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static ru.dev.prizrakk.cookiesbot.util.Utils.getLogger;

public class OnReady extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent event) {
        getLogger().info("The bot is ready to work");
    }
}
