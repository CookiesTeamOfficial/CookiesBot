package ru.dev.prizrakk.cookiesbot.command.slash.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import ru.dev.prizrakk.cookiesbot.command.CommandCategory;
import ru.dev.prizrakk.cookiesbot.command.ICommand;
import ru.dev.prizrakk.cookiesbot.command.CommandStatus;
import ru.dev.prizrakk.cookiesbot.lavaplayer.GuildMusicManager;
import ru.dev.prizrakk.cookiesbot.lavaplayer.PlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.dev.prizrakk.cookiesbot.util.Config;
import ru.dev.prizrakk.cookiesbot.util.Values;

import java.util.List;

public class NowPlaying implements ICommand {
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
        Member member = event.getMember();
        GuildVoiceState memberVoiceState = member.getVoiceState();

        if(!memberVoiceState.inAudioChannel()) {
            event.reply("Тебя нет в голосовом канале я не могу запустить без тебя музыку :(").queue();
            return;
        }

        Member self = event.getGuild().getSelfMember();
        GuildVoiceState selfVoiceState = self.getVoiceState();

        if(!selfVoiceState.inAudioChannel()) {
            event.reply("Упс подождите меня я забыл зайти").queue();
            return;
        }

        if(selfVoiceState.getChannel() != memberVoiceState.getChannel()) {
            event.reply("Тебя нет в " + selfVoiceState.getChannel().getAsMention() + " со мной приди ко мне").queue();
            return;
        }

        GuildMusicManager guildMusicManager = PlayerManager.get().getGuildMusicManager(event.getGuild());
        if(guildMusicManager.getTrackScheduler().getPlayer().getPlayingTrack() == null) {
            event.reply("Я уже за стойкой диджея!").queue();
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
        embedBuilder.setTitle("Я играю щас:");
        embedBuilder.setDescription("**Имя:** `" + info.title + "`"
                + "\n" + "**Автор:** `" + info.author + "`"
                + "\n" + "**Ссылка:** **[клик](" + info.uri + ")**"
                + "\n" + "**Репит:** `" + Values.isRepeat + "`");
        embedBuilder.addField("Длительность", positionMinutes + "мин. " + positionSeconds + "сек. **/**" + durationMinutes + "мин. " + durationSeconds + "сек."
                + "\n" + "**" + progressBar + "**", true);
        String[] parts = info.uri.split("=");
        embedBuilder.setThumbnail("http://img.youtube.com/vi/" + parts[1] + "/mqdefault.jpg").toString();
        Config config = new Config();
        embedBuilder.setFooter(config.years_author);
        event.replyEmbeds(embedBuilder.build()).queue();
    }
}
