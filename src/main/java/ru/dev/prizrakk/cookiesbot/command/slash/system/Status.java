package ru.dev.prizrakk.cookiesbot.command.slash.system;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.dev.prizrakk.cookiesbot.command.CommandCategory;
import ru.dev.prizrakk.cookiesbot.command.ICommand;
import ru.dev.prizrakk.cookiesbot.command.CommandStatus;
import ru.dev.prizrakk.cookiesbot.util.Utils;

import java.awt.Color;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.sql.SQLException;
import java.util.List;

public class Status extends Utils implements ICommand {
    @Override
    public String getName() {
        return "status";
    }

    @Override
    public String getDescription() {
        return "Показывает текущую нагрузку бота и статистику.";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.OTHER;
    }

    @Override
    public CommandStatus getStatus() {
        return CommandStatus.OK;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws SQLException {
        JDA jda = event.getJDA();

        // Пинг до Discord API
        long gatewayPing = jda.getGatewayPing();

        // Нагрузка на систему
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        int availableProcessors = osBean.getAvailableProcessors();
        double systemLoad = osBean.getSystemLoadAverage();
        if (systemLoad < 0) {
            systemLoad = 0;
        } else {
            systemLoad = systemLoad / availableProcessors;
        }

        // Память
        long freeMemory = Runtime.getRuntime().freeMemory() / 1024 / 1024;
        long totalMemory = Runtime.getRuntime().totalMemory() / 1024 / 1024;
        long maxMemory = Runtime.getRuntime().maxMemory() / 1024 / 1024;
        double memoryUsage = (double) (totalMemory - freeMemory) / maxMemory;

        // Статистика серверов и пользователей
        int guildCount = jda.getGuilds().size();
        long userCount = jda.getUsers().stream().distinct().count();

        // Формирование прогресс-бара
        String cpuProgressBar = getProgressBar(systemLoad);
        String memoryProgressBar = getProgressBar(memoryUsage);

        // Создание Embed
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(getLangMessage(event.getGuild(), "command.slash.status.embed.title.message"));
        embed.setColor(Color.GREEN);

        embed.addField(getLangMessage(event.getGuild(), "command.slash.status.embed.field.ping.title.message"), gatewayPing + " ms", true);
        embed.addField(getLangMessage(event.getGuild(), "command.slash.status.embed.field.cpuLoad.title.message"), String.format("%.2f%% %s", systemLoad * 100, cpuProgressBar), true);
        embed.addField(getLangMessage(event.getGuild(), "command.slash.status.embed.field.ramLoad.title.message"), String.format("%.2f%% %s", memoryUsage * 100, memoryProgressBar), true);
        embed.addField(getLangMessage(event.getGuild(), "command.slash.status.embed.field.ramFree.title.message"), freeMemory + " MB", true);
        embed.addField(getLangMessage(event.getGuild(), "command.slash.status.embed.field.ramUsed.title.message"), (totalMemory - freeMemory) + " MB", true);
        embed.addField(getLangMessage(event.getGuild(), "command.slash.status.embed.field.ramAll.title.message"), totalMemory + " MB", true);
        embed.addField(getLangMessage(event.getGuild(), "command.slash.status.embed.field.ramMax.title.message"), maxMemory + " MB", true);
        embed.addField(getLangMessage(event.getGuild(), "command.slash.status.embed.field.serverCount.title.message"), String.valueOf(guildCount), true);
        embed.addField(getLangMessage(event.getGuild(), "command.slash.status.embed.field.userCount.title.message"), String.valueOf(userCount), true);

        event.replyEmbeds(embed.build()).queue();
    }

    private String getProgressBar(double value) {
        int totalBars = 10;
        int filledBars = (int) (value * totalBars);
        int emptyBars = totalBars - filledBars;
    StringBuilder progressBar = new StringBuilder();
        for (int i = 0; i < filledBars; i++) {
            progressBar.append("▰");
        }
        for (int i = 0; i < emptyBars; i++) {
            progressBar.append("▱");
        }

        return progressBar.toString();
    }
}
