package win.stvworlds.stvvelocity.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;

public class CoreCommand {

    public static BrigadierCommand createBridgeCommand(final ProxyServer proxyServer) {
        LiteralCommandNode<CommandSource> node = BrigadierCommand.literalArgumentBuilder("stvvelocity")
                .requires(source -> source.hasPermission("stvvelocity.command"))
                .executes(context -> {
                    context.getSource().sendMessage(Component.text("STV velocity plugin core command"));
                    return 1;
                })
                .build();
        return new BrigadierCommand(node);
    }
}
