package ru.dev.prizrakk.cookiesbot.web.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.JDA;
import ru.dev.prizrakk.cookiesbot.Main;
import ru.dev.prizrakk.cookiesbot.web.ApiInterface;
import ru.dev.prizrakk.cookiesbot.web.WebEnum;
import spark.Request;
import spark.Response;

public class Home implements ApiInterface {
    private JDA jda;
    public Home(JDA jda) {
        this.jda = jda;
    }
    /**
     * getName() - Название страницы
     * getPath() - путь к странице
     * type() - тип get/post
     * execute() - тело кода
     */
    @Override
    public String getName() {
        return "Home Page";
    }

    @Override
    public String getDescription() {
        return "Welcome home page Api CookiesBot";
    }

    @Override
    public String getPath() {
        return "/";
    }

    @Override
    public WebEnum type() {
        return WebEnum.GET;
    }
    private Gson gson = new Gson();

    @Override
    public void execute(Request req, Response res) {
        JsonObject response = new JsonObject();
        response.addProperty("name", "CookiesBot");
        response.addProperty("version", Main.currentVersion);
        response.addProperty("servers", jda.getGuilds().stream().count());
        response.addProperty("users", jda.getUsers().stream().distinct().count());
        res.type("application/json");
        res.body(gson.toJson(response));
    }
}
