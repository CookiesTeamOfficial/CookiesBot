package ru.dev.prizrakk.cookiesbot.command.slash.music;

import ru.dev.prizrakk.cookiesbot.command.CommandCategory;
import ru.dev.prizrakk.cookiesbot.command.ICommand;
import ru.dev.prizrakk.cookiesbot.command.CommandStatus;
import ru.dev.prizrakk.cookiesbot.lavaplayer.GuildMusicManager;
import ru.dev.prizrakk.cookiesbot.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.dev.prizrakk.cookiesbot.util.Values;

import java.util.List;

public class Repeat implements ICommand {
    @Override
    public String getName() {
        return "repeat";
    }

    @Override
    public String getDescription() {
        return "Ставит на репит";
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
            event.reply("Тебя нет в голосовом канале").queue();
            return;
        }

        Member self = event.getGuild().getSelfMember();
        GuildVoiceState selfVoiceState = self.getVoiceState();

        if(!selfVoiceState.inAudioChannel()) {
            event.reply("Подождите меня забыли").queue();
            return;
        }

        if(selfVoiceState.getChannel() != memberVoiceState.getChannel()) {
            event.reply("Тебя нет со мной вернись ко мне").queue();
            return;
        }

        GuildMusicManager guildMusicManager = PlayerManager.get().getGuildMusicManager(event.getGuild());
        Values.isRepeat = !guildMusicManager.getTrackScheduler().isRepeat();
        guildMusicManager.getTrackScheduler().setRepeat(Values.isRepeat);
        event.reply("Щас на репите " + Values.isRepeat).queue();
    }
}
