package ru.dev.prizrakk.cookiesbot.command;

import java.sql.SQLException;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public interface ICommand {
    String getName();

    String getDescription();

    List<OptionData> getOptions();
    CommandCategory getCategory();
    CommandStatus getStatus();
    List<Permission> getRequiredPermissions();

    void execute(final SlashCommandInteractionEvent event) throws SQLException;
}
