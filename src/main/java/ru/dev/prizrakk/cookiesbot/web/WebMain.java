package ru.dev.prizrakk.cookiesbot.web;

import ru.dev.prizrakk.cookiesbot.web.guild.GuildInfo;
import ru.dev.prizrakk.cookiesbot.web.utils.Home;

import java.util.logging.Level;
import java.util.logging.Logger;

import static spark.Spark.port;

public class WebMain {
    public static void initialize() {
        port(1111);
        //spark.sparkContext().setLogLevel("ERROR");

        // Создание и настройка ApiManager
        ApiManager apiManager = new ApiManager();
        Logger.getLogger("org").setLevel(Level.OFF);
        Logger.getLogger("akka").setLevel(Level.OFF);
        
        // Добавление API
        apiManager.addApi(new Home());
        apiManager.addApi(new GuildInfo());

        // Регистрация всех API
        apiManager.registerApis();
    }

}
