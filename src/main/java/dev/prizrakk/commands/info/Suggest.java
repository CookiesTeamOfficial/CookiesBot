package dev.prizrakk.commands.info;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import dev.prizrakk.ICommand;

import java.util.List;

public class Suggest implements ICommand {
    @Override
    public String getName() {
        return "suggest";
    }

    @Override
    public String getDescription() {
        return "Предложения";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        TextInput text1 = TextInput.create("text1", "Краткое описание", TextInputStyle.SHORT)
                .setPlaceholder("В чем смысл вашей задумки")
                .setMinLength(5)
                .setMaxLength(100)
                .setRequired(true)
                .build();

        TextInput text2 = TextInput.create("text2", "Полное описание", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Распишите ее полностью чего вы хотите")
                .setMinLength(3)
                .setMaxLength(1000)
                .setRequired(true)
                .build();
        TextInput text3 = TextInput.create("text3", "Контакт", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Напишите ваш дискод тег чтобы мы могли связатся с вами")
                .setMinLength(3)
                .setMaxLength(40)
                .setRequired(true)
                .build();

        Modal modal = Modal.create("suggest", "Предложение")
                .addActionRows(ActionRow.of(text1), ActionRow.of(text2), ActionRow.of(text3))
                .build();
        event.replyModal(modal).queue();
    }
}
