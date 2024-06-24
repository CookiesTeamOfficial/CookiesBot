package ru.dev.prizrakk.cookiesbot.command.slash.server;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.dev.prizrakk.cookiesbot.command.CommandCategory;
import ru.dev.prizrakk.cookiesbot.command.CommandStatus;
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
    public CommandStatus getStatus() {
        return CommandStatus.OK;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws SQLException {
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
        String boostTier = null;
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
                // Определяем тип канала и увеличиваем счетчик
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
            case NONE -> boostTier = "<:Boost0:1209375787040641054> " + getLangMessage(guild, "command.slash.serverInfo.boostTier0.message");
            case TIER_1 -> boostTier = "<:Boost1:1209375792685907978> " + getLangMessage(guild, "command.slash.serverInfo.boostTier1.message");
            case TIER_2 -> boostTier = "<:Boost2Dark:1209375790459002880> " + getLangMessage(guild, "command.slash.serverInfo.boostTier2.message");
            case TIER_3 -> boostTier = "<:Boost3Dark:1209375788739076136> " + getLangMessage(guild, "command.slash.serverInfo.boostTier3.message");
            case UNKNOWN -> boostTier = "<:Boost0:1209375787040641054> " + getLangMessage(guild, "command.slash.serverInfo.boostTierUnknown.message");
            default -> boostTier = "<:Boost0:1209375787040641054> " + getLangMessage(guild, "command.slash.serverInfo.boostTierNotFound.message");
        }
        switch (guild.getVerificationLevel()) {
            case UNKNOWN -> verifLevel = getLangMessage(guild, "command.slash.serverInfo.verificationLevel.unknown.message");
            case NONE -> verifLevel = getLangMessage(guild, "command.slash.serverInfo.verificationLevel.none.message");
            case LOW -> verifLevel = getLangMessage(guild, "command.slash.serverInfo.verificationLevel.low.message");
            case MEDIUM -> verifLevel = getLangMessage(guild, "command.slash.serverInfo.verificationLevel.medium.message");
            case HIGH -> verifLevel = getLangMessage(guild, "command.slash.serverInfo.verificationLevel.high.message");
            case VERY_HIGH -> verifLevel = getLangMessage(guild, "command.slash.serverInfo.verificationLevel.veryHigh.message");
        }
        OffsetDateTime createTime = event.getGuild().getTimeCreated();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedCreateTime = createTime.format(formatter);


        EmbedBuilder embed = new EmbedBuilder();
        embed.setThumbnail(guild.getIconUrl());
        embed.setTitle(getLangMessage(guild, "command.slash.serverInfo.embed.title.message").replace("%guildName%", guild.getName()));
        embed.setDescription(getLangMessage(guild, "command.slash.serverInfo.embed.description.message")
                .replace("%createTime%", formattedCreateTime)
                .replace("%location%", guild.getLocale().getLanguageName())
                .replace("%verifyLevel%", verifLevel)
                .replace("%boostTier%", boostTier)
                .replace("%boostCount%", guild.getBoostCount() + "")
                .replace("%boostRole%", "Error 512: Bad Gateway")
                .replace("%guildOwner%", guild.getOwner().getAsMention())
                .replace("%language%", getGuildOnSlash(guild).getLang()));
        embed.addField(getLangMessage(guild, "command.slash.serverInfo.embed.field.title.members.message"), " "
                + getLangMessage(guild, "command.slash.serverInfo.embed.field.description.members.message")
                .replace("%allUsers%", guild.getMemberCount() + "")
                .replace("%users%", users + "")
                .replace("%bots%", bots + ""), true);
        embed.addField(getLangMessage(guild, "command.slash.serverInfo.field.title.status.message"), " "
                + getLangMessage(guild, "command.slash.serverInfo.field.description.status.message")
                .replace("%onlineUsers%", onlineUsers + "")
                .replace("%idleUsers%", idleUsers + "")
                .replace("%dndUsers%", dndUsers + "")
                .replace("%offlineUsers%", offlineUsers + ""), true);
        embed.addField(getLangMessage(guild, "command.slash.serverInfo.field.title.channel.message"), " "
                + getLangMessage(guild, "command.slash.serverInfo.field.description.channel.message")
                .replace("%categoryChannels%", categoryChannels + "")
                .replace("%textChannels%", textChannels + "")
                .replace("%voiceChannels%", voiceChannels + "")
                .replace("%newsChannels%", newsChannels + "")
                .replace("%forumChannels%", forumChannels + "")
                .replace("%stageChannels%", stageChannels + ""), true);

        event.replyEmbeds(embed.build()).queue();
    }
}