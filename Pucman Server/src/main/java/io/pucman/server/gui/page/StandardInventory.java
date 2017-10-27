package io.pucman.server.gui.page;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Standard singleton gui.
 */
public class StandardInventory implements GUIInventory
{
    /**
     * Player viewing this inventory.
     */
    private final Player player;

    /**
     * All actions, and their corresponding item slot numbers.
     */
    private final Map<Integer, Action> actions = new HashMap<>();

    /**
     * The inventory instance.
     */
    private final Inventory inventory;

    public StandardInventory(Player player, int size, String title) {
        this.player = player;
        this.inventory = Bukkit.createInventory(null, size, title);
    }

    /**
     * Opens the inventory.
     */
    @Override
    public void open() {
        player.openInventory(inventory);
    }

    /**
     * Checks if the slot corresponds to an  action.
     * @param slot - ths slot.
     * @return true if it it does have an action, else false.
     */
    @Override
    public boolean hasAction(int slot) {
        return actions.containsKey(slot);
    }

    /**
     * Executes an action,
     * @param slot - the slot.
     * @param event - the event.
     */
    @Override
    public void executeAction(int slot, InventoryClickEvent event) {
        if (!hasAction(slot)) {
            return;
        }
        actions.get(slot).on(event);
    }

    /**
     * Sets an item in the inventory.
     * @param slot - the slot.
     * @param item - the item.
     */
    @Override
    public void setItem(int slot, ItemStack item) {
        inventory.setItem(slot, item);
    }

    /**
     * Sets an item and action corresponding to the slot of the item.
     * @param slot - the slot.
     * @param item - the item,
     * @param action - the action.
     */
    @Override
    public void setItem(int slot, ItemStack item, Action action) {
        setItem(slot, item);
        actions.put(slot, action);
    }

    /**
     * Gets the inventory instance.
     * @return inventory instance.
     */
    @Override
    public Inventory getHeldInventory() {
        return this.inventory;
    }

}
