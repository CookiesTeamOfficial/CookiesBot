package ru.dev.prizrakk.cookiesbot.lavalink;

import dev.arbjerg.lavalink.client.AbstractAudioLoadResultHandler;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.player.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.dev.prizrakk.cookiesbot.util.Utils.getLangMessage;

public class AudioLoader extends AbstractAudioLoadResultHandler {
    private final SlashCommandInteractionEvent event;
    private final GuildMusicManager mngr;
    private final LavalinkClient lavalinkClient;
    private ScheduledFuture<?> updateFuture;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private Message lastEmbedMessage;
    public final Map<Long, GuildMusicManager> musicManagers = new HashMap<>();

    public AudioLoader(SlashCommandInteractionEvent event, GuildMusicManager mngr, LavalinkClient lavalinkClient) {
        this.event = event;
        this.mngr = mngr;
        this.lavalinkClient = lavalinkClient;
    }

    @Override
    public void ontrackLoaded(@NotNull TrackLoaded result) {
        Track track = result.getTrack();
        this.mngr.scheduler.enqueue(track);
        sendTrackEmbed(event, track);
    }

    @Override
    public void onPlaylistLoaded(@NotNull PlaylistLoaded result) {
        this.mngr.scheduler.enqueuePlaylist(result.getTracks());
        String message = getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.play.response.playlist")
                .replace("%size%", String.valueOf(result.getTracks().size()))
                .replace("%playlistName%", result.getInfo().getName());
        event.reply(message).queue();
    }

    @Override
    public void onSearchResultLoaded(@NotNull SearchResult result) {
        List<Track> tracks = result.getTracks();
        if (tracks.isEmpty()) {
            event.reply(getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.play.notFoundQueue-message")).queue();
            return;
        }
        Track firstTrack = tracks.get(0);
        this.mngr.scheduler.enqueue(firstTrack);
        sendTrackEmbed(event, firstTrack);
    }

    @Override
    public void noMatches() {
        event.reply(getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.play.response.noMatches")).queue();
    }

    @Override
    public void loadFailed(@NotNull LoadFailed result) {
        event.reply(getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.play.response.loadFailed")
                .replace("%error%", result.getException().getMessage())).queue();
    }

    private void sendTrackEmbed(SlashCommandInteractionEvent event, Track track) {
        if (lastEmbedMessage != null) {
            lastEmbedMessage.delete().queue();
        }

        Guild guild = event.getGuild();
        AtomicBoolean isPaused = new AtomicBoolean(false);
        AtomicInteger volume = new AtomicInteger();

        lavalinkClient.getOrCreateLink(event.getGuild().getIdLong())
                .getPlayer()
                .subscribe(player -> {
                    isPaused.set(player.getPaused());
                    volume.set(player.getVolume());
                });

        String playStatus = isPaused.get() ? getLangMessage(event.getMember().getUser(),guild, "command.slash.play.playstatus.falseSound") : getLangMessage(event.getMember().getUser(),guild, "command.slash.play.playstatus.trueSound");

        EmbedBuilder embed = buildTrackEmbed(track, track.getInfo().getPosition(), isPaused.get(), guild, playStatus, volume.get());
        event.replyEmbeds(embed.build()).queue(response -> response.retrieveOriginal().queue(originalMessage -> {
            lastEmbedMessage = originalMessage;
            startUpdatingEmbed(track);
        }));
    }


    private void startUpdatingEmbed(Track track) {
        if (updateFuture != null && !updateFuture.isCancelled()) {
            updateFuture.cancel(false);
        }

        updateFuture = scheduler.scheduleAtFixedRate(() -> {
            if (lastEmbedMessage == null || track.getInfo().isStream()) return;
            lavalinkClient.getOrCreateLink(event.getGuild().getIdLong())
                    .getPlayer()
                    .subscribe(player -> {
                        long currentPosition = player.getPosition();
                        Guild guild = event.getGuild();
                        String playStatus = player.getPaused() ? getLangMessage(event.getMember().getUser(),guild, "command.slash.nowPlaying.playstatus.falseSound") : getLangMessage(event.getMember().getUser(),guild, "command.slash.nowPlaying.playstatus.trueSound");

                        if (currentPosition >= track.getInfo().getLength()) {
                            updateFuture.cancel(false);
                            lastEmbedMessage = null;
                            if (mngr.scheduler.queue.isEmpty()) {
                                getOrCreateMusicManager(event.getGuild().getIdLong(), lavalinkClient);
                                event.getGuild().getAudioManager().closeAudioConnection();
                            }
                            return;
                        }

                        lastEmbedMessage.editMessageEmbeds(buildTrackEmbed(track, currentPosition, player.getPaused(), guild, playStatus, player.getVolume()).build()).queue();
                    });
        }, 5, 5, TimeUnit.SECONDS);
    }



    private EmbedBuilder buildTrackEmbed(Track track, long currentPosition, boolean isPaused, Guild guild, String playStatus, int volume) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(isPaused ? Color.GRAY : new Color(111, 50, 1));
        embed.setAuthor(track.getInfo().getAuthor());
        embed.setThumbnail(getThumbnail(track.getInfo().getUri()));

        embed.setTitle(getLangMessage(event.getMember().getUser(),guild, "command.slash.play.embed.title")
                .replace("%playStatus%", playStatus)
                .replace("%trackTitle%", track.getInfo().getTitle()), track.getInfo().getUri());

        embed.addField(getLangMessage(event.getMember().getUser(),guild, "command.slash.play.embed.field.author.title"), track.getInfo().getAuthor(), true);
        embed.addField(getLangMessage(event.getMember().getUser(),guild, "command.slash.play.embed.field.duration.title"), formatTime(track.getInfo().getLength()), true);
        embed.addField(getLangMessage(event.getMember().getUser(),guild, "command.slash.play.embed.field.volume.title"), String.valueOf(volume), true);
        if (!track.getInfo().isStream()) {
            embed.addField(getLangMessage(event.getMember().getUser(),guild, "command.slash.play.embed.field.progressbar.title"),
                    getLangMessage(event.getMember().getUser(),guild, "command.slash.play.embed.field.progressbar.description")
                            .replace("%position%", formatTime(currentPosition))
                            .replace("%length%", formatTime(track.getInfo().getLength()))
                            .replace("%progressBar%", getProgressBar(currentPosition, track.getInfo().getLength())), false);
        } else {
            embed.addField(getLangMessage(event.getMember().getUser(),guild, "command.slash.play.embed.field.progressbar.title"),
                    getLangMessage(event.getMember().getUser(),guild, "command.slash.play.embed.field.progressbar.stream.description"), false);
        }

        embed.setFooter(getLangMessage(event.getMember().getUser(),guild, "command.slash.play.embed.footer"));
        return embed;
    }
    private String getProgressBar(long position, long duration) {
        int totalBars = 19;
        int filledBars = (int) ((position * totalBars) / duration);
        return "▬".repeat(filledBars) + "🔵" + "▬".repeat(totalBars - filledBars);
    }
    private String getThumbnail(String url) {
        if (url.contains("youtube.com") || url.contains("youtu.be")) {
            String videoId = url.split("v=")[1].split("&")[0];
            return "https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg";
        }
        return null;
    }
    private String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        seconds %= 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    private GuildMusicManager getOrCreateMusicManager(long guildId, LavalinkClient lavalinkClient) {
        synchronized(this) {
            var mng = this.musicManagers.get(guildId);

            if (mng == null) {
                mng = new GuildMusicManager(guildId, lavalinkClient);
                this.musicManagers.put(guildId, mng);
            }

            return mng;
        }
    }

}
