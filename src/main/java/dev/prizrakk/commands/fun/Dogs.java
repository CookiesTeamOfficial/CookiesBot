package dev.prizrakk.commands.fun;

import com.google.gson.Gson;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import dev.prizrakk.ICommand;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

public class Dogs implements ICommand {
    @Override
    public String getName() {
        return "dogs";
    }

    @Override
    public String getDescription() {
        return "Рандомное фото собаки";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
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
        event.replyEmbeds(embed.build())
                .setActionRow(button)
                .queue();
    }
    public static class DogApiResponse {
        private String message;
        public String getMessage() {
            return message;
        }
    }
}
