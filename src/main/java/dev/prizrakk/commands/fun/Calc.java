package dev.prizrakk.commands.fun;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import dev.prizrakk.ICommand;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Calc implements ICommand {
    @Override
    public String getName() {
        return "calc";
    }

    @Override
    public String getDescription() {
        return "Калькулятор beta-0.1";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING,"calc", "Строка калькулятора", true));
        return options;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String primer = event.getOption("calc").getAsString();
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        try {
            Object result = engine.eval(primer);
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Калькулятор");
            embed.setColor(Color.BLUE);
            embed.setDescription("Ващ результат: " + result);
            embed.setFooter("Версия калькулятора beta-0.1");
            event.replyEmbeds(embed.build()).queue();
        } catch (ScriptException e) {
            event.reply("Простите я не смог посчитать слишком сложно для меня(").queue();
        }
    }
}
