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
import ru.dev.prizrakk.cookiesbot.util.Utils;

import java.awt.*;
import java.sql.SQLException;
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
    public List<Permission> getRequiredPermissions() {
        return List.of(Permission.MESSAGE_SEND);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws SQLException {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(255, 104, 0));
        embed.setTitle(getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.help.embed.title"), "https://docs-cookiesbot.cookiesteam.ru");
        embed.setDescription(getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.help.embed.description"));
        embed.setFooter(getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.help.embed.footer"));
        event.replyEmbeds(embed.build()).setActionRow(
                StringSelectMenu.create("helpmenu")
                        .addOptions(SelectOption.of(getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.help.actionRow.selectMenu.info.title"), "info")
                                .withDescription(getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.help.actionRow.selectMenu.info.description"))
                                .withEmoji(Emoji.fromUnicode("ℹ️")))
                        .addOptions(SelectOption.of(getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.help.actionRow.selectMenu.command.title"), "command")
                                .withDescription(getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.help.actionRow.selectMenu.command.description"))
                                .withEmoji(Emoji.fromUnicode("⌨️")))
                        .addOptions(SelectOption.of(getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.help.actionRow.selectMenu.settings.title"), "settings")
                                .withDescription(getLangMessage(event.getMember().getUser(),event.getGuild(), "command.slash.help.actionRow.selectMenu.settings.description"))
                                .withEmoji(Emoji.fromUnicode("⚙️")))
                        .build()
        ).queue();
    }

}
