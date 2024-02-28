package ru.dev.prizrakk.command.slash.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import ru.dev.prizrakk.ICommand;
import ru.dev.prizrakk.lavaplayer.GuildMusicManager;
import ru.dev.prizrakk.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.dev.prizrakk.util.Config;
import ru.dev.prizrakk.util.Values;
import sun.security.krb5.internal.TGSRep;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class Play implements ICommand {
    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return "Запускает стойку диджея! ЭЙЙ подождите меня я!!!!!";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "name", "Какую музыку будет играть диджей за стойкой?", true));
        return options;
    }
    Config config = new Config();

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        GuildVoiceState memberVoiceState = member.getVoiceState();

        if(!memberVoiceState.inAudioChannel()) {
            event.reply("Тебя нет в голосовом канале!").queue();
            return;
        }

        Member self = event.getGuild().getSelfMember();
        GuildVoiceState selfVoiceState = self.getVoiceState();

        if(!selfVoiceState.inAudioChannel()) {
            event.getGuild().getAudioManager().openAudioConnection(memberVoiceState.getChannel());
        } else {
            if(selfVoiceState.getChannel() != memberVoiceState.getChannel()) {
                event.reply("Тебя нет в голосовом канале со мной вернись!").queue();
                return;
            }
        }

        String name = event.getOption("name").getAsString();
        try {
            new URI(name);
        } catch (URISyntaxException e) {
            name = "ytsearch:" + name;
        }
        PlayerManager playerManager = PlayerManager.get();
        playerManager.play(event.getGuild(), name);
        event.deferReply().queue();
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        GuildMusicManager guildMusicManager = PlayerManager.get().getGuildMusicManager(event.getGuild());
        AudioTrackInfo info = guildMusicManager.getTrackScheduler().getPlayer().getPlayingTrack().getInfo();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Я играю щас:");
        embedBuilder.setDescription("**Имя:** `" + info.title + "`");
        embedBuilder.appendDescription("\n**Автор:** `" + info.author + "`");
        embedBuilder.appendDescription("\n**Ссылка:  [клик]("+ info.uri +")**");
        embedBuilder.appendDescription("\n**Репит:** `" + Values.isRepeat + "`");
        String[] parts = info.uri.split("=");
        embedBuilder.setThumbnail("http://img.youtube.com/vi/" + parts[1] + "/mqdefault.jpg").toString();
        embedBuilder.setFooter(config.years_author);
        event.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
    }
}
