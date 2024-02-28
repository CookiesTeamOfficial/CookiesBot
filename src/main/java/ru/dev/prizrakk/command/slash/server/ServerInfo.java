package ru.dev.prizrakk.command.slash.server;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.dev.prizrakk.util.Config;
import ru.dev.prizrakk.ICommand;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerInfo implements ICommand {
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

    Config config = new Config();

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
                case ONLINE:
                    onlineUsers++;
                    break;
                case IDLE:
                    idleUsers++;
                    break;
                case DO_NOT_DISTURB:
                    dndUsers++;
                    break;
                case OFFLINE:
                    offlineUsers++;
                    break;
                default:
                    break;
            }
        }
        for(Category category : categories) {
            for(GuildChannel channel : category.getChannels()) {
                // Определяем тип канала и увеличиваем счетчик
                switch(channel.getType()) {
                    case TEXT:
                        textChannels++;
                        break;
                    case VOICE:
                        voiceChannels++;
                        break;
                    case CATEGORY:
                        categoryChannels++;
                        break;
                    case NEWS:
                        newsChannels++;
                        break;
                    case FORUM:
                        forumChannels++;
                        break;
                    case STAGE:
                        stageChannels++;
                        break;
                }
            }
        }

        switch (event.getGuild().getBoostTier()) {
            case NONE:
                boostTier = "<:Boost0:1209375787040641054> Буст уровень: **Отсутствует**";
                break;
            case TIER_1:
                boostTier = "<:Boost1:1209375792685907978> Буст уровень: **1**";
                break;
            case TIER_2:
                boostTier = "<:Boost2Dark:1209375790459002880> Буст уровень: **2**";
                break;
            case TIER_3:
                boostTier = "<:Boost3Dark:1209375788739076136> Буст уровень: **3**";
                break;
            case UNKNOWN:
                boostTier = "<:Boost0:1209375787040641054> Буст уровень: **Не опознан! Свяжитесь с разработчиком бота!**";
                break;
            default:
                boostTier = "<:Boost0:1209375787040641054> Буст уровень: **Бип бип буп буп**";
                break;
        }
        switch (guild.getVerificationLevel()) {
            case UNKNOWN:
                verifLevel = "Не опознан";
                break;
            case NONE:
                verifLevel = "Отсутствует";
                break;
            case LOW:
                verifLevel = "Минимальный";
                break;
            case MEDIUM:
                verifLevel = "Оптимальный";
                break;
            case HIGH:
                verifLevel = "Максимальный";
                break;
            case VERY_HIGH:
                verifLevel = "БОЖЕСТВЕНЫЙ!!!!!";
                break;
        }
        OffsetDateTime createTime = event.getGuild().getTimeCreated();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedCreateTime = createTime.format(formatter);


        EmbedBuilder embed = new EmbedBuilder();
        embed.setThumbnail(guild.getIconUrl());
        embed.setTitle("Информация о " + guild.getName());
        embed.setDescription("<:DateTime:1209378875293962240>Дата создания: **" + formattedCreateTime + "**"
                + "\n" + "<:location:1209386794513203241> Локация: **" + guild.getLocale().getLanguageName() + "**"
                + "\n" + "<:Security:1209378873972891648>Уровень безопастности: **" + verifLevel + "**"
                + "\n" + boostTier
                + "\n" + "<:1Month:1209386796077678592> Количество бустов: **" + guild.getBoostCount() + "**"
                + "\n" + "<:6Months:1209386799231926302> Роль буста: **" + "Error 512: Bad Gateway" + "**"
                + "\n" + "<:Owner:1209394164316373043> Создатель: " + guild.getOwner().getAsMention());
        embed.addField("Участники: ", " "
                + "\n" + ":busts_in_silhouette: Всего: **" + guild.getMemberCount() + "**"
                + "\n" + ":bust_in_silhouette: Людей: **" + users + "**"
                + "\n" + ":robot: Боты: **" + bots + "**", true);
        embed.addField("По статусам", " "
                + "\n" + "<:Online:1209207268281942047> Онлайн: **" + onlineUsers + "**"
                + "\n" + "<:Idle:1209207266620866620> Неактивны: **" + idleUsers + "**"
                + "\n" + "<:Dnd:1209207263802171402> Не беспокоить: **" + dndUsers + "**"
                + "\n" + "<:Offline:1209207265165316187> Офлайн: **" + offlineUsers + "**", true);
        embed.addField("Каналы", " "
                + "\n" + "<:CategoryMobile:1209375825980428328> Категории: **" + categoryChannels + "**"
                + "\n" + "<:Text:1209198697297092618> Текстовые: **" + textChannels + "**"
                + "\n" + "<:Voice:1209198695619493999> Голосовые: **" + voiceChannels + "**"
                + "\n" + "<:News:1209198692058271794> Новостные: **" + newsChannels + "**"
                + "\n" + "<:Forum:1209198698702049311> Форумы: **" + forumChannels + "**"
                + "\n" + "<:Stage:1209198694331846676> Stage: **" + stageChannels + "**", true);
        embed.setFooter(config.years_author);
        event.replyEmbeds(embed.build()).queue();
    }
}
