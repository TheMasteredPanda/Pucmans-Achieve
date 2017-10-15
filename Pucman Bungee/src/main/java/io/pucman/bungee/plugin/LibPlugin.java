package io.pucman.bungee.plugin;

import io.pucman.bungee.PLibrary;
import io.pucman.bungee.command.CommandManager;
import io.pucman.bungee.command.PucmanCommand;
import io.pucman.bungee.manager.ManagingPlugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.scheduler.TaskScheduler;

/**
 * Inherited by main classes of plugins.
 *
 * @see ManagingPlugin
 */
public class LibPlugin extends ManagingPlugin
{
    public PluginManager getPluginManager()
    {
        return this.getProxy().getPluginManager();
    }

    public TaskScheduler getScheduler()
    {
        return this.getProxy().getScheduler();
    }

    public void register(PucmanCommand... commands)
    {
        PLibrary.get().get(CommandManager.class).register(this, commands);
    }
}
