package ru.dev.prizrakk.cookiesbot.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import ru.dev.prizrakk.cookiesbot.util.Utils;
import ru.dev.prizrakk.cookiesbot.util.Values;

import java.util.HashMap;
import java.util.Map;

public class PlayerManager extends Utils {

    private static PlayerManager INSTANCE;
    private final Map<Long, GuildMusicManager> guildMusicManagers = new HashMap<>();
    private final AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();

    private PlayerManager() {
        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
        AudioSourceManagers.registerLocalSource(audioPlayerManager);
    }



    public static PlayerManager get() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }
        return INSTANCE;
    }

    public GuildMusicManager getGuildMusicManager(Guild guild) {
        return guildMusicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            GuildMusicManager musicManager = new GuildMusicManager(audioPlayerManager, guild);
            guild.getAudioManager().setSendingHandler(musicManager.getAudioForwarder());
            return musicManager;
        });
    }

    public void play(Guild guild, String trackURL, SlashCommandInteractionEvent event) {
        GuildMusicManager guildMusicManager = getGuildMusicManager(guild);
        audioPlayerManager.loadItemOrdered(guildMusicManager, trackURL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                guildMusicManager.getTrackScheduler().queue(track);
                sendPlayingTrackEmbed(event, track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                guildMusicManager.getTrackScheduler().queue(playlist.getTracks().get(0));
                sendPlayingTrackEmbed(event, playlist.getTracks().get(0));
            }

            @Override
            public void noMatches() {
                event.getHook().sendMessage(getLangMessage(event.getGuild(), "command.slash.play.notFoundTrackInURL.message")
                        .replace("%trackURL%", trackURL)).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                event.getHook().sendMessage(getLangMessage(event.getGuild(), "command.slash.play.errorWithLoadTrack.message")
                        .replace("%errorMessage%", exception.getMessage())).queue();
            }
        });
    }

    private void sendPlayingTrackEmbed(SlashCommandInteractionEvent event, AudioTrack track) {
        AudioTrackInfo info = track.getInfo();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(getLangMessage(event.getGuild(), "command.slash.play.embed.title.message"));
        embedBuilder.setDescription(getLangMessage(event.getGuild(), "command.slash.play.embed.description.message")
                .replace("%title%", info.title)
                .replace("%author%", info.author)
                .replace("%uri%", info.uri)
                .replace("%isRepeat%", Values.isRepeat + ""));
        String[] parts = info.uri.split("=");
        embedBuilder.setThumbnail("http://img.youtube.com/vi/" + parts[1] + "/mqdefault.jpg").toString();
        //embedBuilder.setFooter(new Config().years_author);
        event.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
    }
}
