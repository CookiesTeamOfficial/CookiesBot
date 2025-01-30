package ru.dev.prizrakk.cookiesbot.command;

import java.awt.*;
import java.sql.SQLException;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.text.SimpleDateFormat;
import java.util.Collection;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import java.util.ArrayList;

import java.util.Date;
import java.util.List;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ru.dev.prizrakk.cookiesbot.database.Database;
import ru.dev.prizrakk.cookiesbot.database.DatabaseUtils;
import ru.dev.prizrakk.cookiesbot.database.GuildVariable;
import ru.dev.prizrakk.cookiesbot.manager.LangManager;

import static ru.dev.prizrakk.cookiesbot.util.Utils.getLogger;

public class CommandManager extends ListenerAdapter {
    private static DatabaseUtils databaseUtils;

    public CommandManager(Database database) {
        this.databaseUtils = new DatabaseUtils(database);
    }
    public static List<ICommand> commands;

    public CommandManager() {
        this.commands = new ArrayList<ICommand>();
    }

    private boolean commandsRegistered = false;

    @Override
    public void onReady(@NotNull final ReadyEvent event) {
        if (!commandsRegistered) {
            for (final Guild guild : event.getJDA().getGuilds()) {
                for (final ICommand command : this.commands) {
                    if (command.getOptions() == null) {
                        guild.upsertCommand(command.getName(), command.getDescription()).queue();
                    } else {
                        guild.upsertCommand(command.getName(), command.getDescription()).addOptions((Collection<? extends OptionData>)command.getOptions()).queue();
                    }
                    //TODO: FIX
                    //new LoggerManager(LoggerEnum.DEBUG, "Команда " + command.getName() + " была загружена!");
                }
            }
            commandsRegistered = true; // Устанавливаем флаг после регистрации команд
        }
    }

    @Override
    public void onGuildJoin(final GuildJoinEvent event) {
        for (final Guild guild : event.getJDA().getGuilds()) {
            for (final ICommand command : this.commands) {
                if (command.getOptions() == null) {
                    guild.upsertCommand(command.getName(), command.getDescription()).queue();
                }
                else {
                    guild.upsertCommand(command.getName(), command.getDescription()).addOptions((Collection<? extends OptionData>)command.getOptions()).queue();
                }
            }
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        for (final ICommand command : this.commands) {
            if (command.getName().equals(event.getName())) {
                if (!hasRequiredPermissions(event, command)) {
                    return;
                }
                try {
                    command.execute(event);
                }
                catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void add(final ICommand command) {
        this.commands.add(command);
    }

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private static String getTimestamp() {
        return dateFormat.format(new Date());
    }

    public static boolean hasRequiredPermissions(SlashCommandInteractionEvent event, ICommand command) {
        GuildVariable guildVariable;
        String timestamp = getTimestamp();
        try {
            guildVariable = databaseUtils.getGuildFromDatabase(event.getGuild());
        } catch (SQLException e) {
            getLogger().error("", e);
            return false;
        }
        List<Permission> requiredPermissions = command.getRequiredPermissions();
        if (requiredPermissions.isEmpty()) {
            return true;  // Если прав не требуется
        }

        if (!event.getMember().hasPermission(requiredPermissions)) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(Color.RED);
            embedBuilder.setTitle(LangManager.getMessage(guildVariable.getLang(),"command.noHasPermissionUser.title")
                    .replace("%noHasPermission%", requiredPermissions.toString()));
            embedBuilder.setDescription(LangManager.getMessage(guildVariable.getLang(), "command.noHasPermissionUser.description")
                    .replace("%noHasPermission%", requiredPermissions.toString()));
            embedBuilder.setFooter(LangManager.getMessage(guildVariable.getLang(), "command.noHasPermissionUser.footer")
                    .replace("%time%", timestamp));
            event.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
            return false;
        }
        return true;
    }
}
