package io.pucman.bungee.manager;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.pucman.common.generic.GenericUtil;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * Used for main classes of plugins.
 *
 * @see io.pucman.bungee.plugin.LibPlugin
 * @see io.pucman.bungee.PLibrary
 */
public class ManagingPlugin extends Plugin
{
    private Multimap<Manager.Priority, Manager> managers = HashMultimap.create();

    /**
     * Add managers to the map.
     * @param managers - array of managers.
     */
    public void load(Manager... managers)
    {
        for (Manager m : managers) {
            this.managers.put(m.getPriority(), m);
        }
    }

    /**
     * Enables managers.
     */
    public void enableManagers()
    {
        for (Manager m : this.managers.get(Manager.Priority.HIGH)) {
            m.init();
        }

        for (Manager m : this.managers.get(Manager.Priority.NORMAL)) {
            m.init();
        }
    }

    /**
     * Disables managers.
     */
    public void disableManagers()
    {
        for (Manager m : this.managers.get(Manager.Priority.HIGH)) {
            m.shutdown();
        }

        for (Manager m : this.managers.get(Manager.Priority.NORMAL)) {
            m.shutdown();
        }
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
