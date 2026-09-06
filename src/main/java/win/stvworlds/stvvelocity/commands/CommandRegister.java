package win.stvworlds.stvvelocity.commands;

import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.proxy.ProxyServer;

public class CommandRegister {
    public static void registerCommands(final ProxyServer proxy, final Object plugin) {
        CommandManager commandManager = proxy.getCommandManager();
        CommandMeta commandMeta = commandManager.metaBuilder("stvvelocity")
                .aliases("stvv", "stevious-velocity")
                .plugin(plugin)
                .build();

        BrigadierCommand commandToRegister = CoreCommand.createBridgeCommand(proxy);

        commandManager.register(commandMeta, commandToRegister);
    }
}
