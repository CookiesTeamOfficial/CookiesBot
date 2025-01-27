package ru.dev.prizrakk.cookiesbot.command.slash.system.help;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import ru.dev.prizrakk.cookiesbot.command.CommandCategory;
import ru.dev.prizrakk.cookiesbot.command.ICommand;
import ru.dev.prizrakk.cookiesbot.command.CommandStatus;
import ru.dev.prizrakk.cookiesbot.util.Utils;

import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Help extends Utils implements ICommand {
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Команда хелп";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }
    @Override
    public CommandCategory getCategory() {
        return CommandCategory.SYSTEM;
    }

    @Override
    public CommandStatus getStatus() {
        return CommandStatus.OK;
    }

    @Override
    public List<Permission> getRequiredPermissions() {
        return List.of(Permission.MESSAGE_SEND);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws SQLException {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(255, 104, 0));
        embed.setTitle(getLangMessage(event.getGuild(), "command.slash.help.embed.title.message"), "https://cookiesbot.dev-prizrakk.ru");
        embed.setDescription(getLangMessage(event.getGuild(), "command.slash.help.embed.description.message"));
        embed.setFooter(getLangMessage(event.getGuild(), "command.slash.help.embed.footer.message"));
        event.replyEmbeds(embed.build()).setActionRow(
                StringSelectMenu.create("helpmenu")
                        .addOptions(SelectOption.of(getLangMessage(event.getGuild(), "command.slash.help.actionRow.selectMenu.info.title.message"), "info")
                                .withDescription(getLangMessage(event.getGuild(), "command.slash.help.actionRow.selectMenu.info.description.message"))
                                .withEmoji(Emoji.fromUnicode("ℹ️")))
                        .addOptions(SelectOption.of(getLangMessage(event.getGuild(), "command.slash.help.actionRow.selectMenu.command.title.message"), "command")
                                .withDescription(getLangMessage(event.getGuild(), "command.slash.help.actionRow.selectMenu.command.description.message"))
                                .withEmoji(Emoji.fromUnicode("⌨️")))
                        .addOptions(SelectOption.of(getLangMessage(event.getGuild(), "command.slash.help.actionRow.selectMenu.settings.title.message"), "settings")
                                .withDescription(getLangMessage(event.getGuild(), "command.slash.help.actionRow.selectMenu.settings.description.message"))
                                .withEmoji(Emoji.fromUnicode("⚙️")))
                        .build()
        ).queue();
    }

}
