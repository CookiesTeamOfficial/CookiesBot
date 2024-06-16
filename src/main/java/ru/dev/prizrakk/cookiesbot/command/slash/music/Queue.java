package ru.dev.prizrakk.cookiesbot.command.slash.music;

import ru.dev.prizrakk.cookiesbot.command.CommandCategory;
import ru.dev.prizrakk.cookiesbot.command.ICommand;
import ru.dev.prizrakk.cookiesbot.command.slash.CommandStatus;
import ru.dev.prizrakk.cookiesbot.lavaplayer.GuildMusicManager;
import ru.dev.prizrakk.cookiesbot.lavaplayer.PlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.dev.prizrakk.cookiesbot.util.Config;

import java.util.ArrayList;
import java.util.List;



public class Queue implements ICommand {
    @Override
    public String getName() {
        return "queue";
    }

    @Override
    public String getDescription() {
        return "Показывает очередь в пятерочку XD";
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

    Config config = new Config();
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        GuildVoiceState memberVoiceState = member.getVoiceState();

        if(!memberVoiceState.inAudioChannel()) {
            event.reply("Тебя нет в голосовом канале").queue();
            return;
        }

        Member self = event.getGuild().getSelfMember();
        GuildVoiceState selfVoiceState = self.getVoiceState();

        if(!selfVoiceState.inAudioChannel()) {
            event.reply("Меня забыли подождите").queue();
            return;
        }

        if(selfVoiceState.getChannel() != memberVoiceState.getChannel()) {
            event.reply("Тебя нет в "+ selfVoiceState.getChannel().getAsMention() +" зайди туда чтобы использовать команду").queue();
            return;
        }

        GuildMusicManager guildMusicManager = PlayerManager.get().getGuildMusicManager(event.getGuild());
        List<AudioTrack> queue = new ArrayList<>(guildMusicManager.getTrackScheduler().getQueue());
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Текущая очередь");
        if(queue.isEmpty()) {
            embedBuilder.setDescription("очередь пустая как мой кошелек тоже пустой");
        }
        for(int i = 0; i < queue.size(); i++) {
            AudioTrackInfo info = queue.get(i).getInfo();
            embedBuilder.addField(i+1 + ":", info.title, false);
        }
        embedBuilder.setFooter(config.years_author);
        event.replyEmbeds(embedBuilder.build()).queue();
    }
}
