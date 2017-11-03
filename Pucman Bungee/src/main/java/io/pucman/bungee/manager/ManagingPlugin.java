package io.pucman.bungee.manager;

import com.google.common.collect.HashMultimap;
import io.pucman.common.exception.DeveloperException;
import io.pucman.common.generic.GenericUtil;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.Arrays;

/**
 * Used for main classes of plugins.
 *
 * @see io.pucman.bungee.plugin.LibPlugin
 * @see io.pucman.bungee.PLibrary
 */
public class ManagingPlugin extends Plugin
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
    public synchronized void enableManagers()
    {
        this.managers.get(Manager.Priority.HIGH).forEach(Manager::init);
        this.managers.get(Manager.Priority.NORMAL).forEach(Manager::init);
    }

    /**
     * Disables managers.
     */
    public synchronized void disableManagers()
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
        Manager managerInstance = this.managers.values().stream().filter(m -> m.getClass().equals(manager)).findFirst().<M>map(GenericUtil::cast).orElse(null);

        if (managerInstance== null) {
            System.out.println("m is null, tried to get " + manager.getName() + " manager. Currency size of loaded managers list is: " + String.valueOf(this.managers.size()));
            throw new DeveloperException("m is null.");
        }

        return GenericUtil.cast(managerInstance);
    }
}
