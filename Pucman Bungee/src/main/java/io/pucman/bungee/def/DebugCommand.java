package io.pucman.bungee.def;

import io.pucman.bungee.PLibrary;
import io.pucman.bungee.command.PucmanCommand;
import io.pucman.bungee.sender.Sender;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

import java.util.LinkedList;

public class DebugCommand extends PucmanCommand<CommandSender, PLibrary>
{
    public DebugCommand()
    {
        super("bdebug", "zcore.bungee.debug", "Toggle debug mode for the entire server.", false, true);
    }

    @Override
    public void execute(CommandSender sender, LinkedList<String> arguments) throws Exception
    {
        PLibrary.get().getDebug().set(!PLibrary.get().getDebug().get());

        if (!sender.hasPermission("plibrary.bdebug")) {
            Sender.send(sender, "You don't have the permission to turn on, or off, debug mode.");
            return;
        }

        Sender.send(sender, ChatColor.translateAlternateColorCodes('&', "&aSet debug mode to " + PLibrary.get().getDebug().get() + "."));
    }
}
