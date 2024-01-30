package dev.prizrakk.commands.fun;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import dev.prizrakk.ICommand;

import java.util.List;

public class Embed implements ICommand {
    @Override
    public String getName() {
        return "embed";
    }

    @Override
    public String getDescription() {
        return "Делает embed на заказ";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }

    public static String userEmbedSend = "";
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        TextInput Title = TextInput.create("title", "Краткое описание", TextInputStyle.SHORT)
                .setPlaceholder("В чем смысл вашей задумки")
                .setMinLength(5)
                .setMaxLength(100)
                .setRequired(true)
                .build();
        TextInput Color = TextInput.create("color", "Цвет", TextInputStyle.PARAGRAPH)
                .setPlaceholder("#00FF00 HEX формат")
                .setMinLength(3)
                .setMaxLength(1000)
                .setRequired(true)
                .build();
        TextInput Description = TextInput.create("description", "Описание", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Напишите описание")
                .setMinLength(3)
                .setMaxLength(40)
                .setRequired(true)
                .build();
        TextInput Footer = TextInput.create("footer", "Футер", TextInputStyle.PARAGRAPH)
                .setPlaceholder("ФУУУУУТЕР")
                .setMinLength(3)
                .setMaxLength(40)
                .setRequired(true)
                .build();

        Modal modal = Modal.create("embed", "Создай свой ембед")
                .addActionRows(ActionRow.of(Title), ActionRow.of(Color), ActionRow.of(Description), ActionRow.of(Footer))
                .build();
        userEmbedSend = event.getMember().getUser().getName();
        event.replyModal(modal).queue();
    }
}
