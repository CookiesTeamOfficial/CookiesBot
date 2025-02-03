package ru.dev.prizrakk.cookiesbot.command.slash.music;

import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.dev.prizrakk.cookiesbot.command.CommandCategory;
import ru.dev.prizrakk.cookiesbot.command.ICommand;
import ru.dev.prizrakk.cookiesbot.lavalink.GuildMusicManager;
import ru.dev.prizrakk.cookiesbot.lavalink.LavalinkManager;
import ru.dev.prizrakk.cookiesbot.util.Utils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pause extends Utils implements ICommand {
    private final LavalinkManager lavalinkManager;
    public final Map<Long, GuildMusicManager> musicManagers = new HashMap<>();

    public Pause(LavalinkManager lavalinkManager) {
        this.lavalinkManager = lavalinkManager;
    }
    @Override
    public String getName() {
        return "pause";
    }

    @Override
    public String getDescription() {
        return "pause off/on";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MUSIC;
    }

    @Override
    public List<Permission> getRequiredPermissions() {
        return List.of(Permission.MESSAGE_SEND);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws SQLException {
        if (event.getChannelType() != ChannelType.TEXT) {
            event.reply(getLangMessage(event.getGuild(), "command.doNotSendPrivateMessagesToTheBot"))
                    .setEphemeral(true)
                    .queue();
            return;
        }
        LavalinkClient client = lavalinkManager.getLavalinkClient();
        client.getOrCreateLink(event.getGuild().getIdLong())
                .getPlayer()
                .flatMap((player) -> player.setPaused(!player.getPaused()))
                .subscribe((player) -> {
                    event.reply("Player has been " + (player.getPaused() ? "paused" : "resumed") + "!").queue();
                });
    }

}
