package ru.dev.prizrakk.cookiesbot.command.slash.system.help;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import ru.dev.prizrakk.cookiesbot.command.CommandCategory;
import ru.dev.prizrakk.cookiesbot.command.CommandManager;
import ru.dev.prizrakk.cookiesbot.command.ICommand;
import ru.dev.prizrakk.cookiesbot.util.Utils;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HelpSelectMenu extends ListenerAdapter {
    public List<ICommand> commands = CommandManager.commands; // Список всех команд

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss ");
        String timestamp = dateFormat.format(new Date());
        if (event.getComponentId().equals("helpmenu")) {
            String selectedValue = event.getValues().get(0); // Получение первого выбранного значения
            switch (selectedValue) {
                case "info" -> {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setTitle(Utils.getLangMessage(event.getGuild(), "command.interact.helpSelectMenu.info.embed.title.message"));
                    embed.setColor(new Color(255, 104, 0));
                    embed.setDescription(Utils.getLangMessage(event.getGuild(), "command.interact.helpSelectMenu.info.embed.description.message"));
                    embed.addField(Utils.getLangMessage(event.getGuild(), "command.interact.helpSelectMenu.info.field.dev.title.message"), "<@579683756789727243>", true);
                    embed.addField(Utils.getLangMessage(event.getGuild(), "command.interact.helpSelectMenu.info.field.programLang.title.message"), "`java 15.0.2 by Amazon Corretto`", true);
                    embed.addField(Utils.getLangMessage(event.getGuild(), "command.interact.helpSelectMenu.info.field.discordLibs.title.message"), "`JDA 5.0.0-beta.20`", true);
                    embed.addField(Utils.getLangMessage(event.getGuild(), "command.interact.helpSelectMenu.info.field.version.title.message"), "`pre-release 0.5-rework`", true);
                    embed.setFooter(Utils.getLangMessage(event.getGuild(), "command.interact.helpSelectMenu.info.footer.message").replace("%timestamp%", timestamp));
                    Button button = Button.link("https://github.com/CookiessTeam/CookiesBot", "GitHub");
                    event.replyEmbeds(embed.build()).setActionRow(button).setEphemeral(true).queue();
                }
                case "command" -> {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setColor(new Color(255, 104, 0));
                    embed.setTitle(Utils.getLangMessage(event.getGuild(), "command.interact.helpSelectMenu.command.embed.title.message"));
                    embed.setDescription(Utils.getLangMessage(event.getGuild(), "command.interact.helpSelectMenu.command.embed.description.message"));
                    embed.setFooter(Utils.getLangMessage(event.getGuild(), "command.interact.helpSelectMenu.command.embed.footer.message").replace("%timestamp%", timestamp));
                    event.replyEmbeds(embed.build()).setActionRow(
                            StringSelectMenu.create("helpcommand")
                                    .addOptions(
                                            SelectOption.of(Utils.getLangMessage(event.getGuild(),"command.interact.helpSelectMenu.actionRow.server.title.message"), "server")
                                                    .withDescription(Utils.getLangMessage(event.getGuild(),"command.interact.helpSelectMenu.actionRow.server.description.message"))
                                                    .withEmoji(Emoji.fromUnicode("🏠")),
                                            SelectOption.of(Utils.getLangMessage(event.getGuild(),"command.interact.helpSelectMenu.actionRow.admin.title.message"), "admin")
                                                    .withDescription(Utils.getLangMessage(event.getGuild(),"command.interact.helpSelectMenu.actionRow.admin.description.message"))
                                                    .withEmoji(Emoji.fromUnicode("🔧")),
                                            SelectOption.of(Utils.getLangMessage(event.getGuild(),"command.interact.helpSelectMenu.actionRow.fun.title.message"), "fun")
                                                    .withDescription(Utils.getLangMessage(event.getGuild(),"command.interact.helpSelectMenu.actionRow.fun.description.message"))
                                                    .withEmoji(Emoji.fromUnicode("🎉")),
                                            SelectOption.of(Utils.getLangMessage(event.getGuild(),"command.interact.helpSelectMenu.actionRow.music.title.message"), "music")
                                                    .withDescription(Utils.getLangMessage(event.getGuild(),"command.interact.helpSelectMenu.actionRow.music.description.message"))
                                                    .withEmoji(Emoji.fromUnicode("🎵")),
                                            SelectOption.of(Utils.getLangMessage(event.getGuild(),"command.interact.helpSelectMenu.actionRow.prefix.title.message"), "prefix")
                                                    .withDescription(Utils.getLangMessage(event.getGuild(),"command.interact.helpSelectMenu.actionRow.prefix.description.message"))
                                                    .withEmoji(Emoji.fromUnicode("🔤")),
                                            SelectOption.of(Utils.getLangMessage(event.getGuild(),"command.interact.helpSelectMenu.actionRow.user.title.message"), "user")
                                                    .withDescription(Utils.getLangMessage(event.getGuild(),"command.interact.helpSelectMenu.actionRow.user.description.message"))
                                                    .withEmoji(Emoji.fromUnicode("🔤")),
                                            SelectOption.of(Utils.getLangMessage(event.getGuild(),"command.interact.helpSelectMenu.actionRow.other.title.message"), "other")
                                                    .withDescription(Utils.getLangMessage(event.getGuild(),"command.interact.helpSelectMenu.actionRow.other.description.message"))
                                                    .withEmoji(Emoji.fromUnicode("❓"))
                                    ).build()
                    ).setEphemeral(true).queue();
                }
                case "settings" -> {
                    if (event.getChannelType() != ChannelType.TEXT) {
                        event.reply(Utils.getLangMessage(event.getGuild(), "command.doNotSendPrivateMessagesToTheBot")).setEphemeral(true).queue();
                        return;
                    }
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
        }
        if (event.getComponentId().equals("helpcommand")) {
            String selectedValue = event.getValues().get(0); // Получение первого выбранного значения

            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(new Color(255, 104, 0));
            embed.setTitle(Utils.getLangMessage(event.getGuild(), "command.interact.helpCommand.embed.title.message").replace("%category%", selectedValue.toUpperCase()));

            CommandCategory category;
            switch (selectedValue) {
                case "server":
                    category = CommandCategory.SERVER;
                    break;
                case "admin":
                    category = CommandCategory.ADMINISTRATION;
                    break;
                case "fun":
                    category = CommandCategory.FUN;
                    break;
                case "music":
                    category = CommandCategory.MUSIC;
                    break;
                case "prefix":
                    category = CommandCategory.PREFIX;
                    break;
                case "other":
                    category = CommandCategory.OTHER;
                    break;
                default:
                    embed.setDescription(Utils.getLangMessage(event.getGuild(), "command.interact.helpCommand.embed.notFoundCategory.message"));
                    category = null;
                    break;
            }

            if (category != null) {
                for (ICommand command : commands) {
                    if (command.getCategory() == category) {

                        StringBuilder optionsDescription = new StringBuilder();
                        List<OptionData> options = command.getOptions();
                        if (options != null) {
                            for (OptionData option : options) {
                                optionsDescription.append(option.getName())
                                        .append(": ")
                                        .append(option.getDescription())
                                        .append("\n");
                            }
                        }
                        embed.addField(Utils.getLangMessage(event.getGuild(),"command.interact.helpCommand.embed.field.name.title.message"),
                                 Utils.getLangMessage(event.getGuild(), "command.interact.helpCommand.embed.field.name.description.message")
                                         .replace("%nameCommand%",command.getName()), false);
                        embed.addField(Utils.getLangMessage(event.getGuild(), "command.interact.helpCommand.embed.field.description.title.message"),
                                Utils.getLangMessage(event.getGuild(), "command.interact.helpCommand.embed.field.description.description.message")
                                        .replace("%descriptionCommand%", command.getDescription()), true);
                        embed.addField(Utils.getLangMessage(event.getGuild(), "command.interact.helpCommand.embed.field.option.title.message"),
                                Utils.getLangMessage(event.getGuild(), "command.interact.helpCommand.embed.field.option.description.message")
                                        .replace("%option%", (optionsDescription.length() > 0 ? optionsDescription.toString() : "Нет опций")) , true);
                    }
                }
            }
            embed.setFooter(Utils.getLangMessage(event.getGuild(), "command.interact.helpCommand.embed.footer.message").replace("%timestamp%", timestamp));
            event.replyEmbeds(embed.build()).setEphemeral(true).queue();
        }
    }
}
