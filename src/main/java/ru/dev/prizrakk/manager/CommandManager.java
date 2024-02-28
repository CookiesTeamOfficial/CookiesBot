

package ru.dev.prizrakk.manager;

import java.sql.SQLException;

import ru.dev.prizrakk.ICommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import java.util.Iterator;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import java.util.Collection;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import java.util.ArrayList;

import java.util.List;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandManager extends ListenerAdapter
{
    private List<ICommand> commands;
    
    public CommandManager() {
        this.commands = new ArrayList<ICommand>();
    }
    
    @Override
    public void onReady(@NotNull final ReadyEvent event) {
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
    public void onSlashCommandInteraction(@NotNull final SlashCommandInteractionEvent event) {
        for (final ICommand command : this.commands) {
            if (command.getName().equals(event.getName())) {
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
}
