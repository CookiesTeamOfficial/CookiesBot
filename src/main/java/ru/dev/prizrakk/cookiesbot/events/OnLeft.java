package ru.dev.prizrakk.cookiesbot.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ru.dev.prizrakk.cookiesbot.util.Config;
@Deprecated
public class OnLeft extends ListenerAdapter {
    long welcome = 1208944813152665640L;
    Config config = new Config();
    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Покинул сервер! :c " + event.getGuild().getName());
        embed.setDescription("Пока " + event.getMember().getAsMention() +"!");
        embed.setImage("https://i.imgur.com/QRrTwiV.gif");
        embed.setFooter(config.years_author);
        event.getGuild().getTextChannelById(welcome).sendMessageEmbeds(embed.build()).queue();
    }
}
