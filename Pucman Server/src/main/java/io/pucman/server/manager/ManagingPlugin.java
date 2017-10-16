package io.pucman.server.manager;

import com.google.common.collect.HashMultimap;
import io.pucman.common.generic.GenericUtil;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class ManagingPlugin extends JavaPlugin
{
    @Getter
    private HashMultimap<Manager.Priority, Manager> managers = HashMultimap.create();

    public void load(Manager... managers)
    {
        Arrays.stream(managers).forEach(m -> this.managers.put(m.getPriority(), m));
    }

    public void enableManagers()
    {
        this.managers.get(Manager.Priority.HIGH).forEach(Manager::init);
        this.managers.get(Manager.Priority.NORMAL).forEach(Manager::init);
    }

    public void disableManagers()
    {
        this.managers.get(Manager.Priority.HIGH).forEach(Manager::shutdown);
        this.managers.get(Manager.Priority.NORMAL).forEach(Manager::shutdown);
    }

    public <M extends Manager> M get(Class<M> manager)
    {
        return this.managers.values().stream().filter(m -> m.getClass().equals(manager)).findFirst().<M>map(GenericUtil::cast).orElse(null);
    }
}
