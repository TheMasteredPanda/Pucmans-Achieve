package io.pucman.server;

import io.pucman.server.block.BlockMappingManger;
import io.pucman.server.command.CommandManager;
import io.pucman.server.manager.Manager;
import io.pucman.server.manager.ManagingPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.ScoreboardManager;

public class PLibrary extends ManagingPlugin
{
    private static PLibrary instance;

    @Override
    public void onLoad()
    {
        instance = this;
        this.load(new CommandManager<>(this), new BlockMappingManger(this, Manager.Priority.HIGH));
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
        return this.getServer().getPluginManager();
    }

    public ScoreboardManager getScoreboardManager()
    {
        return this.getServer().getScoreboardManager();
    }

    public BukkitScheduler getScheulder()
    {
        return this.getServer().getScheduler();
    }
}
