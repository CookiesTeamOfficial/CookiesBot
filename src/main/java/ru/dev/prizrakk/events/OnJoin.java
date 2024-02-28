package ru.dev.prizrakk.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ru.dev.prizrakk.util.Config;

public class OnJoin extends ListenerAdapter {
    long welcome = 1208944813152665640L;
    Config config = new Config();
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        long id = 1206376441516662786L;
        Role player = event.getGuild().getRoleById(id);
        event.getGuild().addRoleToMember(event.getMember(), player).queue();
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Добро пожаловать на сервер " + event.getGuild().getName());
        embed.setDescription("Привет " + event.getMember().getAsMention() +"!");
        embed.setImage("https://i.imgur.com/XDHcEhd.gif");
        embed.setFooter(config.years_author);
        event.getGuild().getTextChannelById(welcome).sendMessageEmbeds(embed.build()).queue();
    }
}
