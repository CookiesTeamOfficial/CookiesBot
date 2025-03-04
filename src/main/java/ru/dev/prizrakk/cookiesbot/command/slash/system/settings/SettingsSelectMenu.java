package ru.dev.prizrakk.cookiesbot.command.slash.system.settings;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import ru.dev.prizrakk.cookiesbot.database.Database;
import ru.dev.prizrakk.cookiesbot.database.DatabaseUtils;
import ru.dev.prizrakk.cookiesbot.database.ExpVariable;
import ru.dev.prizrakk.cookiesbot.database.GuildVariable;
import ru.dev.prizrakk.cookiesbot.manager.LangManager;
import ru.dev.prizrakk.cookiesbot.util.Utils;

import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class SettingsSelectMenu extends ListenerAdapter {
    private Database database;
    private DatabaseUtils databaseUtils;

    public SettingsSelectMenu(Database database) {
        this.database = database;
        this.databaseUtils = new DatabaseUtils(database);
    }
    GuildVariable guildVariable;
    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (event.getChannelType() != ChannelType.TEXT) {
            event.reply(Utils.getLangMessage(event.getMember().getUser(),event.getGuild(), "command.doNotSendPrivateMessagesToTheBot")).setEphemeral(true).queue();
            return;
        }
        try {
            guildVariable = databaseUtils.getGuildFromDatabase(event.getGuild());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss ");
        String timestamp = dateFormat.format(new Date());
        if (event.getComponentId().equals("settingsmenu")) {
            String selectedValue = event.getValues().get(0);
            if (selectedValue.equals("language")) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(new Color(255, 104, 0));
                embed.setTitle(Utils.getLangMessage(event.getMember().getUser(),event.getGuild(), "command.interact.settingsMenu.embed.title"));
                embed.setDescription(Utils.getLangMessage(event.getMember().getUser(),event.getGuild(), "command.interact.settingsMenu.embed.description"));
                embed.setFooter(Utils.getLangMessage(event.getMember().getUser(),event.getGuild(), "command.interact.settingsMenu.embed.footer").replace("%selectLanguage%", guildVariable.getLang()));

                // Получение списка доступных языков

                StringSelectMenu.Builder selectMenuBuilder = StringSelectMenu.create("language_select");
                Set<String> languageKeys = LangManager.getAvailableLanguages(); // Получаем Set<String>

                for (String langKey : languageKeys) {
                    Map<String, Object> langProperties = LangManager.getLanguageProperties(langKey); // Получаем свойства языка

                    String languageName = (String) langProperties.getOrDefault("language.name", langKey);
                    String languageEmoji = (String) langProperties.getOrDefault("language.emoji", "");
                    String languageDesc = (String) langProperties.getOrDefault("language.description", "");
                    String languageVersion = (String) langProperties.getOrDefault("language.version", "");

                    selectMenuBuilder.addOption(languageName, langKey, languageDesc + " version: " + languageVersion, Emoji.fromUnicode(languageEmoji));
                }




                StringSelectMenu selectMenu = selectMenuBuilder.build();
                event.replyEmbeds(embed.build()).setActionRow(selectMenu).setEphemeral(true).queue();
            }
        }
        if (event.getComponentId().equals("language_select")) {
            try {

                guildVariable.setLang(event.getValues().get(0));
                database.updateGuildStats(guildVariable);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            event.reply(Utils.getLangMessage(event.getMember().getUser(),event.getGuild(), "command.interact.settingsMenu.embed.footer").replace("%selectLanguage%", guildVariable.getLang())).setEphemeral(true).queue();
        }
    }
}
