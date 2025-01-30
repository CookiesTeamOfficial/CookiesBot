package ru.dev.prizrakk.cookiesbot.web.guild;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import ru.dev.prizrakk.cookiesbot.web.ApiInterface;
import ru.dev.prizrakk.cookiesbot.web.WebEnum;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class GuildInfo implements ApiInterface {
    /**
     * getName() - Название страницы
     * getPath() - путь к странице
     * type() - тип get/post
     * execute() - тело кода
     */
    @Override
    public String getName() {
        return "GuildInfo Page";
    }

    @Override
    public String getDescription() {
        return "GuildInfo - Gives statistics about guild discord";
    }

    @Override
    public String getPath() {
        return "/guild/:guildId/info";
    }

    @Override
    public WebEnum type() {
        return WebEnum.GET;
    }
    private JDA jda;
    public GuildInfo(JDA jda) {
        this.jda = jda;
    }
    private Gson gson = new Gson();

    @Override
    public void execute(Request req, Response res) {
        String guildId = req.params(":guildId");
        long guildIdLong;

        try {
            guildIdLong = Long.parseLong(guildId);
        } catch (NumberFormatException e) {
            res.status(502);
            res.body(gson.toJson("Invalid guild ID format."));
            return;
        }

        Guild guild = jda.getGuildById(guildIdLong);

        if (guild == null) {
            res.status(404);
            res.body(gson.toJson("Guild not found."));
            return;
        }

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

        guild.loadMembers().onSuccess(members -> {
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




        Member owner = guild.getOwner();
        JsonObject chanel = new JsonObject();
        chanel.addProperty("text", textChannels);
        chanel.addProperty("voice", voiceChannels);
        chanel.addProperty("category", categoryChannels);
        chanel.addProperty("news", newsChannels);
        chanel.addProperty("forum", forumChannels);
        chanel.addProperty("stage", stageChannels);
        chanel.addProperty("all", (textChannels + voiceChannels + categoryChannels + newsChannels + forumChannels + stageChannels));
        JsonObject memberCount = new JsonObject();
        memberCount.addProperty("bot", bots);
        memberCount.addProperty("online", onlineUsers);
        memberCount.addProperty("idle", idleUsers);
        memberCount.addProperty("dnd", dndUsers);
        memberCount.addProperty("offline", offlineUsers);
        memberCount.addProperty("all", guild.getMemberCount());
        JsonObject ownerResponse = new JsonObject();
        ownerResponse.addProperty("id", guild.getOwnerId());
        ownerResponse.addProperty("name", owner.getNickname());
        ownerResponse.addProperty("effectiveName", owner.getEffectiveName());
        ownerResponse.addProperty("createdAt", String.valueOf(owner.getTimeCreated()));
        ownerResponse.addProperty("joinAt", String.valueOf(owner.getTimeJoined()));
        JsonObject guildResponse = new JsonObject();
        guildResponse.addProperty("guildId", String.valueOf(guildIdLong));
        guildResponse.addProperty("name", guild.getName());
        guildResponse.add("owner", ownerResponse);
        guildResponse.add("members", memberCount);
        guildResponse.add("chanel", chanel);
        guildResponse.addProperty("createAt", String.valueOf(guild.getTimeCreated()));
        guildResponse.addProperty("region", guild.getLocale().getLanguageName());
        guildResponse.addProperty("boostLevel", String.valueOf(guild.getBoostTier()));
        guildResponse.addProperty("boostCount", guild.getBoostCount());



        res.type("application/json");
        res.body(gson.toJson(guildResponse));
    }

}
