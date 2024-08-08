package ru.dev.prizrakk.cookiesbot.command.slash.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import org.apache.commons.codec.language.bm.Lang;
import ru.dev.prizrakk.cookiesbot.command.CommandCategory;
import ru.dev.prizrakk.cookiesbot.command.ICommand;
import ru.dev.prizrakk.cookiesbot.command.CommandStatus;
import ru.dev.prizrakk.cookiesbot.database.Database;
import ru.dev.prizrakk.cookiesbot.database.DatabaseUtils;
import ru.dev.prizrakk.cookiesbot.database.GuildVariable;
import ru.dev.prizrakk.cookiesbot.lavaplayer.GuildMusicManager;
import ru.dev.prizrakk.cookiesbot.lavaplayer.PlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.dev.prizrakk.cookiesbot.manager.LangManager;
import ru.dev.prizrakk.cookiesbot.util.Utils;
import ru.dev.prizrakk.cookiesbot.util.Values;

import java.sql.SQLException;
import java.util.List;

public class NowPlaying extends Utils implements ICommand {
    @Override
    public String getName() {
        return "nowplaying";
    }

    @Override
    public String getDescription() {
        return "Показывает текущую музыку";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MUSIC;
    }

    @Override
    public CommandStatus getStatus() {
        return CommandStatus.ERROR;
    }


    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getChannelType() != ChannelType.TEXT) {
            event.reply(getLangMessage(event.getGuild(), "command.doNotSendPrivateMessagesToTheBot")).setEphemeral(true).queue();
            return;
        }

        Member member = event.getMember();
        GuildVoiceState memberVoiceState = member.getVoiceState();

        if(!memberVoiceState.inAudioChannel()) {
            event.reply(getLangMessage(event.getGuild(), "command.slash.nowPlaying.notFoundMember.message")).queue();
            return;
        }

        Member self = event.getGuild().getSelfMember();
        GuildVoiceState selfVoiceState = self.getVoiceState();

        if(!selfVoiceState.inAudioChannel()) {
            event.reply(getLangMessage(event.getGuild(), "command.slash.nowPlaying.notFoundNotInVoice.message")).queue();
            return;
        }

        if(selfVoiceState.getChannel() != memberVoiceState.getChannel()) {
            event.reply(getLangMessage(event.getGuild(), "command.slash.nowPlaying.notFoundNotInVoice.message").replace("%voiceChannel%", selfVoiceState.getChannel().getAsMention())).queue();
            return;
        }

        GuildMusicManager guildMusicManager = PlayerManager.get().getGuildMusicManager(event.getGuild());
        if(guildMusicManager.getTrackScheduler().getPlayer().getPlayingTrack() == null) {
            event.reply(getLangMessage(event.getGuild(), "command.slash.nowPlaying.notFoundQueue.message")).queue();
            return;
        }
        AudioTrackInfo info = guildMusicManager.getTrackScheduler().getPlayer().getPlayingTrack().getInfo();
        AudioTrack track = guildMusicManager.getTrackScheduler().getPlayer().getPlayingTrack();
        long duration = track.getDuration(); // Длительность трека в миллисекундах
        long position = track.getPosition(); // Текущее время проигрывания трека в миллисекундах
        long durationMinutes = (duration / 1000) / 60;
        long durationSeconds = (duration / 1000) % 60;

        // Преобразование текущего времени проигрывания трека в минуты и секунды
        long positionMinutes = (position / 1000) / 60;
        long positionSeconds = (position / 1000) % 60;


        int barLength = 20; // Длина полосы прогресса (количество символов)
        double progress = (double) position / duration; // Прогресс в процентах

        // Создание строки прогресса
        StringBuilder progressBar = new StringBuilder();
        for (int i = 0; i < barLength; i++) {
            if (i < progress * barLength) {
                progressBar.append("▰"); // Символ для заполненной части
            } else {
                progressBar.append("▱"); // Символ для не заполненной части
            }
        }
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(getLangMessage(event.getGuild(), "command.slash.nowPlaying.title.message"));
        String isRepeat = String.valueOf(Values.isRepeat);
        embedBuilder.setDescription(getLangMessage(event.getGuild(), "command.slash.nowPlaying.description.message")
                .replace("%title%", info.title)
                .replace("%author%", info.author)
                .replace("%uri%", info.uri)
                .replace("%isRepeat%", isRepeat));
        embedBuilder.addField(
                getLangMessage(event.getGuild(), "command.slash.nowPlaying.field.title.message"),
                getLangMessage(event.getGuild(), "command.slash.nowPlaying.field.description.message")
                        .replace("%positionMinutes%", positionMinutes + "")
                        .replace("%positionSeconds%", positionSeconds + "")
                        .replace("%durationMinutes%", durationMinutes + "")
                        .replace("%durationSeconds%", durationSeconds + ""),
                true);
        String[] parts = info.uri.split("=");
        embedBuilder.setThumbnail("http://img.youtube.com/vi/" + parts[1] + "/mqdefault.jpg").toString();

        //embedBuilder.setFooter(config.years_author);
        event.replyEmbeds(embedBuilder.build()).queue();
    }
}
