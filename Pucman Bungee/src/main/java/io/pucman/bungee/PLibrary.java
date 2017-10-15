package io.pucman.bungee;

import io.pucman.bungee.command.CommandManager;
import io.pucman.bungee.manager.ManagingPlugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class PLibrary extends ManagingPlugin
{
    private static PLibrary instance;

    @Override
    public void onLoad()
    {
        instance = this;
        this.load(new CommandManager(this));
    }

    @Override
    public void onEnable()
    {
        this.enableManagers();
    }

    @Override
    public void onDisable()
    {
        this.disableManagers();
    }

    public static PLibrary get()
    {
        return instance;
    }

    public PluginManager getPluginManager()
    {
        return this.getProxy().getPluginManager();
    }
}
