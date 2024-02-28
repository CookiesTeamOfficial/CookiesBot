package ru.dev.prizrakk;

import java.sql.SQLException;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import java.util.List;

public interface ICommand
{
    String getName();

    String getDescription();

    List<OptionData> getOptions();

    void execute(final SlashCommandInteractionEvent event) throws SQLException;
}