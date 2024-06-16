package ru.dev.prizrakk.cookiesbot.command.prefix.fun;

import com.google.gson.Gson;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Dogs extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String[] message = event.getMessage().getContentRaw().split(" ", 2);

        // Проверяем, что сообщение начинается с вашего префикса
        if (message[0].startsWith("!")) { // Замените "!" на ваш префикс
            String command = message[0].substring(1).toLowerCase(); // Убираем префикс и приводим к нижнему регистру
            String[] args = message.length > 1 ? message[1].split(" ") : new String[0];

            // Обработка команды
            if (command.equals("dogs")) {
                String jsonUrl = "https://dog.ceo/api/breeds/image/random";
                String imageUrl = null;

                try {
                    // Получение JSON-строки с сайта
                    String jsonString = new Scanner(new URL(jsonUrl).openStream(), String.valueOf(StandardCharsets.UTF_8)).useDelimiter("\\A").next();

                    // Создание объекта Gson
                    Gson gson = new Gson();

                    // Извлечение значения из message
                    DogApiResponse response = gson.fromJson(jsonString, DogApiResponse.class);
                    imageUrl = response.getMessage();


                } catch (IOException e) {
                    e.printStackTrace();
                }
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(new Color(0, 0, 0));
                embed.setTitle("Вот ваша собачка :)");
                embed.setImage(imageUrl);

                Button button = Button.link(imageUrl, "Ссылка на собаку");
                event.getChannel().sendMessageEmbeds(embed.build()).setActionRow(button).queue();
            }
        }
    }
    public static class DogApiResponse {
        private String message;
        public String getMessage() {
            return message;
        }
    }
}
