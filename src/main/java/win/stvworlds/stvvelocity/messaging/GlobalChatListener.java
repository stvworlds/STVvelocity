package win.stvworlds.stvvelocity.messaging;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.slf4j.Logger;
import win.stvworlds.stvvelocity.STVvelocity;
import win.stvworlds.stvvelocity.config.ConfigManager;
import win.stvworlds.stvvelocity.misc.Utils;

import java.util.List;
import java.util.Optional;

/**
 * Routes serialized Adventure Component payloads between backend Paper servers.
 * Velocity acts only as a router: payload bytes are never modified.
 */
public final class GlobalChatListener {

    private static final MinecraftChannelIdentifier CHANNEL = STVvelocity.CHANNEL;

    private final ProxyServer proxyServer;
    private final ConfigManager configManager;
    private final Logger logger;

    public GlobalChatListener(ProxyServer proxyServer, ConfigManager configManager, Logger logger) {
        this.proxyServer = proxyServer;
        this.configManager = configManager;
        this.logger = logger;
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        if (!CHANNEL.equals(event.getIdentifier())) {
            return;
        }

        // Only accept plugin messages originating from a backend server connection.
        // Player/client-originated messages must not be trusted for global chat routing.
        if (!(event.getSource() instanceof ServerConnection serverConnection)) {
            event.setResult(PluginMessageEvent.ForwardResult.handled());
            return;
        }

        // Mark handled so Velocity does not forward this message to the player's current server again.
        event.setResult(PluginMessageEvent.ForwardResult.handled());

        byte[] payload = event.getData();
        if (payload == null || payload.length == 0) {
            logger.debug("Ignoring empty global chat payload from {}", serverConnection.getServerInfo().getName());
            return;
        }

        String sourceName = serverConnection.getServerInfo().getName();
        List<String> configuredServers = configManager.getGlobalChatServers();
        if (configuredServers.isEmpty()) {
            logger.debug("Global chat servers list is empty; dropping message from {}", sourceName);
            return;
        }

        // Source must be part of the global chat mesh to participate.
        if (!Utils.containsIgnoreCase(configuredServers, sourceName)) {
            logger.debug(
                    "Source server {} is not in global-messaging.servers; dropping message",
                    sourceName
            );
            return;
        }

        for (String targetName : configuredServers) {
            if (targetName.equalsIgnoreCase(sourceName)) {
                // Do not echo back to the originating backend (it already shows the message locally).
                continue;
            }

            Optional<RegisteredServer> target = proxyServer.getServer(targetName);
            if (target.isEmpty()) {
                logger.warn("Global chat target server '{}' is not registered on this proxy", targetName);
                continue;
            }

            RegisteredServer registeredServer = target.get();
            try {
                boolean sent = registeredServer.sendPluginMessage(CHANNEL, payload);
                if (!sent) {
                    logger.debug(
                            "Failed to send global chat payload from {} to {} (no listeners or offline)",
                            sourceName,
                            targetName
                    );
                }
            } catch (Exception e) {
                logger.warn(
                        "Error forwarding global chat payload from {} to {}: {}",
                        sourceName,
                        targetName,
                        e.getMessage()
                );
            }
        }
    }


}
