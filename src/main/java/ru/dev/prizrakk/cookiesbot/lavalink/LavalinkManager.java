package ru.dev.prizrakk.cookiesbot.lavalink;

import dev.arbjerg.lavalink.client.*;
import dev.arbjerg.lavalink.client.event.*;
import dev.arbjerg.lavalink.libraries.jda.JDAVoiceUpdateListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.dev.prizrakk.cookiesbot.manager.ColorManager;
import ru.dev.prizrakk.cookiesbot.manager.ConfigManager;

import java.util.List;
import java.util.Map;

import static ru.dev.prizrakk.cookiesbot.util.Utils.getLogger;

public class LavalinkManager {
    private static final Logger LOG = LoggerFactory.getLogger(LavalinkManager.class);
    private LavalinkClient lavalinkClient = null;
    private static final int SESSION_INVALID = 4006;
    private JDA jda;

    public LavalinkManager(String token, JDA jda) {
        try {
            this.lavalinkClient = new LavalinkClient(Helpers.getUserIdFromToken(token));
            getLogger().info(ColorManager.ANSI_BLUE + "===================");
            getLogger().info("Load lavalink nodes");
            getLogger().info(ColorManager.ANSI_BLUE + "===================");
            registerLavalinkListeners();
            registerLavalinkNodes();
            this.jda = jda;
        } catch (InvalidTokenException e) {
            getLogger().error("The provided token is invalid!", e);
            System.exit(1);
        }
        catch (Exception e) {
            getLogger().error("An unexpected error occurred!", e);
            System.exit(1);
        }
    }

    public LavalinkClient getLavalinkClient() {
        return lavalinkClient;
    }


    public JDAVoiceUpdateListener getVoiceUpdateListener() {
        return new JDAVoiceUpdateListener(lavalinkClient);
    }

    public void registerLavalinkNodes() {
        ConfigManager configManager = new ConfigManager();
        Map<String, Object> nodesMap = configManager.getMap("lavalink.node");
        if (nodesMap == null || nodesMap.isEmpty()) {
            getLogger().warn("No Lavalink nodes found in config!");
            return;
        }

        for (Map.Entry<String, Object> entry : nodesMap.entrySet()) {
            if (!(entry.getValue() instanceof Map)) continue;

            Map<String, String> node = (Map<String, String>) entry.getValue();
            String name = node.get("name");
            String url = node.get("url");
            String password = node.get("password");

            lavalinkClient.addNode(
                    new NodeOptions.Builder()
                            .setName(name)
                            .setServerUri(url)
                            .setPassword(password)
                            .build()
            );

            getLogger().info("Registered Lavalink node: " + name);


        }
    }


    private void registerLavalinkListeners() {


        lavalinkClient.on(ReadyEvent.class).subscribe((event) -> {
            final LavalinkNode node = event.getNode();
            getLogger().info("Node '" + node.getName() + "' is ready, session id is '" + event.getSessionId() + "'");
        });

        lavalinkClient.on(StatsEvent.class).subscribe((event) -> {
            final LavalinkNode node = event.getNode();
            getLogger().debug("Node '" + node.getName()+ "' has stats, current players: " + event.getPlayingPlayers()+ "/" + event.getPlayers() + "(link count " + event.getPlayers() + ")");
        });

        lavalinkClient.on(TrackStartEvent.class).subscribe((event) -> {
           getLogger().debug(event.getNode().getName() + ": track started: {}" + event.getTrack().getInfo());
        });

        lavalinkClient.on(TrackEndEvent.class).subscribe((event) -> {
            getLogger().debug(event.getNode().getName() + ": track ended: " + event.getTrack().getInfo() + "due to: " + event.getEndReason());
        });

        lavalinkClient.on(EmittedEvent.class).subscribe((event) -> {
            getLogger().debug("Node '" + event.getNode().getName() + "' emitted event: " + event);
        });

        // Переподключение при ошибке сессии
        lavalinkClient.on(WebSocketClosedEvent.class).subscribe((event) -> {
            if (event.getCode() == SESSION_INVALID) {
                final var guildId = event.getGuildId();
                final var guild = jda.getGuildById(guildId);

                if (guild == null) {
                    return;
                }

                final var connectedChannel = guild.getSelfMember().getVoiceState().getChannel();

                if (connectedChannel == null) {
                    return;
                }

                jda.getDirectAudioController().reconnect(connectedChannel);
            }
        });
    }
}
