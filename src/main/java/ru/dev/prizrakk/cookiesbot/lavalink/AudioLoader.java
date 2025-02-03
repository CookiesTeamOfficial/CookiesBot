package ru.dev.prizrakk.cookiesbot.lavalink;

import dev.arbjerg.lavalink.client.AbstractAudioLoadResultHandler;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.player.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

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
        final Track track = result.getTrack();
        this.mngr.scheduler.enqueue(track);
        sendTrackEmbed(event, track);
    }

    @Override
    public void onPlaylistLoaded(@NotNull PlaylistLoaded result) {
        this.mngr.scheduler.enqueuePlaylist(result.getTracks());
        event.reply("Added " + result.getTracks().size() + " tracks to queue from " + result.getInfo().getName() + "!").queue();
    }

    @Override
    public void onSearchResultLoaded(@NotNull SearchResult result) {
        List<Track> tracks = result.getTracks();
        if (tracks.isEmpty()) {
            event.reply("No tracks found!").queue();
            return;
        }
        final Track firstTrack = tracks.get(0);
        this.mngr.scheduler.enqueue(firstTrack);
        sendTrackEmbed(event, firstTrack);
    }

    @Override
    public void noMatches() {
        event.reply("No matches found for your input!").queue();
    }

    @Override
    public void loadFailed(@NotNull LoadFailed result) {
        event.reply("Failed to load track! " + result.getException().getMessage()).queue();
    }

    private void sendTrackEmbed(SlashCommandInteractionEvent event, Track track) {
        // Отменяем обновление старого Embed, если оно активно
        if (updateFuture != null && !updateFuture.isCancelled()) {
            updateFuture.cancel(false);
            lastEmbedMessage = null;
        }

        EmbedBuilder embed = buildTrackEmbed(track, track.getInfo().getPosition());
        event.replyEmbeds(embed.build()).queue(response -> {
            response.retrieveOriginal().queue(originalMessage -> {
                lastEmbedMessage = originalMessage;
                startUpdatingEmbed(track);
            });
        });
    }

    private void startUpdatingEmbed(Track track) {
        updateFuture = scheduler.scheduleAtFixedRate(() -> {
            if (lastEmbedMessage == null || track.getInfo().isStream()) return;
            lavalinkClient.getOrCreateLink(event.getGuild().getIdLong())
                    .getPlayer()
                    .subscribe(player -> {
                        long currentPosition = player.getPosition();
                        // Если трек завершён, отменяем обновления
                        if (currentPosition >= track.getInfo().getLength()) {
                            updateFuture.cancel(false);
                            lastEmbedMessage = null;
                            // Если очередь пуста, очищаем очередь и выходим с войса
                            if (mngr.scheduler.queue.isEmpty()) {
                                getOrCreateMusicManager(event.getGuild().getIdLong(), lavalinkClient); // Этот метод должен сбрасывать очередь, если требуется
                                event.getGuild().getAudioManager().closeAudioConnection();
                            }
                            return;
                        }
                        lastEmbedMessage.editMessageEmbeds(buildTrackEmbed(track, currentPosition).build()).queue();
                    });
        }, 1, 1, TimeUnit.SECONDS);
    }


    private EmbedBuilder buildTrackEmbed(Track track, long currentPosition) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.CYAN);
        embed.setTitle(track.getInfo().getTitle(), track.getInfo().getUri());
        embed.setAuthor(track.getInfo().getAuthor());

        long duration = track.getInfo().getLength();
        String progressBar = getProgressBar(currentPosition, duration);
        String timeInfo = String.format("`%s / %s`", formatTime(currentPosition), formatTime(duration));

        embed.addField("Time", timeInfo, false);
        embed.addField("Progress", progressBar, false);
        embed.addField("Volume", "`" + getVolume() + "%`", false);

        return embed;
    }

    private int getVolume() {
        AtomicInteger volume = new AtomicInteger();
        lavalinkClient.getOrCreateLink(event.getGuild().getIdLong())
                .getPlayer()
                .flatMap(player -> player.setVolume(player.getVolume()))
                .subscribe(player -> volume.set(player.getVolume()));
        return volume.get();
    }

    private String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        seconds %= 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private String getProgressBar(long position, long duration) {
        int totalBars = 20;
        int progressBars = (int) ((double) position / duration * totalBars);
        String progress = "▬".repeat(progressBars) + "🔘" + "▬".repeat(totalBars - progressBars);
        return "`" + progress + "`";
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
