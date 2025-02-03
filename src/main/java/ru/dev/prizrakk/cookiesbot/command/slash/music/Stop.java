package ru.dev.prizrakk.cookiesbot.command.slash.music;

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

public class Stop extends Utils implements ICommand {
    private final LavalinkManager lavalinkManager;
    public final Map<Long, GuildMusicManager> musicManagers = new HashMap<>();

    public Stop(LavalinkManager lavalinkManager) {
        this.lavalinkManager = lavalinkManager;
    }
    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public String getDescription() {
        return "stop";
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
        event.reply("Stopped the current track and clearing the queue").queue();
        getOrCreateMusicManager(event.getGuild().getIdLong()).stop();
        event.getJDA().getDirectAudioController().disconnect(event.getGuild());
    }
    private GuildMusicManager getOrCreateMusicManager(long guildId) {
        synchronized(this) {
            var mng = this.musicManagers.get(guildId);

            if (mng == null) {
                mng = new GuildMusicManager(guildId, lavalinkManager.getLavalinkClient());
                this.musicManagers.put(guildId, mng);
            }

            return mng;
        }
    }
}
