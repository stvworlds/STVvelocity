package win.stvworlds.stvvelocity.events;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import org.slf4j.Logger;
import win.stvworlds.stvvelocity.config.ConfigManager;
import win.stvworlds.stvvelocity.misc.CheckPermit;
import win.stvworlds.stvvelocity.misc.Utils;

import java.util.List;

public class WhitelistProtect {

    private final ConfigManager configManager;
    private final Logger logger;

    public WhitelistProtect(ConfigManager configManager, Logger logger) {
        this.configManager = configManager;
        this.logger = logger;
    }

    @Subscribe
    public void onPlayerJoin(ServerPreConnectEvent event) {
        event.getResult().getServer().ifPresent(targetServer -> {
            String targetName = targetServer.getServerInfo().getName();
            List<String> configuredServers = configManager.getWhitelistProtectedServers();

            if (configuredServers.isEmpty()) {
                return;
            }

            if (!Utils.containsIgnoreCase(configuredServers, targetName)) {
                return;
            }

            Player player = event.getPlayer();
            if (!CheckPermit.checkPermit(player, configManager, logger)) {
                event.setResult(ServerPreConnectEvent.ServerResult.denied());
            }
        });
    }
}
