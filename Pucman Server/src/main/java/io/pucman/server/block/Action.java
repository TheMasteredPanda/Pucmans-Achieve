package io.pucman.server.block;

import jdk.nashorn.internal.objects.annotations.Function;
import org.bukkit.event.Event;

@FunctionalInterface
public interface Action<E extends Event>
{
    void on(E event);
}
