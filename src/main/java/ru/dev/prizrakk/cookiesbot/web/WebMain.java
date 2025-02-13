package ru.dev.prizrakk.cookiesbot.web;

import net.dv8tion.jda.api.JDA;
import ru.dev.prizrakk.cookiesbot.util.Utils;
import ru.dev.prizrakk.cookiesbot.web.guild.GuildInfo;
import ru.dev.prizrakk.cookiesbot.web.utils.Home;


import static spark.Spark.port;

public class WebMain extends Utils {
    public static void initialize(JDA jda) {
        port(Integer.parseInt(getConfig().getProperty("web-api.port")));
        // Создание и настройка ApiManager

        ApiManager apiManager = new ApiManager();
        // Добавление API
        apiManager.addApi(new Home(jda));
        apiManager.addApi(new GuildInfo(jda));

        // Регистрация всех API
        apiManager.registerApis();
    }

}
