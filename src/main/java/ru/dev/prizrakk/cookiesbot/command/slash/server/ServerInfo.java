package ru.dev.prizrakk.cookiesbot.command.slash.server;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.dev.prizrakk.cookiesbot.command.CommandCategory;
import ru.dev.prizrakk.cookiesbot.command.ICommand;
import ru.dev.prizrakk.cookiesbot.util.Utils;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerInfo extends Utils implements ICommand {
    @Override
    public String getName() {
        return "serverinfo";
    }

    @Override
    public String getDescription() {
        return "Информация о сервере";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }
    @Override
    public CommandCategory getCategory() {
        return CommandCategory.SERVER;
    }

    @Override
    public List<Permission> getRequiredPermissions() {
        return List.of(Permission.MESSAGE_SEND);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws SQLException {
        if (event.getChannelType() != ChannelType.TEXT) {
            event.reply(getLangMessage(event.getMember().getUser(),event.getGuild(), "command.doNotSendPrivateMessagesToTheBot")).setEphemeral(true).queue();
            return;
        }
        Guild guild = event.getGuild();
        List<Category> categories = guild.getCategories();
        AtomicInteger users = new AtomicInteger();
        AtomicInteger bots = new AtomicInteger();
        int textChannels = 0;
        int voiceChannels = 0;
        int categoryChannels = 0;
        int newsChannels = 0;
        int stageChannels = 0;
        int forumChannels = 0;
        int onlineUsers = 0;
        int idleUsers = 0;
        int dndUsers = 0;
        int offlineUsers = 0;
        String boostTier;
        String verifLevel = null;

        event.getGuild().loadMembers().onSuccess(members -> {
            for(Member member: members){
                if (member.getUser().isBot())
                    bots.getAndIncrement();
                else
                    users.getAndIncrement();
            }
        });
        for (Member member : guild.getMembers()) {
            switch (member.getOnlineStatus()) {
                case ONLINE -> onlineUsers++;
                case IDLE -> idleUsers++;
                case DO_NOT_DISTURB -> dndUsers++;
                case OFFLINE -> offlineUsers++;
            }
        }
        for(Category category : categories) {
            for(GuildChannel channel : category.getChannels()) {
                switch(channel.getType()) {
                    case TEXT -> textChannels++;
                    case VOICE -> voiceChannels++;
                    case CATEGORY -> categoryChannels++;
                    case NEWS -> newsChannels++;
                    case FORUM -> forumChannels++;
                    case STAGE -> stageChannels++;
                }
            }
        }

        switch (event.getGuild().getBoostTier()) {
            case NONE -> boostTier = "<:Boost0:1209375787040641054> " + getLangMessage(event.getMember().getUser(),guild, "command.slash.serverInfo.boost.Tier0");
            case TIER_1 -> boostTier = "<:Boost1:1209375792685907978> " + getLangMessage(event.getMember().getUser(),guild, "command.slash.serverInfo.boost.Tier1");
            case TIER_2 -> boostTier = "<:Boost2Dark:1209375790459002880> " + getLangMessage(event.getMember().getUser(),guild, "command.slash.serverInfo.boost.Tier2");
            case TIER_3 -> boostTier = "<:Boost3Dark:1209375788739076136> " + getLangMessage(event.getMember().getUser(),guild, "command.slash.serverInfo.boost.Tier3");
            case UNKNOWN -> boostTier = "<:Boost0:1209375787040641054> " + getLangMessage(event.getMember().getUser(),guild, "command.slash.serverInfo.boost.TierUnknown");
            default -> boostTier = "<:Boost0:1209375787040641054> " + getLangMessage(event.getMember().getUser(),guild, "command.slash.serverInfo.boost.TierNotFound");
        }
        switch (guild.getVerificationLevel()) {
            case UNKNOWN -> verifLevel = getLangMessage(event.getMember().getUser(),guild, "command.slash.serverInfo.verificationLevel.unknown");
            case NONE -> verifLevel = getLangMessage(event.getMember().getUser(),guild, "command.slash.serverInfo.verificationLevel.none");
            case LOW -> verifLevel = getLangMessage(event.getMember().getUser(),guild, "command.slash.serverInfo.verificationLevel.low");
            case MEDIUM -> verifLevel = getLangMessage(event.getMember().getUser(),guild, "command.slash.serverInfo.verificationLevel.medium");
            case HIGH -> verifLevel = getLangMessage(event.getMember().getUser(),guild, "command.slash.serverInfo.verificationLevel.high");
            case VERY_HIGH -> verifLevel = getLangMessage(event.getMember().getUser(),guild, "command.slash.serverInfo.verificationLevel.veryHigh");
        }
        OffsetDateTime createTime = event.getGuild().getTimeCreated();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedCreateTime = createTime.format(formatter);


        EmbedBuilder embed = new EmbedBuilder();
        embed.setThumbnail(guild.getIconUrl());
        embed.setTitle(getLangMessage(event.getMember().getUser(),guild, "command.slash.serverInfo.embed.title").replace("%guildName%", guild.getName()));
        embed.setDescription(getLangMessage(event.getMember().getUser(),guild, "command.slash.serverInfo.embed.description")
                .replace("%createTime%", formattedCreateTime)
                .replace("%location%", guild.getLocale().getNativeName())
                .replace("%verifyLevel%", verifLevel)
                .replace("%boostTier%", boostTier)
                .replace("%boostCount%", guild.getBoostCount() + "")
                .replace("%boostRole%", "Error 512: Bad Gateway")
                .replace("%guildOwner%", guild.getOwner().getAsMention())
                .replace("%language%", getGuildInDatabase(guild).getLang()));
        embed.addField(getLangMessage(event.getMember().getUser(),guild, "command.slash.serverInfo.embed.field.members.title"), " "
                + getLangMessage(event.getMember().getUser(),guild, "command.slash.serverInfo.embed.field.members.description")
                .replace("%allUsers%", guild.getMemberCount() + "")
                .replace("%users%", users + "")
                .replace("%bots%", bots + ""), true);
        embed.addField(getLangMessage(event.getMember().getUser(),guild, "command.slash.serverInfo.embed.field.status.title"), " "
                + getLangMessage(event.getMember().getUser(),guild, "command.slash.serverInfo.embed.field.status.description")
                .replace("%onlineUsers%", onlineUsers + "")
                .replace("%idleUsers%", idleUsers + "")
                .replace("%dndUsers%", dndUsers + "")
                .replace("%offlineUsers%", offlineUsers + ""), true);
        embed.addField(getLangMessage(event.getMember().getUser(),guild, "command.slash.serverInfo.embed.field.channel.title"), " "
                + getLangMessage(event.getMember().getUser(),guild, "command.slash.serverInfo.embed.field.channel.description")
                .replace("%categoryChannels%", categoryChannels + "")
                .replace("%textChannels%", textChannels + "")
                .replace("%voiceChannels%", voiceChannels + "")
                .replace("%newsChannels%", newsChannels + "")
                .replace("%forumChannels%", forumChannels + "")
                .replace("%stageChannels%", stageChannels + ""), true);

        event.replyEmbeds(embed.build()).queue();
    }
}