package ru.dev.prizrakk.cookiesbot.web;

import spark.Request;
import spark.Response;

public interface ApiInterface {
    String getName();
    String getDescription();
    String getPath();
    WebEnum type();
    void execute(Request req, Response res);
    default String getTemplatePath() {
        return getPath();
    }
}
