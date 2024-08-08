package ru.dev.prizrakk.cookiesbot.command.slash.fun;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.dev.prizrakk.cookiesbot.command.CommandCategory;
import ru.dev.prizrakk.cookiesbot.command.ICommand;
import ru.dev.prizrakk.cookiesbot.command.CommandStatus;
import ru.dev.prizrakk.cookiesbot.database.Database;
import ru.dev.prizrakk.cookiesbot.database.DatabaseUtils;
import ru.dev.prizrakk.cookiesbot.database.GuildVariable;
import ru.dev.prizrakk.cookiesbot.manager.LangManager;
import ru.dev.prizrakk.cookiesbot.util.Utils;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.awt.Color;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Calc extends Utils implements ICommand  {
    @Override
    public String getName() {
        return "calc";
    }

    @Override
    public String getDescription() {
        return "Вычисляет арифметическое выражение.";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "calc", "Арифметическое выражение для вычисления.", true));
        return options;
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.FUN;
    }

    @Override
    public CommandStatus getStatus() {
        return CommandStatus.OK;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws SQLException {
        String expression = event.getOption("calc").getAsString();

        // Проверка выражения с использованием регулярного выражения
        //if (!expression.matches("[0-9+\\-*/().\\s]*((cos|sin|tan|acos|asin|atan)\\(([^)]+)\\)|pi|e)*")) {
        //    event.reply("Неподдерживаемое выражение. Пожалуйста, используйте только цифры, операторы +, -, *, /, (), а также функции cos(), sin(), tan(), acos(), asin(), atan() и константы pi, e.").queue();
        //    return;
        //}

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");

        // Проверка, что движок JavaScript доступен
        if (engine == null) {
            event.reply(getLangMessage(event.getGuild(), "command.slash.calc.errorEngine.message")).queue();
            return;
        }

        // Добавление предопределённых значений и функций
        Bindings bindings = engine.createBindings();
        bindings.put("pi", Math.PI);
        bindings.put("e", Math.E);
        bindings.put("cos", (TrigFunction) Math::cos);
        bindings.put("sin", (TrigFunction) Math::sin);
        bindings.put("tan", (TrigFunction) Math::tan);
        bindings.put("acos", (TrigFunction) Math::acos);
        bindings.put("asin", (TrigFunction) Math::asin);
        bindings.put("atan", (TrigFunction) Math::atan);

        try {
            Object result = engine.eval(expression, bindings);
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle(getLangMessage(event.getGuild(), "command.slash.calc.title.message"));
            embed.setColor(Color.BLUE);
            embed.setDescription(getLangMessage(event.getGuild(), "command.slash.calc.description.message").replace("%result%", result.toString()));
            embed.setFooter(getLangMessage(event.getGuild(), "command.slash.calc.footer.message"));
            event.replyEmbeds(embed.build()).queue();
        } catch (ScriptException e) {
            getLogger().error("", e);
            event.reply(getLangMessage(event.getGuild(), "command.slash.calc.error.message")).queue();
        }
    }

    @FunctionalInterface
    interface TrigFunction {
        double apply(double value);
    }
}
