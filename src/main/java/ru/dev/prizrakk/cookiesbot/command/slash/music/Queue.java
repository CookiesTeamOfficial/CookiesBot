package ru.dev.prizrakk.cookiesbot.command.slash.music;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import ru.dev.prizrakk.cookiesbot.command.CommandCategory;
import ru.dev.prizrakk.cookiesbot.command.ICommand;
import ru.dev.prizrakk.cookiesbot.command.CommandStatus;
import ru.dev.prizrakk.cookiesbot.lavaplayer.GuildMusicManager;
import ru.dev.prizrakk.cookiesbot.lavaplayer.PlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.dev.prizrakk.cookiesbot.util.Utils;

import java.util.ArrayList;
import java.util.List;



public class Queue extends Utils implements ICommand {
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

    @Override
    public List<Permission> getRequiredPermissions() {
        return List.of(Permission.MESSAGE_SEND);
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
            event.reply(getLangMessage(event.getGuild(), "command.slash.queue.notFoundMemberInVoice.message")).queue();
            return;
        }

        Member self = event.getGuild().getSelfMember();
        GuildVoiceState selfVoiceState = self.getVoiceState();

        if(!selfVoiceState.inAudioChannel()) {
            event.reply(getLangMessage(event.getGuild(), "command.slash.queue.notFoundBotInVoice.message")).queue();
            return;
        }

        if(selfVoiceState.getChannel() != memberVoiceState.getChannel()) {
            event.reply(getLangMessage(event.getGuild(), "command.slash.queue.memberInVoice.message").replace("%voiceChannel%", selfVoiceState.getChannel().getAsMention())).queue();
            return;
        }

        GuildMusicManager guildMusicManager = PlayerManager.get().getGuildMusicManager(event.getGuild());
        List<AudioTrack> queue = new ArrayList<>(guildMusicManager.getTrackScheduler().getQueue());
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(getLangMessage(event.getGuild(), "command.slash.queue.embed.title.message"));
        if(queue.isEmpty()) {
            embedBuilder.setDescription(getLangMessage(event.getGuild(), "command.slash.queue.embed.descriptionErrNotFoundQueue.message"));
        }
        for(int i = 0; i < queue.size(); i++) {
            AudioTrackInfo info = queue.get(i).getInfo();
            embedBuilder.addField(i+1 + ":", info.title, false);
        }
        event.replyEmbeds(embedBuilder.build()).queue();
    }
}
