package io.pucman.server.manager;

import com.google.common.collect.HashMultimap;
import io.pucman.common.generic.GenericUtil;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

/**
 * Used for main classes of plugins.
 *
 * @see io.pucman.server.plugin.LibPlugin
 * @see io.pucman.server.PLibrary
 */
public class ManagingPlugin extends JavaPlugin
{
    @Getter
    private HashMultimap<Manager.Priority, Manager> managers = HashMultimap.create();

    /**
     * Add managers to the map.
     * @param managers - array of managers.
     */
    public void load(Manager... managers)
    {
        Arrays.stream(managers).forEachOrdered(m -> this.managers.put(m.getPriority(), m));
    }

    /**
     * Enables managers.
     */
    public void enableManagers()
    {
        this.managers.get(Manager.Priority.HIGH).forEach(Manager::init);
        this.managers.get(Manager.Priority.NORMAL).forEach(Manager::init);
    }

    /**
     * Disables managers.
     */
    public void disableManagers()
    {
        this.managers.get(Manager.Priority.HIGH).forEach(Manager::shutdown);
        this.managers.get(Manager.Priority.NORMAL).forEach(Manager::shutdown);
    }

    /**
     * Gets a manager.
     * @param manager - the managers class.
     * @param <M> - the generic type that will be returned.
     * @return the manager if it is present, else null.
     */
    public <M extends Manager> M get(Class<M> manager)
    {
        return this.managers.values().stream().filter(m -> m.getClass().equals(manager)).findFirst().<M>map(GenericUtil::cast).orElse(null);
    }
}

