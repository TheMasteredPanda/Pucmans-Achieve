package io.pucman.bungee.def;

import io.pucman.bungee.PLibrary;
import io.pucman.bungee.sender.Sender;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class DebugCommand extends Command
{
    public DebugCommand()
    {
        super("bdebug", "zcore.bungee.debug");
    }

    @Override
    public void execute(CommandSender sender, String[] arguments)
    {
        PLibrary.get().setDebug(!PLibrary.get().isDebug());
        Sender.send(sender, ChatColor.translateAlternateColorCodes('&', "&aSet debug mode to " + PLibrary.get().isDebug() + "."));
    }
}
