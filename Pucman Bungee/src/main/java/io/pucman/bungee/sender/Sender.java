package io.pucman.bungee.sender;

import com.google.common.collect.Lists;
import io.pucman.bungee.PLibrary;
import io.pucman.common.exception.UtilException;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utilities regarding sending senders messages that vary in structure.
 */
public final class Sender
{
    private static final PLibrary LIB = PLibrary.get();

    private Sender()
    {
        throw new UtilException();
    }

    /**
     * Converts the string into a BaseComponent and sends it to the player.
     * @param sender - the sender.
     * @param message - the string message.
     */
    public static void send(CommandSender sender, String message)
    {
        sender.sendMessage(TextComponent.fromLegacyText(message));
    }

    /**
     * Converts a list of string messages into a list of BaseComponents and sends it to the player.
     * @param sender - the sender.
     * @param messages - the list of messages.
     */
    public static void send(CommandSender sender, List<String> messages)
    {
        List<BaseComponent[]> components = messages.stream().map(TextComponent::fromLegacyText).collect(Collectors.toCollection(Lists::newLinkedList));
        components.forEach(sender::sendMessage);
    }
}
