package ru.dev.prizrakk.cookiesbot.command.slash.music;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import ru.dev.prizrakk.cookiesbot.command.CommandCategory;
import ru.dev.prizrakk.cookiesbot.command.ICommand;
import ru.dev.prizrakk.cookiesbot.command.CommandStatus;
import ru.dev.prizrakk.cookiesbot.lavaplayer.GuildMusicManager;
import ru.dev.prizrakk.cookiesbot.lavaplayer.PlayerManager;
import ru.dev.prizrakk.cookiesbot.lavaplayer.TrackScheduler;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.dev.prizrakk.cookiesbot.util.Utils;

import java.util.List;

public class Stop extends Utils implements ICommand {
    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public String getDescription() {
        return "Останавливает музыку";
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
            event.reply(getLangMessage(event.getGuild(),"command.slash.stop.notFoundMemberInVoice.message")).queue();
            return;
        }

        Member self = event.getGuild().getSelfMember();
        GuildVoiceState selfVoiceState = self.getVoiceState();

        if(!selfVoiceState.inAudioChannel()) {
            event.reply(getLangMessage(event.getGuild(),"command.slash.stop.botInVoice.message")).queue();
            return;
        }

        if(selfVoiceState.getChannel() != memberVoiceState.getChannel()) {
            event.reply(getLangMessage(event.getGuild(),"command.slash.stop.notFoundMemberInVoice.message")).queue();
            return;
        }

        GuildMusicManager guildMusicManager = PlayerManager.get().getGuildMusicManager(event.getGuild());
        TrackScheduler trackScheduler = guildMusicManager.getTrackScheduler();
        trackScheduler.getQueue().clear();
        trackScheduler.getPlayer().stopTrack();
        event.reply(getLangMessage(event.getGuild(),"command.slash.stop.stopMusic.message")).queue();
    }
}
