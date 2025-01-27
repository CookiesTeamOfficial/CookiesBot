package ru.dev.prizrakk.cookiesbot.web.guild;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import ru.dev.prizrakk.cookiesbot.web.ApiInterface;
import ru.dev.prizrakk.cookiesbot.web.WebEnum;
import spark.Request;
import spark.Response;

import java.awt.*;

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
        Member owner = guild.getOwner();
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
        guildResponse.addProperty("memberCount", guild.getMemberCount());
        guildResponse.addProperty("createAt", String.valueOf(guild.getTimeCreated()));
        guildResponse.addProperty("region", guild.getLocale().getLanguageName());
        guildResponse.addProperty("boostLevel", String.valueOf(guild.getBoostTier()));
        guildResponse.addProperty("boostCount", guild.getBoostCount());



        res.type("application/json");
        res.body(gson.toJson(guildResponse));
    }

}
