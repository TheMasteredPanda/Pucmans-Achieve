package io.pucman.bungee.module;

import io.pucman.module.Module;
import io.pucman.module.ModuleInfo;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

/**
 * Base Module for Modules on a BungeeCord Plugin.
 * @param <T> - plugin main class.
 *
 * @see ModuleManager
 */
public class BaseModule<T extends Plugin> implements Module
{
    private ModuleInfo info;
    private T instance;
    private AtomicBoolean enabled = new AtomicBoolean(false);
    private ReentrantLock lock = new ReentrantLock();

    public BaseModule(T instance, String name, String version, boolean immutableModule, String... authors)
    {
        this.instance = instance;
        info = new ModuleInfo(authors, version, name, immutableModule);
    }

    /**
     * @return module information instance.
     */
    @Override
    public ModuleInfo getInfo()
    {
        return info;
    }

    /**
     * Boots the module.
     */
    @Override
    public void boot()
    {
        try {
            lock.lock();
            if (enabled.get()) {
                instance.getLogger().log(Level.WARNING, "Attempted to enable " + info.getName() + " but it was already enabled.");
                return;
            }

            enable();
            enabled.set(true);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Shuts down module.
     */
    @Override
    public void shutdown()
    {
        try {
            lock.lock();
            if (!enabled.get()) {
                instance.getLogger().log(Level.WARNING, "Attempted to disable " + info.getName() + " but it was already disabled.");
                return;
            }

            disable();
            enabled.set(false);
        } finally {
            lock.unlock();
        }
    }

    /**
     * @return true if enabled, else false.
     */
    @Override
    public boolean isEnabled()
    {
        return enabled.get();
    }

    public void enable()
    {
    }

    public void disable()
    {
    }
}
