package dev.prizrakk.commands.modals;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.codec.binary.Hex;
import org.jetbrains.annotations.NotNull;
import dev.prizrakk.commands.fun.Embed;

import java.awt.*;

public class EmbedModal extends ListenerAdapter {
    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if (event.getModalId().equals("embed")) {
            String title = event.getValue("title").getAsString();
            String color = event.getValue("color").getAsString();
            long decimalColor = Long.parseLong(color.replace("#", ""), 16);

            String description = event.getValue("description").getAsString();
            String footer = event.getValue("footer").getAsString();
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle(title);
            embed.setColor((int) decimalColor);
            embed.setDescription(description);
            embed.setFooter(footer);
            embed.setAuthor(Embed.userEmbedSend);
            event.replyEmbeds(embed.build()).queue();
        }
    }
}
