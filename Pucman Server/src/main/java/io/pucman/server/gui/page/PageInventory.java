package io.pucman.server.gui.page;

import com.google.common.collect.Lists;
import io.pucman.common.exception.DeveloperException;
import io.pucman.server.gui.GUIPlayer;
import io.pucman.server.locale.Format;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * GUI inventory for a 'book' like functionality.
 */
public class PageInventory implements GUIInventory
{
    private static final ItemStack BACK_BUTTON;
    private static final ItemStack FORWARD_BUTTON;

    static
    {
        BACK_BUTTON = new ItemStack(Material.ARROW);
        FORWARD_BUTTON = new ItemStack(Material.ARROW);

        ItemMeta meta = BACK_BUTTON.getItemMeta();
        meta.setDisplayName(Format.color("&aPrevious Page"));
        BACK_BUTTON.setItemMeta(meta);

        meta.setDisplayName(Format.color("&aNext Page"));
        FORWARD_BUTTON.setItemMeta(meta);
    }

    /**
     * GUIPlayer that is viewing this gui.
     */
    private final GUIPlayer player;

    /**
     * Contents of the gui.
     */
    private final List<ItemStack> contents = Lists.newArrayList();

    /**
     * Title of each gui page.
     */
    private final String title;

    /**
     * Current page number.
     */
    private int page = 1;

    /**
     * Total amount of pages that can be viewed.
     */
    private int totalPages = 1;

    public PageInventory(GUIPlayer player, String title)
    {
        this.player = player;
        this.title = title;
    }

    /**
     * Add an item to the content of this gui.
     * @param item - item.
     * @return this.
     */
    public PageInventory addItem(ItemStack item)
    {
        this.contents.add(item);
        this.recalculate();
        return this;
    }

    /**
     * Add a collection of items to this content of this gui.
     * @param items - collection of items.
     * @return this.
     */
    public PageInventory withItems(Collection<ItemStack> items)
    {
        this.contents.addAll(items);
        this.recalculate();
        return this;
    }

    /**
     * Add an array of items to this gui.
     * @param items - the array of items.
     * @return this.
     */
    public PageInventory withItems(ItemStack... items)
    {
        this.contents.addAll(Arrays.asList(items));
        this.recalculate();
        return this;
    }

    /**
     * To open up an inventory instance. The way this gui works is as follows: the
     * list of content is parsed to fit the size of the inventory while leaving
     * out space for the index, which is at the bottom of the inventory. When you
     * change the page, be either clicking the forward or back buttons in the index,
     * it will parse the next set of items in the content list to go on that page or
     * the last set of items to go on that pages.
     */
    @Override
    public void open()
    {
        if (totalPages == 1) {
            Inventory inventory = Bukkit.createInventory(null, calcSize(contents.size()), title);
            int slot = 0;
            for (ItemStack item : contents)
            {
                inventory.setItem(slot++, item);
            }
            player.internalIgnore(InternalType.PAGES);
            player.get().openInventory(inventory);
            player.stopInternalIgnore(InternalType.PAGES);
            return;
        }

        int startPoint = (this.page - 1) * 45;
        List<ItemStack> invContents = Lists.newArrayList();

        ItemStack item;
        try {
            while ((item = this.contents.get(startPoint++)) != null) {
                invContents.add(item);
                if (startPoint - ((this.page - 1) * 45) == 45) break;
            }
        }
        catch (IndexOutOfBoundsException ignored) {}

        Inventory inventory = Bukkit.createInventory(null, 54, this.title);

        int slot = 0;
        for (ItemStack invItem : invContents) {
            inventory.setItem(slot++, invItem);
        }

        if (this.page > 1) {
            inventory.setItem(45, PageInventory.BACK_BUTTON);
        }
        if (this.page < this.getPages(this.contents.size())) {
            inventory.setItem(53, PageInventory.FORWARD_BUTTON);
        }

        player.internalIgnore(InternalType.PAGES);
        player.get().openInventory(inventory);
        player.stopInternalIgnore(InternalType.PAGES);
    }

    /**
     * Changes the page number and opens the last page.
     */
    private void backPage()
    {
        page--;
        open();
    }

    /**
     * Changes the page number and opens the next page.
     */
    private void forwardPage()
    {
        page++;
        open();
    }

    /**
     * Calculates the amount of pages possible to be generated with the amount of content that has been added.
     */
    private void recalculate()
    {
        this.totalPages = this.contents.size() > 54 ? this.contents.size() / 45 : 1;
    }

    /**
     * Calculates the size of the page.
     * @param size - the amount of content that will be on the page..
     * @return the total size of the page.
     */
    private int calcSize(int size)
    {
        return (((size - 1) / 9) + 1) * 9;
    }

    private int getPages(int size)
    {
        if (size % 45 == 0) {
            return size / 45;
        }
        Double d = ((double) size + 1) / 45;
        return (int) Math.ceil(d);
    }

    /**
     * Checks if the item in the slot has an immutable action attached to it.
     * @param slot - slot.
     * @return true if it does, else false.
     */
    @Override
    public boolean hasAction(int slot)
    {
        return totalPages > 1 && ((slot == 45) || (slot == 53));
    }

    /**
     * Executes the action corresponding to that slot.
     * @param slot - the slot.
     * @param event - the event.
     */
    @Override
    public void executeAction(int slot, InventoryClickEvent event)
    {
        if (slot == 45 && page > 1) {
            backPage();
        }
        if (slot == 53 && this.page < this.getPages(this.contents.size())) {
            forwardPage();
        }
    }

    @Override
    public void setItem(int slot, ItemStack item)
    {
        throw new DeveloperException("Setting items not supported in PageInventory");
    }
    @Override
    public void setItem(int slot, ItemStack item, Action action)
    {
        throw new DeveloperException("Setting items not supported in PageInventory");
    }

    @Override
    public Inventory getHeldInventory()
    {
        throw new DeveloperException("Getting the instance of the current inventory is not supported.");
    }
}
