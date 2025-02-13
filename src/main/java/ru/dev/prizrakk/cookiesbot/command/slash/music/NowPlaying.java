package ru.dev.prizrakk.cookiesbot.command.slash.music;

import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.dev.prizrakk.cookiesbot.command.CommandCategory;
import ru.dev.prizrakk.cookiesbot.command.ICommand;
import ru.dev.prizrakk.cookiesbot.lavalink.GuildMusicManager;
import ru.dev.prizrakk.cookiesbot.lavalink.LavalinkManager;
import ru.dev.prizrakk.cookiesbot.util.Utils;

import java.awt.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
            event.reply(getLangMessage(event.getMember().getUser(),event.getGuild(), "command.doNotSendPrivateMessagesToTheBot"))
                    .setEphemeral(true)
                    .queue();
            return;
        }
        Guild guild = event.getGuild();
        Member member = event.getMember();
        GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.inAudioChannel()) {
            event.reply(getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.nowPlaying.notFoundMemberInVoice-message")
                    .replace("%voiceChannel%", memberVoiceState.getChannel().getAsMention())).queue();
            return;
        }

        Member self = event.getGuild().getSelfMember();
        GuildVoiceState selfVoiceState = self.getVoiceState();

        if (!selfVoiceState.inAudioChannel()) {
            event.getGuild().getAudioManager().openAudioConnection(memberVoiceState.getChannel());
        } else if (!selfVoiceState.getChannel().equals(memberVoiceState.getChannel())) {
            event.reply(getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.nowPlaying.notFoundMemberInVoice-message")).queue();

            return;
        }
        final var link = lavalinkManager.getLavalinkClient().getOrCreateLink(guild.getIdLong());
        final var player = link.getCachedPlayer();

        if (player == null) {
            event.reply(getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.nowPlaying.noConnected-voice-player")).queue();
            return;
        }

        final var track = player.getTrack();
        if (track == null) {
            event.reply(getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.nowPlaying.notFoundQueue-message")).queue();
            return;
        }

        final var trackInfo = track.getInfo();
        boolean isPaused = player.getPaused();
        String playStatus = isPaused ? getLangMessage(event.getMember().getUser(),guild, "command.slash.nowPlaying.playstatus.falseSound") : getLangMessage(event.getMember().getUser(),guild, "command.slash.nowPlaying.playstatus.trueSound");

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(isPaused ? Color.GRAY : new Color(111, 50, 1));
        embed.setAuthor(trackInfo.getAuthor());
        embed.setThumbnail(getThumbnail(trackInfo.getUri()));

        //embed.setTitle(playStatus + " | " + trackInfo.getTitle(), trackInfo.getUri());
        embed.setTitle(getLangMessage(event.getMember().getUser(),guild, "command.slash.nowPlaying.embed.title")
                .replace("%playStatus%", playStatus)
                .replace("%trackTitle%", trackInfo.getTitle()), trackInfo.getUri());

        embed.addField(getLangMessage(event.getMember().getUser(),guild, "command.slash.nowPlaying.embed.field.author.title"), trackInfo.getAuthor(), true);
        embed.addField(getLangMessage(event.getMember().getUser(),guild, "command.slash.nowPlaying.embed.field.duration.title"), formatTime(trackInfo.getLength()), true);
        embed.addField(getLangMessage(event.getMember().getUser(),guild, "command.slash.nowPlaying.embed.field.link.title")
                , getLangMessage(event.getMember().getUser(),guild, "command.slash.nowPlaying.embed.field.link.description").replace("%trackURI", trackInfo.getUri()), false);
        embed.addField(getLangMessage(event.getMember().getUser(),guild, "command.slash.nowPlaying.embed.field.volume.title"), String.valueOf(getVolume(event, lavalinkManager.getLavalinkClient())), true);
        embed.addField(getLangMessage(event.getMember().getUser(),guild, "command.slash.nowPlaying.embed.field.progressbar.title"),
                getLangMessage(event.getMember().getUser(),guild, "command.slash.nowPlaying.embed.field.progressbar.description")
                        .replace("%position%", formatTime(trackInfo.getPosition()))
                        .replace("%length%", formatTime(trackInfo.getLength()))
                        .replace("%progressBar%", getProgressBar(trackInfo.getPosition(), trackInfo.getLength()))
                ,false);
        embed.setFooter(getLangMessage(event.getMember().getUser(),guild, "command.slash.nowPlaying.embed.footer"));

        event.replyEmbeds(embed.build()).queue();
    }

    private String getProgressBar(long position, long duration) {
        int totalBars = 19;
        int filledBars = (int) ((position * totalBars) / duration);
        return "▬".repeat(filledBars) + "🔵" + "▬".repeat(totalBars - filledBars);
    }
    private int getVolume(SlashCommandInteractionEvent event, LavalinkClient lavalinkClient) {
        AtomicInteger volume = new AtomicInteger();
        lavalinkClient.getOrCreateLink(event.getGuild().getIdLong())
                .getPlayer()
                .flatMap(player -> player.setVolume(player.getVolume()))
                .subscribe(player -> volume.set(player.getVolume()));
        return volume.get();
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
}
