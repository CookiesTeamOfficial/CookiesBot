package ru.dev.prizrakk.cookiesbot.lavalink;

import dev.arbjerg.lavalink.client.*;
import dev.arbjerg.lavalink.client.event.*;
import dev.arbjerg.lavalink.libraries.jda.JDAVoiceUpdateListener;
import net.dv8tion.jda.api.JDA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LavalinkManager {
    private static final Logger LOG = LoggerFactory.getLogger(LavalinkManager.class);
    private final LavalinkClient lavalinkClient;
    private static final int SESSION_INVALID = 4006;
    private JDA jda;

    public LavalinkManager(String token, JDA jda) {
        this.lavalinkClient = new LavalinkClient(Helpers.getUserIdFromToken(token));
        registerLavalinkListeners();  // Регистрируем события
        registerLavalinkNodes();      // Регистрируем узлы
        this.jda = jda;
    }

    public LavalinkClient getLavalinkClient() {
        return lavalinkClient;
    }

    // Возвращаем JDAVoiceUpdateListener для работы с голосовыми каналами
    public JDAVoiceUpdateListener getVoiceUpdateListener() {
        return new JDAVoiceUpdateListener(lavalinkClient);
    }

    private void registerLavalinkNodes() {
        lavalinkClient.addNode(
                new NodeOptions.Builder()
                        .setName("Node #1")
                        .setServerUri("ws://127.0.0.1:2333")
                        .setPassword("4798869em")
                        .build()
        );
    }

    private void registerLavalinkListeners() {
        // Подписываемся на события Lavalink
        lavalinkClient.on(ReadyEvent.class).subscribe((event) -> {
            final LavalinkNode node = event.getNode();
            LOG.info("Node '{}' is ready, session id is '{}'", node.getName(), event.getSessionId());
        });

        lavalinkClient.on(StatsEvent.class).subscribe((event) -> {
            final LavalinkNode node = event.getNode();
            LOG.info(
                    "Node '{}' has stats, current players: {}/{} (link count {})",
                    node.getName(),
                    event.getPlayingPlayers(),
                    event.getPlayers(),
                    lavalinkClient.getLinks().size()
            );
        });

        lavalinkClient.on(TrackStartEvent.class).subscribe((event) -> {
            LOG.trace("{}: track started: {}", event.getNode().getName(), event.getTrack().getInfo());
        });

        lavalinkClient.on(TrackEndEvent.class).subscribe((event) -> {
            LOG.trace("{}: track ended: {} due to: {}", event.getNode().getName(), event.getTrack().getInfo(), event.getEndReason());
        });

        lavalinkClient.on(EmittedEvent.class).subscribe((event) -> {
            LOG.info("Node '{}' emitted event: {}", event.getNode().getName(), event);
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
