package ru.dev.prizrakk.cookiesbot.command.slash.music;

import dev.arbjerg.lavalink.client.Link;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import ru.dev.prizrakk.cookiesbot.command.CommandCategory;
import ru.dev.prizrakk.cookiesbot.command.ICommand;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.dev.prizrakk.cookiesbot.lavalink.AudioLoader;
import ru.dev.prizrakk.cookiesbot.lavalink.LavalinkManager;
import ru.dev.prizrakk.cookiesbot.lavalink.GuildMusicManager;
import ru.dev.prizrakk.cookiesbot.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Play extends Utils implements ICommand {
    SlashCommandInteractionEvent event;
    private GuildMusicManager mngr;
    private final LavalinkManager lavalinkManager;
    public final Map<Long, GuildMusicManager> musicManagers = new HashMap<>();

    public Play(LavalinkManager lavalinkManager) {
        this.lavalinkManager = lavalinkManager;
    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return "Запускает музыку!";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "name", "Название или URL трека", true));
        return options;
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
    public void execute(SlashCommandInteractionEvent event) {
        this.event = event;
        if (event.getChannelType() != ChannelType.TEXT) {
            event.reply(getLangMessage(event.getGuild(), "command.doNotSendPrivateMessagesToTheBot"))
                    .setEphemeral(true)
                    .queue();
            return;
        }
        Guild guild = event.getGuild();

        Member member = event.getMember();
        GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.inAudioChannel()) {
            event.reply(getLangMessage(event.getGuild(), "command.slash.play.notFoundMemberInVoice.message")).queue();
            return;
        }

        Member self = event.getGuild().getSelfMember();
        GuildVoiceState selfVoiceState = self.getVoiceState();

        if (!selfVoiceState.inAudioChannel()) {
            event.getGuild().getAudioManager().openAudioConnection(memberVoiceState.getChannel());
        } else if (!selfVoiceState.getChannel().equals(memberVoiceState.getChannel())) {
            event.reply(getLangMessage(event.getGuild(), "command.slash.play.notFoundMemberInVoice.message")).queue();

            return;
        }

        String name = event.getOption("name").getAsString();
        if (!name.startsWith("http")) {
            name = "ytsearch:" + name; // Поиск на YouTube, если это не ссылка
        }

        if (guild.getSelfMember().getVoiceState().inAudioChannel()) {
            event.deferReply(false).queue();
        } else {

            if (memberVoiceState.inAudioChannel()) {
                event.getJDA().getDirectAudioController().connect(memberVoiceState.getChannel());
            }

            this.getOrCreateMusicManager(member.getGuild().getIdLong());
        }
        //event.deferReply().queue();

        Link link = lavalinkManager.getLavalinkClient().getOrCreateLink(event.getGuild().getIdLong());
        var mngr = getOrCreateMusicManager(event.getGuild().getIdLong());
        this.mngr = mngr;
        link.loadItem(name).subscribe(new AudioLoader(event, mngr, lavalinkManager.getLavalinkClient()));

    }
    private GuildMusicManager getOrCreateMusicManager(long guildId) {
        synchronized(this) {
            var mng = this.musicManagers.get(guildId);

            if (mng == null) {
                mng = new GuildMusicManager(guildId, lavalinkManager.getLavalinkClient());
                this.musicManagers.put(guildId, mng);
            }

            return mng;
        }
    }
}