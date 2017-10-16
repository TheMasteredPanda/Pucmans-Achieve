package io.pucman.bungee.sender;

import com.google.common.collect.Lists;
import io.pucman.common.exception.UtilException;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;
import java.util.stream.Collectors;

public final class Sender
{
    private Sender()
    {
        throw new UtilException();
    }

    public static void sender(CommandSender sender, String message)
    {
        sender.sendMessage(new TextComponent(message));
    }

    public static void sender(CommandSender sender, List<String> messages)
    {
        List<TextComponent> components = messages.stream().map(TextComponent::new).collect(Collectors.toCollection(Lists::newLinkedList));
        sender.sendMessage(components.toArray(new TextComponent[components.size()]));
    }
}
