package ru.dev.prizrakk.cookiesbot.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Deprecated
public class OnLeft extends ListenerAdapter {
    long welcome = 1208944813152665640L;
    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Покинул сервер! :c " + event.getGuild().getName());
        embed.setDescription("Пока " + event.getMember().getAsMention() +"!");
        embed.setImage("https://i.imgur.com/QRrTwiV.gif");
        event.getGuild().getTextChannelById(welcome).sendMessageEmbeds(embed.build()).queue();
    }
}
