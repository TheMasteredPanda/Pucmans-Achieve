package io.pucman.bungee;

import io.pucman.bungee.command.CommandManager;
import io.pucman.bungee.def.DebugCommand;
import io.pucman.bungee.file.BaseFile;
import io.pucman.bungee.manager.ManagingPlugin;
import io.pucman.bungee.module.ModuleManager;
import lombok.Getter;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.YamlConfiguration;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class PLibrary extends ManagingPlugin
{
    private static PLibrary instance;

    @Getter
    private BaseFile mainConfig;

    @Getter
    private AtomicBoolean debug = new AtomicBoolean(false);

    @Override
    public void onLoad()
    {
        instance = this;
        this.getPluginManager().registerCommand(this, new DebugCommand());
        this.load(new CommandManager(this), new ModuleManager(this));
        mainConfig = new BaseFile(instance, "config", getDataFolder(), YamlConfiguration.class);
        mainConfig.load();
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
        if (debug.get()) {
            Arrays.stream(messages).forEachOrdered(msg -> this.getLogger().info("[Debug][" + o.toString() + "] " + msg));
        }
    }
}
