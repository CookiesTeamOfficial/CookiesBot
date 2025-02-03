package ru.dev.prizrakk.cookiesbot.command.slash.music;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
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

public class NowPlaying extends Utils implements ICommand {
    private final LavalinkManager lavalinkManager;
    public final Map<Long, GuildMusicManager> musicManagers = new HashMap<>();

    public NowPlaying(LavalinkManager lavalinkManager) {
        this.lavalinkManager = lavalinkManager;
    }
    @Override
    public String getName() {
        return "nowplaying";
    }

    @Override
    public String getDescription() {
        return "nowplaying";
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
        Guild guild = event.getGuild();
        final var link = lavalinkManager.getLavalinkClient().getOrCreateLink(guild.getIdLong());
        final var player = link.getCachedPlayer();

        if (player == null) {
            event.reply("Not connected or no player available!").queue();

        }

        final var track = player.getTrack();

        if (track == null) {
            event.reply("Nothing playing currently!").queue();

        }

        final var trackInfo = track.getInfo();

        event.reply(
                "Currently playing: %s\nDuration: %s/%s".formatted(
                        trackInfo.getTitle(),
                        player.getPosition(),
                        trackInfo.getLength()
                )
        ).queue();
    }
}
