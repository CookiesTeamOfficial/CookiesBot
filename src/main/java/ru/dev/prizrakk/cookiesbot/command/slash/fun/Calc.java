package ru.dev.prizrakk.cookiesbot.command.slash.fun;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.dev.prizrakk.cookiesbot.command.CommandCategory;
import ru.dev.prizrakk.cookiesbot.command.ICommand;
import ru.dev.prizrakk.cookiesbot.util.Utils;

import java.awt.Color;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calc extends Utils implements ICommand {
    private static final Map<String, DoubleUnaryOperator> functions = new HashMap<>();

    static {
        functions.put("cos", Math::cos);
        functions.put("sin", Math::sin);
        functions.put("tan", Math::tan);
        functions.put("acos", Math::acos);
        functions.put("asin", Math::asin);
        functions.put("atan", Math::atan);
    }

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
        return List.of(new OptionData(OptionType.STRING, "calc", "Арифметическое выражение для вычисления.", true));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.FUN;
    }

    @Override
    public List<Permission> getRequiredPermissions() {
        return List.of(Permission.MESSAGE_SEND);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws SQLException {
        String expression = event.getOption("calc").getAsString();
        try {
            double result = evaluate(expression);
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle(getLangMessage(event.getMember().getUser(), event.getGuild(), "command.slash.calc.embed.title"))
                    .setColor(Color.BLUE)
                    .setDescription(getLangMessage(event.getMember().getUser(), event.getGuild(), "command.slash.calc.embed.description").replace("%result%", String.valueOf(result)))
                    .setFooter(getLangMessage(event.getMember().getUser(), event.getGuild(), "command.slash.calc.embed.footer").replace("%version%", "v0.2-beta"));
            event.replyEmbeds(embed.build()).queue();
        } catch (Exception e) {
            getLogger().error("Error in calculation: ", e);
            event.reply(getLangMessage(event.getMember().getUser(), event.getGuild(), "command.slash.calc.error-message")).queue();
        }
    }

    private double evaluate(String expression) {
        expression = expression.replaceAll("\\s+", "").toLowerCase();
        expression = expression.replace("pi", String.valueOf(Math.PI)).replace("e", String.valueOf(Math.E));

        return parseExpression(expression);
    }

    private double parseExpression(String expr) {
        Stack<Double> values = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                StringBuilder numBuffer = new StringBuilder();
                while (i < expr.length() && (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.')) {
                    numBuffer.append(expr.charAt(i++));
                }
                i--;
                values.push(Double.parseDouble(numBuffer.toString()));
            } else if (c == '(') {
                operators.push(c);
            } else if (c == ')') {
                while (!operators.isEmpty() && operators.peek() != '(') {
                    values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                }
                operators.pop();
            } else if (isOperator(c)) {
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(c)) {
                    values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                }
                operators.push(c);
            } else {
                StringBuilder funcBuffer = new StringBuilder();
                while (i < expr.length() && Character.isLetter(expr.charAt(i))) {
                    funcBuffer.append(expr.charAt(i++));
                }
                i--;

                String functionName = funcBuffer.toString();
                if (functions.containsKey(functionName)) {
                    i++;
                    int j = i, brackets = 1;
                    while (j < expr.length() && brackets > 0) {
                        if (expr.charAt(j) == '(') brackets++;
                        if (expr.charAt(j) == ')') brackets--;
                        j++;
                    }
                    double arg = parseExpression(expr.substring(i, j - 1));
                    values.push(functions.get(functionName).apply(arg));
                    i = j - 1;
                }
            }
        }

        while (!operators.isEmpty()) {
            values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
        }
        return values.pop();
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
    }

    private int precedence(char operator) {
        return switch (operator) {
            case '+', '-' -> 1;
            case '*', '/' -> 2;
            case '^' -> 3; // Степень имеет самый высокий приоритет
            default -> -1;
        };
    }

    private double applyOperator(char operator, double b, double a) {
        return switch (operator) {
            case '+' -> a + b;
            case '-' -> a - b;
            case '*' -> a * b;
            case '/' -> a / b;
            case '^' -> Math.pow(a, b);
            default -> throw new IllegalArgumentException("Invalid operator: " + operator);
        };
    }


    @FunctionalInterface
    interface DoubleUnaryOperator {
        double apply(double value);
    }
}
