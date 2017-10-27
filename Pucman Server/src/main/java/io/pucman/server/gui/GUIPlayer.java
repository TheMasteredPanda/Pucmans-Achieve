package io.pucman.server.gui;

import com.google.common.collect.Lists;
import io.pucman.server.PLibrary;
import io.pucman.server.gui.page.GUIPage;
import io.pucman.server.gui.page.InternalType;
import io.pucman.server.player.PlayerWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for players. One is made for every player. This manages all GUI-related actions in this library section.
 */
public class GUIPlayer extends PlayerWrapper
{
    /**
     * Library instance.
     */
    private final PLibrary lib;

    /**
     * A cache for all the gui instances that the player has open, the more gui instances that are open under
     * the players gui wrapper the bigger the crumb gets. The crumb gets smaller when the player exits a gui.
     * When they exit a gui, and they were on a gui previously before the gui they just closed popped up, they
     * will be put back onto the original gui they were looking that. This mechanic will be active until the player
     * closes the last gui in the crumb, in which they will no longer be in a recognised gui by this library.
     */
    private final List<GUIPage> crumb = new ArrayList<>();

    /**
     * GUIs to ignore.
     */
    private final List<Class<? extends GUIPage>> ignores = new ArrayList<>();

    private final List<InternalType> internalIgnores = new ArrayList<>();
    private final List<Class<? extends GUIPage>> cancels = new ArrayList<>();
    private final List<InternalType> internalCancels = new ArrayList<>();

    public GUIPlayer(PLibrary lib, Player player)
    {
        super(player);
        this.lib = lib;
    }

    /**
     * To check if a player is viewing a gui.
     * @return truue if yes, else false.
     */
    public boolean inGUI()
    {
        return crumb.size() > 0;
    }

    /**
     * Safely opens a gui.
     * @param page - the gui.
     * @param document - if it is a document, it'll add it to the crumb, else it won't.
     */
    public void openPage(GUIPage page, boolean document)
    {
        if (document) {
            crumb.add(page);
        }
        internalIgnores.add(InternalType.PLAYER);
        page.open();
        internalIgnores.remove(InternalType.PLAYER);
    }

    /**
     * Gets the current viewed gui.
     * @return the gui instance.
     */
    public GUIPage currentPage()
    {
        if (!inGUI()) {
            return null;
        }

        return crumb.get(currentIndex());
    }

    /**
     * To copy the crumb.
     * @return copied list of loaded guis.
     */
    public List<GUIPage> copyCrumb()
    {
        return Lists.newArrayList(this.crumb);
    }

    /**
     * Gets the size of the crumb.
     * @return the crumb size.
     */
    private int currentIndex()
    {
        return crumb.size() - 1;
    }

    /**
     * Closes the inventory.
     */
    public void onClose()
    {
        if (inGUI()) crumb.remove(currentIndex());

    }

    /**
     * Invokes InventoryClickEvent on current viewed gui.
     * @param event - the event.
     */
    void onInventoryClick(InventoryClickEvent event)
    {
        if (!inGUI()) return;
        if (event.getClickedInventory() == null) return;
        if (!event.getClickedInventory().equals(this.get().getOpenInventory().getTopInventory())) return;
        currentPage().onClick(event);
    }

    /**
     * Invokes InventoryCloseEvent on current viewed gui.
     * @param event
     */
    void onInventoryClose(InventoryCloseEvent event)
    {
        if (ignoring()) return;
        if (cancelling()) openPage(currentPage(), false);
        if (inGUI()) this.lib.getServer().getScheduler().runTask(this.lib, () -> currentPage().onClose());
    }

    /**
     * Closes the viewed gui, if the player is viewing it.
     */
    public void closePage()
    {
        if (inGUI()) currentPage().onClose();

    }

    /**
     * Closes the viewed gui and the entire crumb.
     * @param closeInventory
     */
    public void closeGUI(boolean closeInventory)
    {
        crumb.clear();
        if (closeInventory) this.get().closeInventory();

    }

    public boolean ignoring()
    {
        return internalIgnores.size() > 0 || ignores.size() > 0;
    }

    public void ignore(GUIPage ignore)
    {
        ignore(ignore.getClass());
    }

    public void ignore(Class<? extends GUIPage> ignore)
    {
        ignores.add(ignore);
    }

    public void stopIgnore(GUIPage ignore)
    {
        stopIgnore(ignore.getClass());
    }

    public void stopIgnore(Class<? extends GUIPage> ignore)
    {
        ignores.remove(ignore);
    }

    public void internalIgnore(InternalType type)
    {
        internalIgnores.add(type);
    }

    public void stopInternalIgnore(InternalType type)
    {
        internalIgnores.remove(type);
    }

    public boolean cancelling()
    {
        return internalCancels.size() > 0 || cancels.size() > 0;
    }

    public void cancel(GUIPage cancel)
    {
        cancel(cancel.getClass());
    }

    public void cancel(Class<? extends GUIPage> cancel)
    {
        cancels.add(cancel);
    }

    public void stopCancel(GUIPage cancel)
    {
        stopCancel(cancel.getClass());
    }

    public void stopCancel(Class<? extends GUIPage> cancel)
    {
        cancels.remove(cancel);
    }

    public void internalCancel(InternalType type)
    {
        internalCancels.add(type);
    }

    public void stopInternalCancel(InternalType type)
    {
        internalCancels.remove(type);
    }
}
