package dev.prizrakk.commands.fun;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import dev.prizrakk.ICommand;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class Tyan implements ICommand {
    @Override
    public String getName() {
        return "tyan";
    }

    @Override
    public String getDescription() {
        return "Тянка :)";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Random random = new Random();
        int randomNumber = random.nextInt(100) + 1;
        String formattedNumber = String.format("%03d", randomNumber);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setImage("https://cdn.nekos.life/neko/neko_" + formattedNumber + ".jpg");
        embed.setColor(Color.GREEN);
        Button link = Button.link("https://cdn.nekos.life/neko/neko_" + formattedNumber + ".jpg", "Ссылка на сочную тян");
        event.replyEmbeds(embed.build())
                .setActionRow(link)
                .queue();
    }
}
