package io.pucman.bungee;

import io.pucman.bungee.command.CommandManager;
import io.pucman.bungee.manager.ManagingPlugin;
import net.md_5.bungee.api.plugin.PluginManager;

import java.util.Arrays;

public class PLibrary extends ManagingPlugin
{
    private static PLibrary instance;
    private boolean debug = true;

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

    public void debug(Object o, String... messages)
    {
        if (debug) {
            Arrays.stream(messages).forEachOrdered(msg -> this.getLogger().info("[Debug][" + o.toString() + "] " + msg));
        }
    }
}
