package io.pucman.server.gui.page;

import org.bukkit.event.inventory.InventoryClickEvent;

@FunctionalInterface
public interface Action
{
    void on(InventoryClickEvent event);
}
