package dev.prizrakk.commands.fun;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import dev.prizrakk.ICommand;

import java.util.ArrayList;
import java.util.List;

public class HttpCat implements ICommand {
    @Override
    public String getName() {
        return "httpcat";
    }

    @Override
    public String getDescription() {
        return "Кошки вместо кодов http";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.INTEGER,"httpcode","Сюда ввести возвратный http код например 100",true));
        return options;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        int httpCat = event.getOption("httpcode").getAsInt();
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Http кошка");
        embed.setImage("https://http.cat/" + httpCat);
        Button button = Button.link("https://http.cat/" + httpCat, "Ссылка на Http-кошку");
        event.replyEmbeds(embed.build())
                .setActionRow(button)
                .queue();
    }
}
