package dev.prizrakk.commands.modals;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import dev.prizrakk.ICommand;

import java.awt.*;

public class SuggestModals extends ListenerAdapter {
    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if (event.getModalId().equals("suggest")) {
            String text1 = event.getValue("text1").getAsString();
            String text2 = event.getValue("text2").getAsString();
            String text3 = event.getValue("text3").getAsString();
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.BLUE);
            embed.setTitle("Предложение!");
            embed.setDescription("Отправил: " + text3
                    + "\n" +"Краткое описание " + text1
                    + "\n" + "Полное описание: " + text2);
            embed.setFooter("ЫХЫХЫ");
            TextChannel channel = event.getGuild().getTextChannelById("1128739370845880410");
            channel.sendMessageEmbeds(embed.build()).queue();
            event.reply("Спасибо за ваше предложение в скором времени мы свяжемся с вами если оно нам подойдет!").setEphemeral(true).queue();
        }
    }
}
