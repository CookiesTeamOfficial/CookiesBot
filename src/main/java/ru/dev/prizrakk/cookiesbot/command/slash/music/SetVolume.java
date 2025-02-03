package ru.dev.prizrakk.cookiesbot.command.slash.music;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.player.LavalinkPlayer;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.dev.prizrakk.cookiesbot.command.CommandCategory;
import ru.dev.prizrakk.cookiesbot.command.ICommand;
import ru.dev.prizrakk.cookiesbot.lavalink.GuildMusicManager;
import ru.dev.prizrakk.cookiesbot.lavalink.LavalinkManager;
import ru.dev.prizrakk.cookiesbot.util.Utils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetVolume extends Utils implements ICommand {
    private final LavalinkManager lavalinkManager;
    public final Map<Long, GuildMusicManager> musicManagers = new HashMap<>();

    public SetVolume(LavalinkManager lavalinkManager) {
        this.lavalinkManager = lavalinkManager;
    }
    @Override
    public String getName() {
        return "volume";
    }

    @Override
    public String getDescription() {
        return "volume";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.INTEGER, "volume", "Название или URL трека", false));
        return options;
    }

    @Override
    public CommandCategory getCategory() {
        return null;
    }

    @Override
    public List<Permission> getRequiredPermissions() {
        return List.of();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws SQLException {
        if (event.getChannelType() != ChannelType.TEXT) {
            event.reply(getLangMessage(event.getGuild(), "command.doNotSendPrivateMessagesToTheBot"))
                    .setEphemeral(true)
                    .queue();
            return;
        }
        String volume = event.getOption("volume").getAsString();
        if (!(volume == null || volume.isEmpty())) {
            LavalinkClient client = lavalinkManager.getLavalinkClient();
            client.getOrCreateLink(event.getGuild().getIdLong())
                    .getPlayer()
                    .flatMap((player) -> player.setVolume(Integer.parseInt(volume)))
                    .subscribe((player) -> {
                        event.reply("Volume set " + player.getVolume() + "!").queue();
                    });
        } else {
            LavalinkClient client = lavalinkManager.getLavalinkClient();
            client.getOrCreateLink(event.getGuild().getIdLong())
                    .getPlayer()
                    .flatMap((player) -> player.setVolume(player.getVolume()))
                    .subscribe((player) -> {
                        event.reply("Volume: " + player.getVolume() + "!").queue();
                    });
        }
    }
}
