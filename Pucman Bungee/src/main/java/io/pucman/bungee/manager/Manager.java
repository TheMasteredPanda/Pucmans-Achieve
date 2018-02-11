package io.pucman.bungee.manager;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Used for classes of with will be the manager of a
 * library section.
 * @param <P>
 */
public class Manager<P extends ManagingPlugin>
{
    protected P instance;
    private AtomicBoolean enable = new AtomicBoolean(false);

    @Getter
    private Priority priority;

    public Manager(P instance, Priority priority)
    {
        this.instance = instance;
        this.priority = priority;
    }

    /**
     * Invoked when all managers are being enabled.
     */
    protected void init()
    {
        if (!enable.get()) {
            onEnable();
            enable.set(true);
        }
    }

    /**
     * Invoked when all managers are being disabled.
     */
    public void shutdown()
    {
        if (enable.get()) {
            onDisable();
            enable.set(false);
        }
    }

    public boolean isEnabled()
    {
        return enable.get();
    }

    public void onEnable()
    {
    }

    public void onDisable()
    {
    }

    /**
     * HIGH - first managers to be enabled.
     * NORMAL - last managers to be enabled.
     * This applies to the disabling of managers as well.
     */
    public enum Priority
    {
        NORMAL, HIGH
    }
}
