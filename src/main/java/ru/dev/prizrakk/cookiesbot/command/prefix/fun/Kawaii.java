package ru.dev.prizrakk.cookiesbot.command.prefix.fun;

import com.google.gson.Gson;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import ru.dev.prizrakk.cookiesbot.util.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Kawaii extends ListenerAdapter {
    Config config = new Config();
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String[] message = event.getMessage().getContentRaw().split(" ", 2);

// Проверяем, что сообщение начинается с вашего префикса
        if (message[0].startsWith("!")) { // Замените "!" на ваш префикс
            String command = message[0].substring(1).toLowerCase(); // Убираем префикс и приводим к нижнему регистру
            String[] args = message.length > 1 ? message[1].split(" ") : new String[0];

            // Обработка команды
            switch (command) {
                case "hug" :
                    EmbedBuilder hug = new EmbedBuilder();
                    if (args.length == 0) {
                        hug.setTitle("Обнял(-а) самого себя!");
                    } else {
                        // Проверяем, содержится ли упоминание пользователя в аргументах команды
                        if (!event.getMessage().getContentDisplay().contains("@")) {
                            event.getChannel().sendMessage("Укажите пользователя правильно!").queue(); // Выводим ошибку
                            return;
                        }
                        // Извлекаем упоминание пользователя из аргументов команды
                        String[] mentionedUsers = event.getMessage().getContentDisplay().split("@");
                        String huggedUser = mentionedUsers[1].split(" ")[0]; // Получаем первое упоминание пользователя
                        hug.setTitle("Обнял(-а) " + huggedUser + "!");
                    }
                    hug.setImage(apiLoad("hug"));
                    hug.setFooter(config.years_author);
                    event.getChannel().sendMessageEmbeds(hug.build()).queue();
                    break;
                case "kiss" :
                    EmbedBuilder kiss = new EmbedBuilder();
                    if (args.length == 0) {
                        kiss.setTitle("Поцеловал(-а) себя (Чевоо блять????)");
                    } else {
                        // Проверяем, содержится ли упоминание пользователя в аргументах команды
                        if (!event.getMessage().getContentDisplay().contains("@")) {
                            event.getChannel().sendMessage("Укажите пользователя правильно!").queue(); // Выводим ошибку
                            return;
                        }
                        // Извлекаем упоминание пользователя из аргументов команды
                        String[] mentionedUsers = event.getMessage().getContentDisplay().split("@");
                        String huggedUser = mentionedUsers[1].split(" ")[0]; // Получаем первое упоминание пользователя
                        kiss.setTitle("Поцеловал(-а) " + huggedUser + "!");
                    }
                    kiss.setImage(apiLoad("kiss"));
                    kiss.setFooter(config.years_author);
                    event.getChannel().sendMessageEmbeds(kiss.build()).queue();
                    break;
                case "kill" :
                    EmbedBuilder kill = new EmbedBuilder();
                    if (args.length == 0) {
                        kill.setTitle("Убил(-а) самого себя!");
                    } else {
                        // Проверяем, содержится ли упоминание пользователя в аргументах команды
                        if (!event.getMessage().getContentDisplay().contains("@")) {
                            event.getChannel().sendMessage("Укажите пользователя правильно!").queue(); // Выводим ошибку
                            return;
                        }
                        // Извлекаем упоминание пользователя из аргументов команды
                        String[] mentionedUsers = event.getMessage().getContentDisplay().split("@");
                        String huggedUser = mentionedUsers[1].split(" ")[0]; // Получаем первое упоминание пользователя
                        kill.setTitle("Убил(-а) " + huggedUser + "!");
                    }
                    kill.setImage(apiLoad("kill"));
                    kill.setFooter(config.years_author);
                    event.getChannel().sendMessageEmbeds(kill.build()).queue();
                    break;
                case "dead" :
                    EmbedBuilder wasted = new EmbedBuilder();
                    if (args.length == 0) {
                        wasted.setTitle("Умер(-ла) от депресии!");
                    } else {
                        // Проверяем, содержится ли упоминание пользователя в аргументах команды
                        if (!event.getMessage().getContentDisplay().contains("@")) {
                            event.getChannel().sendMessage("Укажите пользователя правильно!").queue(); // Выводим ошибку
                            return;
                        }
                        // Извлекаем упоминание пользователя из аргументов команды
                        String[] mentionedUsers = event.getMessage().getContentDisplay().split("@");
                        String huggedUser = mentionedUsers[1].split(" ")[0]; // Получаем первое упоминание пользователя
                        wasted.setTitle("Умер(-ла) из-за " + huggedUser + "!");
                    }
                    wasted.setImage(apiLoad("wasted"));
                    wasted.setFooter(config.years_author);
                    event.getChannel().sendMessageEmbeds(wasted.build()).queue();
                    break;
                case "cry" :
                    EmbedBuilder cry = new EmbedBuilder();
                    if (args.length == 0) {
                        cry.setTitle("Расплакалася(-ась)!");
                    } else {
                        // Проверяем, содержится ли упоминание пользователя в аргументах команды
                        if (!event.getMessage().getContentDisplay().contains("@")) {
                            event.getChannel().sendMessage("Укажите пользователя правильно!").queue(); // Выводим ошибку
                            return;
                        }
                        // Извлекаем упоминание пользователя из аргументов команды
                        String[] mentionedUsers = event.getMessage().getContentDisplay().split("@");
                        String huggedUser = mentionedUsers[1].split(" ")[0]; // Получаем первое упоминание пользователя
                        cry.setTitle("Расплакался(-ась) из-за " + huggedUser + "!");
                    }
                    cry.setImage(apiLoad("cry"));
                    cry.setFooter(config.years_author);
                    event.getChannel().sendMessageEmbeds(cry.build()).queue();
                    break;
            }
        }
    }
    public String apiLoad(String mode) {
        String responseValue = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet("https://kawaii.red/api/gif/" + mode + "/token=579683756789727243.a3ceICmznU290G75nVIQ");

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

            // Получаем значение переменной "response"
            responseValue = responseObject.getResponse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseValue;
    }
    static class ResponseObject {
        private String response;

        public String getResponse() {
            return response;
        }
    }
}
