package io.pucman.server.gui.page;

import io.pucman.server.gui.GUIPlayer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * GUIPage, regular gui page.
 * @param <P>
 */
public abstract class GUIPage<P extends JavaPlugin>
{
    protected final P plugin;
    protected final GUIPlayer player;
    protected final String title;

    private GUIInventory inventory;

    public GUIPage(P plugin, GUIPlayer player, String title)
    {
        this.plugin = plugin;
        this.player = player;
        this.title = title;
    }


    protected abstract GUIInventory loadInventory();

    public final void open()
    {
        this.inventory = this.loadInventory();
        inventory.open();
        onInventoryOpen();
    }

    protected void onInventoryOpen()
    {
    }

    public final void onClose() {
        player.onClose();
        onInventoryClose();
        if (player.inGUI()) {
            player.openPage(player.currentPage(), false);
        }
    }

    protected void onInventoryClose() {}

    public final void onClick(InventoryClickEvent event)
    {
        event.setCancelled(true);

        if (inventory.hasAction(event.getSlot())) {
            inventory.executeAction(event.getSlot(), event);
        } else {
            if (event.getInventory().getItem(event.getSlot()) != null) {
                onInventoryClick(event);
            }
        }
    }

    protected void onInventoryClick(InventoryClickEvent event) {}

    public final String getTitle()
    {
        return title;
    }

    protected final GUIInventory getGUIInventory()
    {
        return this.inventory;
    }

}
