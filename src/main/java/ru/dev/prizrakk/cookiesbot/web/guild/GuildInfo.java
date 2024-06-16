package ru.dev.prizrakk.cookiesbot.web.guild;

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

    @Override
    public void execute(Request req, Response res) {

        String guildId = req.params(":guildId");
        res.body("Info for guild: " + guildId);
    }
}
