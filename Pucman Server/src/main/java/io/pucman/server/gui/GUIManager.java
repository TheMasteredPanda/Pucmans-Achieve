package io.pucman.server.gui;

import com.google.common.collect.Maps;
import io.pucman.server.PLibrary;
import io.pucman.server.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;

/**
 * GUI Manager, design taken from insou22 and edited by Duke J. Morgan.
 */
public class GUIManager extends Manager<PLibrary>
{
    private HashMap<UUID, GUIPlayer> players = Maps.newHashMap();

    public GUIManager(PLibrary instance)
    {
        super(instance, Priority.NORMAL);
    }

    @Override
    public void onEnable()
    {
        this.instance.getServer().getPluginManager().registerEvents(new GUIListeners(), this.instance);
        Bukkit.getOnlinePlayers().forEach(this::register);
    }

    /**
     * Register a player.
     * @param player
     */
    public void register(Player player)
    {
        if (!players.containsKey(player.getUniqueId())) players.put(player.getUniqueId(), new GUIPlayer(PLibrary.get(), player));
    }

    /**
     * Unregister a player.
     * @param player
     */
    public void unregister(Player player)
    {
        if (players.containsKey(player.getUniqueId())) players.remove(player.getUniqueId());
    }

    /**
     * Get a wrapped player instance.
     * @param player
     * @return
     */
    public GUIPlayer getPlayer(Player player)
    {
        if (players.containsKey(player.getUniqueId())) {
            return players.get(player.getUniqueId());
        } else {
            register(player);
            return getPlayer(player);
        }
    }

    /**
     * GUI Listeners, invoked when
     */
    public class GUIListeners implements Listener
    {
        @EventHandler
        public void on(PlayerJoinEvent e)
        {
            register(e.getPlayer());
        }

        @EventHandler
        public void on(PlayerQuitEvent e)
        {
            unregister(e.getPlayer());
        }

        @EventHandler
        public void on(InventoryClickEvent e)
        {
            if (!(e.getWhoClicked() instanceof Player)) return;
            GUIPlayer player = getPlayer((Player) e.getWhoClicked());
            if (player.inGUI()) player.onInventoryClick(e);
        }

        @EventHandler
        public void on(InventoryCloseEvent e)
        {
            if (!(e.getPlayer() instanceof Player)) return;
            GUIPlayer player = getPlayer((Player) e.getPlayer());
            if (player.inGUI()) player.onInventoryClose(e);
        }
    }
}
