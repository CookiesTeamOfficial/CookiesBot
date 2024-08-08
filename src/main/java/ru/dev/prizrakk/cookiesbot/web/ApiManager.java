package ru.dev.prizrakk.cookiesbot.web;

import java.util.ArrayList;
import java.util.List;
import static spark.Spark.*;

import ru.dev.prizrakk.cookiesbot.util.Utils;

public class ApiManager extends Utils {
    private List<ApiInterface> apis;

    public ApiManager() {
        this.apis = new ArrayList<>();
    }

    private boolean apisRegistered = false;

    public void registerApis() {
        getLogger().debug("===================");
        getLogger().debug("Loading web api");
        getLogger().debug("===================");
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
                        throw new UnsupportedOperationException("Unsupported HTTP method: " + api.type());
                }
                getLogger().debug("API " + api.getName() + " зарегистрирована по пути: " + api.getPath());
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
