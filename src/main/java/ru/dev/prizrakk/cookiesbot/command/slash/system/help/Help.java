package ru.dev.prizrakk.cookiesbot.command.slash.system.help;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import ru.dev.prizrakk.cookiesbot.command.CommandCategory;
import ru.dev.prizrakk.cookiesbot.command.ICommand;
import ru.dev.prizrakk.cookiesbot.command.slash.CommandStatus;

import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Help implements ICommand {
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

    private List<ICommand> commands = new ArrayList<>();
    @Override
    public void execute(SlashCommandInteractionEvent event) throws SQLException {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(255, 104, 0));
        embed.setTitle("Справочная информация о CookiesBot");
        embed.setDescription("Здесь вы найдете бебру и вкинитесь :D");
        embed.setFooter("Все права принадлежат Илону Маску");
        event.replyEmbeds(embed.build()).setActionRow(
                StringSelectMenu.create("helpmenu")
                        .addOptions(SelectOption.of("Информация", "info")
                                .withDescription("Покажет вам информацию о боте и технической части")
                                .withEmoji(Emoji.fromUnicode("ℹ️")))
                        .addOptions(SelectOption.of("Команды", "command")
                                .withDescription("Покажет все возможные команды")
                                .withEmoji(Emoji.fromUnicode("⌨️")))
                        .build()
        ).queue();
    }

}
