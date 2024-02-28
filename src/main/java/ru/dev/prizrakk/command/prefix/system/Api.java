package ru.dev.prizrakk.command.prefix.system;

import com.google.gson.Gson;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import ru.dev.prizrakk.manager.LoggerManager;
import ru.dev.prizrakk.util.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Api extends ListenerAdapter {

    Config config = new Config();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String[] message = event.getMessage().getContentRaw().split(" ", 2);

// Проверяем, что сообщение начинается с вашего префикса
        if (message[0].startsWith("!")) { // Замените "!" на ваш префикс
            String command = message[0].substring(1).toLowerCase(); // Убираем префикс и приводим к нижнему регистру
            String[] args = message.length > 1 ? message[1].split(" ") : new String[0];
            switch (command) {
                case "kawai":
                    if (event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {
                        CloseableHttpClient httpClient = HttpClients.createDefault();
                        HttpGet request = new HttpGet("https://kawaii.red/api/stats/most_endpoints/token=579683756789727243.a3ceICmznU290G75nVIQ");

                        try (CloseableHttpResponse response = httpClient.execute(request)) {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                            StringBuilder result = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                result.append(line);
                            }

                            // Парсим JSON-ответ
                            Gson gson = new Gson();
                            ResponseObject responseObject = gson.fromJson(result.toString(), ResponseObject.class);

                            // Создаем EmbedBuilder и добавляем поля
                            EmbedBuilder embed = new EmbedBuilder();
                            embed.setTitle("Статистика по GIF-ам");

                            for (ResponseItem item : responseObject.getResponse()) {
                                embed.addField(item.getName(), String.valueOf(item.getValue()), true);
                            }

                            // Отправляем Embed в канал
                            event.getChannel().sendMessageEmbeds(embed.build()).queue();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setTitle("Error 403: FORBIDDEN");
                        embed.setDescription("Прошу прощения у вас нет прав на выполнение этой команды");
                        Config config = new Config();
                        embed.setFooter(config.years_author);
                        event.getChannel().sendMessageEmbeds(embed.build()).queue();

                    }
                    break;
                case "services":
                    if (event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setTitle("Информация о сайтах!");
                    embed.addField("Основной сайт", checkUri("https://cookiesland.net"), true);
                    embed.addField("Форум", checkUri("https://forum.cookiesland.net"), true);
                    embed.addField("Админ панель", checkUri("https://panel.dev-prizrakk.ru"), true);
                    embed.addField("Example.com", checkUri("https://example.com"), true);
                    embed.setFooter(config.years_author);
                    event.getChannel().sendMessageEmbeds(embed.build()).queue();
                    break;
                    } else {
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setTitle("Error 403: FORBIDDEN");
                        embed.setDescription("Прошу прощения у вас нет прав на выполнение этой команды");
                        Config config = new Config();
                        embed.setFooter(config.years_author);
                        event.getChannel().sendMessageEmbeds(embed.build()).queue();

                    }

            }
        }
    }
    String checkUri(String url) {
        boolean check = false;
        String site = null;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            HttpResponse response = httpClient.execute(request);

            int statusCode = response.getStatusLine().getStatusCode();


            check = statusCode == 200;
        } catch (IOException e) {
            LoggerManager log = new LoggerManager();
            log.error("Произошла ошибка при выполнение действия checkUri \n StarkTrace: \n " + e.getMessage());
            check = false;
        }
        if(check) {
            site = "<:Online:1209207268281942047> Онлайн";
        } else {
            site = "<:Dnd:1209207263802171402> Не работает \n";
        }
        return site;
    }
    // Класс, соответствующий структуре вашего JSON-ответа
    static class ResponseObject {
        private ResponseItem[] response;

        public ResponseItem[] getResponse() {
            return response;
        }
    }

    static class ResponseItem {
        private String name;
        private int value;

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }
    }
}