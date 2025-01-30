package ru.dev.prizrakk.cookiesbot.web;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import net.dv8tion.jda.api.JDA;
import org.slf4j.LoggerFactory;
import ru.dev.prizrakk.cookiesbot.web.guild.GuildInfo;
import ru.dev.prizrakk.cookiesbot.web.utils.Home;


import static spark.Spark.port;

public class WebMain {
    public static void initialize(JDA jda) {

        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        // Отключаем логи JDA полностью
        loggerContext.getLogger("net.dv8tion.jda").setLevel(Level.OFF);
        loggerContext.getLogger("net.dv8tion.jda.internal.requests.Requester").setLevel(Level.OFF);
        loggerContext.getLogger("net.dv8tion.jda.api.requests.RestRateLimiter").setLevel(Level.OFF);
        loggerContext.getLogger("net.dv8tion.jda.api.JDA").setLevel(Level.OFF);
        loggerContext.getLogger("net.dv8tion.jda.api.utils.SessionControllerAdapter").setLevel(Level.OFF);

        // Отключаем Spark и Jetty
        loggerContext.getLogger("spark").setLevel(Level.OFF);
        loggerContext.getLogger("org.eclipse.jetty").setLevel(Level.OFF);
        port(1111);
        // Создание и настройка ApiManager

        ApiManager apiManager = new ApiManager();
        // Добавление API
        apiManager.addApi(new Home());
        apiManager.addApi(new GuildInfo(jda));

        // Регистрация всех API
        apiManager.registerApis();
    }

}
