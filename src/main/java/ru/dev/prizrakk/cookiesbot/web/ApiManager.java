package ru.dev.prizrakk.cookiesbot.web;

import java.util.ArrayList;
import java.util.List;
import static spark.Spark.*;

import ru.dev.prizrakk.cookiesbot.manager.ColorManager;
import ru.dev.prizrakk.cookiesbot.util.Utils;

public class ApiManager extends Utils {
    private List<ApiInterface> apis;

    public ApiManager() {
        this.apis = new ArrayList<>();
    }

    private boolean apisRegistered = false;

    public void registerApis() {
        getLogger().info(ColorManager.ANSI_BLUE + "===================");
        getLogger().info("Loading web api");
        getLogger().info(ColorManager.ANSI_BLUE + "===================");
        getLogger().info("Load on http://localhost:"+ getConfig().getProperty("web-api.port")  +"/");
        if (!apisRegistered) {
            for (ApiInterface api : this.apis) {
                switch (api.type()) {
                    case GET:
                        get(api.getPath(), (req, res) -> {
                            api.execute(req, res);
                            return res.body();
                        });
                        break;
                    case POST:
                        post(api.getPath(), (req, res) -> {
                            api.execute(req, res);
                            return res.body();
                        });
                        break;
                    default:
                        UnsupportedOperationException e = new UnsupportedOperationException();
                        getLogger().error("Unsupported HTTP method: " + api.type(), e);
                }
                getLogger().info("API " + api.getName() + " registered on the way: " + api.getPath());
            }
            notFound((req, res) -> {
                res.type("application/json");
                return "{\"message\":\"404 - Not Found\"}";
            });
            apisRegistered = true; // Устанавливаем флаг после регистрации API
        }
    }

    public void addApi(ApiInterface api) {
        this.apis.add(api);
    }
}
