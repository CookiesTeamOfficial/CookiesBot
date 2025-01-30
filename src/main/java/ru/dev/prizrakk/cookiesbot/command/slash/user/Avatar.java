package ru.dev.prizrakk.cookiesbot.command.slash.user;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.dev.prizrakk.cookiesbot.command.CommandCategory;
import ru.dev.prizrakk.cookiesbot.command.ICommand;
import ru.dev.prizrakk.cookiesbot.util.Utils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Avatar extends Utils implements ICommand {
    @Override
    public String getName() {
        return "avatar";
    }

    @Override
    public String getDescription() {
        return "Показывает вашу аватарку или аватарку пользователя";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.USER, "user", "Позволяет показать аватарку пользователя", false));
        return options;
    }
    @Override
    public CommandCategory getCategory() {
        return CommandCategory.USER;
    }

    @Override
    public List<Permission> getRequiredPermissions() {
        return List.of(Permission.MESSAGE_SEND);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws SQLException {
        User user;
        if (event.getOption("user") != null) {
            user = event.getOption("user").getAsUser();
        } else {
            user = event.getUser();
        }
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(getLangMessage(event.getGuild(), "command.slash.avatar.embed.title.message").replace("%user%", user.getName()));
        embed.setImage(user.getAvatarUrl());
        embed.setFooter(getLangMessage(event.getGuild(), "command.slash.avatar.embed.footer.message"));
        event.replyEmbeds(embed.build()).queue();
    }
}
