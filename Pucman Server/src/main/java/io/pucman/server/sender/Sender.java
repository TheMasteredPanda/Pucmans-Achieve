package io.pucman.server.sender;

import io.pucman.common.exception.UtilException;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class Sender
{
    private Sender()
    {
        throw new UtilException();
    }

    public static void send(CommandSender sender, String message)
    {
        sender.sendMessage(message);
    }

    public static void send(CommandSender sender, BaseComponent... components)
    {
        sender.spigot().sendMessage(components);
    }

    public static void send(CommandSender sender, List<String> page)
    {
        sender.sendMessage(page.toArray(new String[page.size()]));
    }
}
