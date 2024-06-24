package ru.dev.prizrakk.cookiesbot.command.slash.server;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.FileUpload;
import ru.dev.prizrakk.cookiesbot.command.CommandCategory;
import ru.dev.prizrakk.cookiesbot.command.ICommand;
import ru.dev.prizrakk.cookiesbot.command.CommandStatus;
import ru.dev.prizrakk.cookiesbot.database.Database;
import ru.dev.prizrakk.cookiesbot.database.DatabaseUtils;
import ru.dev.prizrakk.cookiesbot.database.ExpVariable;
import ru.dev.prizrakk.cookiesbot.util.Utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RankCard extends Utils implements ICommand {
    @Override
    public String getName() {
        return "rank";
    }

    @Override
    public String getDescription() {
        return "rank this is base";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.USER, "user", "Позволяет узнать инфу пользователя", false));
        return options;
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.SERVER;
    }

    @Override
    public CommandStatus getStatus() {
        return CommandStatus.OK;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws SQLException {
        User user = (event.getOption("user") != null) ? event.getOption("user").getAsUser() : event.getUser();


        try {
            String avatarUrl = user.getAvatarUrl();
            String effectiveName = user.getEffectiveName();
            String username = user.getName();
            int level = getUserLevel(user, event.getGuild()).getLevel();
            int experience = getUserLevel(user, event.getGuild()).getExp();
            int maxExperience = getUserLevel(user, event.getGuild()).getMaxExp();

            byte[] profileImage = generateProfileImage(event, avatarUrl, effectiveName, username, level, experience, maxExperience);
            event.replyFiles(FileUpload.fromData(profileImage, "profile_image.png")).queue();
        } catch (IOException e) {
            e.printStackTrace();
            event.reply(getLangMessage(event.getGuild(), "command.slash.rankCard.errorDrawImage.message")).setEphemeral(true).queue();
        }
    }

    public static byte[] generateProfileImage(SlashCommandInteractionEvent event, String avatarUrl, String effectiveName, String username, int level, int experience, int maxExperience) throws IOException {
        int width = 900;
        int height = 200;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        // Задний фон
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // Загрузка аватарки
        BufferedImage avatar = loadImageFromUrl(avatarUrl);
        BufferedImage roundedAvatar = getRoundedImage(avatar, 160);
        g.drawImage(roundedAvatar, 20, 20, null);

        // Рисование текста
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString(effectiveName, 200, 45);

        // Рисовка ника
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 26));
        g.drawString(username, 200, 70);

        // Рисование уровня
        g.setFont(new Font("Arial", Font.BOLD, 22));
        g.drawString(getLangMessage(event.getGuild(), "command.slash.rankCard.level.message").replace("%level%", level + ""), 200, 140);

        // Рисование опыта
        g.setFont(new Font("Arial", Font.BOLD, 22));
        g.drawString(getLangMessage(event.getGuild(), "command.slash.rankCard.experience.message")
                .replace("%experience%", experience + "")
                .replace("%maxExperience%", maxExperience + ""), 340, 140);

        // Рисование прогресс-бара
        int progressBarWidth = 600;
        int progressBarHeight = 24;
        int progressBarX = 200;
        int progressBarY = 150;
        int filledWidth = (int) ((double) experience / maxExperience * progressBarWidth);

        // Фон прогресс-бара
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(progressBarX, progressBarY, progressBarWidth, progressBarHeight);

        // Заполненная часть прогресс-бара
        g.setColor(Color.GREEN);
        g.fillRect(progressBarX, progressBarY, filledWidth, progressBarHeight);

        // Обводка прогресс-бара
        g.setColor(Color.BLACK);
        g.drawRect(progressBarX, progressBarY, progressBarWidth, progressBarHeight);

        // Завершение работы с графикой
        g.dispose();

        // Конвертация изображения в байты
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }

    private static BufferedImage loadImageFromUrl(String avatarUrl) throws IOException {
        URL url = new URL(avatarUrl);
        URLConnection uc = url.openConnection();
        uc.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");

        try (InputStream is = uc.getInputStream()) {
            return ImageIO.read(is);
        }
    }

    private static BufferedImage getRoundedImage(BufferedImage image, int diameter) {
        BufferedImage mask = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = mask.createGraphics();

        applyQualityRenderingHints(g2);
        g2.fill(new Ellipse2D.Double(0, 0, diameter, diameter));
        g2.dispose();

        BufferedImage roundImage = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
        g2 = roundImage.createGraphics();
        applyQualityRenderingHints(g2);
        g2.drawImage(image, 0, 0, diameter, diameter, null);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_IN));
        g2.drawImage(mask, 0, 0, null);
        g2.dispose();

        return roundImage;
    }

    private static void applyQualityRenderingHints(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }
}
