package io.pucman.server.manager;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.atomic.AtomicBoolean;

public class Manager<P extends JavaPlugin>
{
    @Getter
    private P instance;

    @Getter
    private Priority priority;

    @Getter
    private AtomicBoolean enabled = new AtomicBoolean(false);

    public Manager(P instance, Priority priority)
    {
        this.instance = instance;
        this.priority = priority;
    }

    public enum Priority
    {
        HIGH, NORMAL
    }

    public void init()
    {
        if (!this.enabled.get()) {
            this.onEnable();
            this.enabled.set(true);
        }
    }

    public void shutdown()
    {
        if (this.enabled.get()) {
            this.onDisable();
            this.enabled.set(false);
        }
    }

    public void onEnable()
    {
    }

    public void onDisable()
    {
    }
}
