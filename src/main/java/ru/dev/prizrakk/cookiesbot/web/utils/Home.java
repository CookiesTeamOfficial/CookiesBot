package ru.dev.prizrakk.cookiesbot.web.utils;

import ru.dev.prizrakk.cookiesbot.web.ApiInterface;
import ru.dev.prizrakk.cookiesbot.web.WebEnum;
import spark.Request;
import spark.Response;

public class Home implements ApiInterface {
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

    @Override
    public void execute(Request req, Response res) {
        res.body("Hello World");
    }
}
