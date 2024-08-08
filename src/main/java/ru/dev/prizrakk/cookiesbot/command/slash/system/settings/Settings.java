package ru.dev.prizrakk.cookiesbot.command.slash.system.settings;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import ru.dev.prizrakk.cookiesbot.command.CommandCategory;
import ru.dev.prizrakk.cookiesbot.command.CommandStatus;
import ru.dev.prizrakk.cookiesbot.command.ICommand;
import ru.dev.prizrakk.cookiesbot.util.Utils;

import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Settings extends Utils implements ICommand {
    @Override
    public String getName() {
        return "settings";
    }

    @Override
    public String getDescription() {
        return "Ваш верный помощник для настройки бота";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.SERVER;
    }

    @Override
    public CommandStatus getStatus() {
        return CommandStatus.OK;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws SQLException {
        if (event.getChannelType() != ChannelType.TEXT) {
            event.reply(getLangMessage(event.getGuild(), "command.doNotSendPrivateMessagesToTheBot")).setEphemeral(true).queue();
            return;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss ");
        String timestamp = dateFormat.format(new Date());
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            event.reply(Utils.getLangMessage(event.getGuild(),"command.interact.settings.noPerm.message")).queue();
        } else {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(new Color(255, 104, 0));
            embed.setTitle(Utils.getLangMessage(event.getGuild(), "command.interact.settings.embed.title.message"));
            embed.setDescription(Utils.getLangMessage(event.getGuild(), "command.interact.settings.embed.description.message"));
            embed.setFooter(Utils.getLangMessage(event.getGuild(), "command.interact.settings.embed.footer.message").replace("%timestamp%", timestamp));
            event.replyEmbeds(embed.build()).setActionRow(
                    StringSelectMenu.create("settingsmenu")
                            .addOptions(
                                    SelectOption.of(Utils.getLangMessage(event.getGuild(),"command.interact.settings.actionRow.language.title.message"), "language")
                                            .withDescription(Utils.getLangMessage(event.getGuild(), "command.interact.settings.actionRow.language.description.message"))

                            ).build()
            ).setEphemeral(true).queue();
        }
    }
}
