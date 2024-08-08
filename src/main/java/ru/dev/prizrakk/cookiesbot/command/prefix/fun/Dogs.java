package ru.dev.prizrakk.cookiesbot.command.prefix.fun;

import com.google.gson.Gson;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import ru.dev.prizrakk.cookiesbot.database.Database;
import ru.dev.prizrakk.cookiesbot.database.DatabaseUtils;
import ru.dev.prizrakk.cookiesbot.database.GuildVariable;
import ru.dev.prizrakk.cookiesbot.manager.LangManager;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Scanner;

import static ru.dev.prizrakk.cookiesbot.util.Utils.getLogger;

public class Dogs extends ListenerAdapter {

    private DatabaseUtils databaseUtils;

    public Dogs(Database database) {
        this.databaseUtils = new DatabaseUtils(database);
    }
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        GuildVariable guildVariable;
        try {
            guildVariable = databaseUtils.getGuildFromDatabase(event.getGuild());
        } catch (SQLException e) {
            getLogger().error("", e);
            return;
        }
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
                embed.setTitle(LangManager.getMessage(guildVariable.getLang(),"command.prefix.dogs.message"));
                embed.setImage(imageUrl);

                Button button = Button.link(imageUrl, LangManager.getMessage(guildVariable.getLang(),"command.prefix.dogs.button.message"));
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
