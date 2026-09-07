package win.stvworlds.stvvelocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import org.slf4j.Logger;
import win.stvworlds.stvvelocity.commands.CommandRegister;
import win.stvworlds.stvvelocity.config.ConfigManager;
import win.stvworlds.stvvelocity.messaging.GlobalChatListener;

import java.nio.file.Path;

public record STVvelocity(ProxyServer proxyServer, Logger logger, ConfigManager configManager) {

    public static final MinecraftChannelIdentifier CHANNEL =
            MinecraftChannelIdentifier.from("stvchat:main");

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        CommandRegister.registerCommands(proxyServer, this);
        proxyServer.getChannelRegistrar().register(CHANNEL);
        proxyServer.getEventManager().register(this, new GlobalChatListener(proxyServer, configManager, logger));
        logger.info(
                "Global chat routing enabled for servers: {}",
                configManager.getGlobalChatServers()
        );
    }

    @Inject
    public STVvelocity(ProxyServer proxyServer, Logger logger, @DataDirectory Path dataDirectory) {
        this(proxyServer, logger, new ConfigManager(dataDirectory, logger));
        configManager.load();

        logger.info(configManager.getRoot().node("init-message").getString("STVvelocity has been initialized!"));
    }
}
