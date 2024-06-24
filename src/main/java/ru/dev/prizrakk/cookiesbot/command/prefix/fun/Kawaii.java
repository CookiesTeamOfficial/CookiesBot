package ru.dev.prizrakk.cookiesbot.command.prefix.fun;

import com.google.gson.Gson;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import ru.dev.prizrakk.cookiesbot.database.Database;
import ru.dev.prizrakk.cookiesbot.database.DatabaseUtils;
import ru.dev.prizrakk.cookiesbot.database.GuildVariable;
import ru.dev.prizrakk.cookiesbot.manager.LangManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class Kawaii extends ListenerAdapter {
    private final Database database;
    private final DatabaseUtils databaseUtils;

    public Kawaii(Database database) {
        this.database = database;
        this.databaseUtils = new DatabaseUtils(database);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        GuildVariable guildVariable;
        try {
            guildVariable = databaseUtils.getGuildFromDatabase(event.getGuild());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String[] message = event.getMessage().getContentRaw().split(" ", 2);
        if (message[0].startsWith("!")) {
            String command = message[0].substring(1).toLowerCase();
            String[] args = message.length > 1 ? message[1].split(" ") : new String[0];

            Map<String, BiConsumer<MessageReceivedEvent, GuildVariable>> commandMap = new HashMap<>();
            commandMap.put("hug", this::handleHugCommand);
            commandMap.put("kiss", this::handleKissCommand);
            commandMap.put("kill", this::handleKillCommand);
            commandMap.put("dead", this::handleDeadCommand);
            commandMap.put("cry", this::handleCryCommand);

            if (commandMap.containsKey(command)) {
                commandMap.get(command).accept(event, guildVariable);
            }
        }
    }

    private void handleHugCommand(MessageReceivedEvent event, GuildVariable guildVariable) {
        EmbedBuilder embedBuilder = createEmbed(event, guildVariable, "hug");
        if (embedBuilder != null) {
            event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
        }
    }

    private void handleKissCommand(MessageReceivedEvent event, GuildVariable guildVariable) {
        EmbedBuilder embedBuilder = createEmbed(event, guildVariable, "kiss");
        if (embedBuilder != null) {
            event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
        }
    }

    private void handleKillCommand(MessageReceivedEvent event, GuildVariable guildVariable) {
        EmbedBuilder embedBuilder = createEmbed(event, guildVariable, "kill");
        if (embedBuilder != null) {
            event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
        }
    }

    private void handleDeadCommand(MessageReceivedEvent event, GuildVariable guildVariable) {
        EmbedBuilder embedBuilder = createEmbed(event, guildVariable, "dead");
        if (embedBuilder != null) {
            event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
        }
    }

    private void handleCryCommand(MessageReceivedEvent event, GuildVariable guildVariable) {
        EmbedBuilder embedBuilder = createEmbed(event, guildVariable, "cry");
        if (embedBuilder != null) {
            event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
        }
    }

    private EmbedBuilder createEmbed(MessageReceivedEvent event, GuildVariable guildVariable, String mode) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        String[] args = event.getMessage().getContentRaw().split(" ", 2);
        if (args.length == 1) {
            embedBuilder.setTitle(LangManager.getMessage(guildVariable.getLang(), "command.prefix.kawaii." + mode + ".none.message"));
        } else {
            if (!event.getMessage().getContentDisplay().contains("@")) {
                event.getChannel().sendMessage(LangManager.getMessage(guildVariable.getLang(), "command.prefix.kawaii.errorMentioned.message")).queue();
                return null;
            }
            String[] mentionedUsers = event.getMessage().getContentDisplay().split("@");
            String user = mentionedUsers[1].split(" ")[0];
            embedBuilder.setTitle(LangManager.getMessage(guildVariable.getLang(), "command.prefix.kawaii." + mode + ".mentioned.message").replace("%user%", user));
        }
        embedBuilder.setImage(apiLoad(mode));
        return embedBuilder;
    }

    public String apiLoad(String mode) {
        String responseValue = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet("https://kawaii.red/api/gif/" + mode + "/token=579683756789727243.a3ceICmznU290G75nVIQ");

        try (CloseableHttpResponse response = httpClient.execute(request);
             BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            Gson gson = new Gson();
            ResponseObject responseObject = gson.fromJson(result.toString(), ResponseObject.class);
            responseValue = responseObject.getResponse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseValue;
    }

    static class ResponseObject {
        private String response;

        public String getResponse() {
            return response;
        }
    }
}