package win.stvworlds.stvvelocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import win.stvworlds.stvvelocity.commands.CommandRegister;
import win.stvworlds.stvvelocity.config.ConfigManager;

import java.nio.file.Path;

public record STVvelocity(ProxyServer proxyServer, Logger logger, ConfigManager configManager) {

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        CommandRegister.registerCommands(proxyServer, this);
    }

    @Inject
    public STVvelocity(ProxyServer proxyServer, Logger logger, @DataDirectory Path dataDirectory) {
        this(proxyServer, logger, new ConfigManager(dataDirectory, logger));
        configManager.load();

        logger.info(configManager.getRoot().node("init-message").getString("STVvelocity has been initialized!"));
    }
}
