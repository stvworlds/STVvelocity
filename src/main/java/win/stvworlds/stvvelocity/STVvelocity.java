package win.stvworlds.stvvelocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import win.stvworlds.stvvelocity.commands.CommandRegister;

public record STVvelocity(ProxyServer proxyServer, Logger logger) {

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        // Plugin initialization logic goes here

        CommandRegister.registerCommands(proxyServer, this);
    }

    @Inject
    public STVvelocity(ProxyServer proxyServer, Logger logger) {
        this.logger = logger;
        this.proxyServer = proxyServer;

        logger.info("STVvelocity has been initialized!");
    }
}
