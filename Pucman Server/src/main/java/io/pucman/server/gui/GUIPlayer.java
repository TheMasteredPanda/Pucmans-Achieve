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

public class GUIPlayer extends PlayerWrapper
{
    private final PLibrary lib;
    private final List<GUIPage> crumb = new ArrayList<>();
    private final List<Class<? extends GUIPage>> ignores = new ArrayList<>();
    private final List<InternalType> internalIgnores = new ArrayList<>();
    private final List<Class<? extends GUIPage>> cancels = new ArrayList<>();
    private final List<InternalType> internalCancels = new ArrayList<>();

    public GUIPlayer(PLibrary lib, Player player) {
        super(player);
        this.lib = lib;
    }

    public boolean inGUI() {
        return crumb.size() > 0;
    }

    public void openPage(GUIPage page, boolean document) {
        if (document) {
            crumb.add(page);
        }
        internalIgnores.add(InternalType.PLAYER);
        page.open();
        internalIgnores.remove(InternalType.PLAYER);
    }

    public GUIPage currentPage() {
        if (!inGUI()) {
            return null;
        }

        return crumb.get(currentIndex());
    }

    public List<GUIPage> copyCrumb() {
        return Lists.newArrayList(this.crumb);
    }

    private int currentIndex() {
        return crumb.size() - 1;
    }

    public void onClose() {
        if (inGUI()) crumb.remove(currentIndex());

    }

    void onInventoryClick(InventoryClickEvent event) {
        if (!inGUI()) return;

        if (event.getClickedInventory() == null) return;
        if (!event.getClickedInventory().equals(this.get().getOpenInventory().getTopInventory())) return;
        currentPage().onClick(event);
    }

    void onInventoryClose(InventoryCloseEvent event) {
        if (ignoring()) return;
        if (cancelling()) openPage(currentPage(), false);
        if (inGUI()) this.lib.getServer().getScheduler().runTask(this.lib, () -> currentPage().onClose());
    }

    public void closePage() {
        if (inGUI()) currentPage().onClose();

    }

    public void closeGUI(boolean closeInventory) {
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
