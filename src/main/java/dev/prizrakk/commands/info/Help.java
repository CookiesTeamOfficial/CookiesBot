package dev.prizrakk.commands.info;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import dev.prizrakk.ICommand;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Help implements ICommand {
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Показывает список команд";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }
    private List<ICommand> commands = new ArrayList<>();

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        EmbedBuilder embed = new EmbedBuilder();
        String help = "";
        embed.setTitle("Список доступных команд!");
        for(ICommand command : commands) {
            help = help + command.getName() + "\n" + command.getDescription() + "\n";
        }
        embed.setDescription(help);
        embed.setColor(Color.BLACK);
        embed.setFooter("Powered by prizrakk");
        event.replyEmbeds(embed.build()).queue();
    }
}
