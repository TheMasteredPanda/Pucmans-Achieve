package io.pucman.server.plugin;

import io.pucman.server.manager.ManagingPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Inherited by main classes of plugins.
 *
 * @see ManagingPlugin
 */
public class LibPlugin extends ManagingPlugin
{
    public PluginManager getPluginManager()
    {
        return this.getServer().getPluginManager();
    }

    public BukkitScheduler getScheduler()
    {
        return this.getServer().getScheduler();
    }

    public Plugin getInstanceOfPlugin(String name)
    {
        if (this.getPluginManager().isPluginEnabled(name)) {
            return this.getPluginManager().getPlugin(name);
        } else {
            return null;
        }
    }
}
