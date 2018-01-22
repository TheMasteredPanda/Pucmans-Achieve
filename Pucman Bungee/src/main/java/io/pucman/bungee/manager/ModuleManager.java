package io.pucman.bungee.manager;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.TreeTraverser;
import io.pucman.bungee.PLibrary;
import io.pucman.common.reflect.ReflectUtil;
import io.pucman.common.reflect.accessors.ConstructorAccessor;
import io.pucman.module.DefaultModuleTraverserFunction;
import io.pucman.module.Dependencies;
import io.pucman.module.Module;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.*;

public class ModuleManager extends Manager<PLibrary>
{
    private TreeMap<Plugin, List<Class<? extends Module>>> dependencyMap = Maps.newTreeMap(Collections.reverseOrder());
    private TreeTraverser traverser = TreeTraverser.using(new DefaultModuleTraverserFunction(Manager.class));
    private HashMap<Plugin, Module> loadedModules = Maps.newHashMap();
    private ArrayList<Class<? extends Module>> cannotLoad = Lists.newArrayList();

    public ModuleManager(PLibrary instance)
    {
        super(instance, Priority.HIGH);
    }

    public void register(Plugin plugin, Class<? extends Module>... modules)
    {
        ArrayList<Class<? extends Module>> examineList = Lists.newArrayList(modules);

        if (dependencyMap.containsKey(plugin)) {
            examineList.addAll(dependencyMap.get(plugin));
            dependencyMap.put(plugin, traverser.breadthFirstTraversal(examineList).toList());
            return;
        }

        dependencyMap.put(plugin, traverser.breadthFirstTraversal(examineList).toList());
    }

    public TreeMap<Plugin, List<Class<? extends Module>>> getLoadOrder()
    {
        return dependencyMap;
    }

    public LinkedList<Class<? extends Module>> getLoadOrder(Plugin plugin)
    {
        List<Class<? extends Module>> modules = this.dependencyMap.get(plugin);
        Collections.reverse(modules);
        return Lists.newLinkedList(modules);
    }

    public void boot(Plugin plugin)
    {
        List<Class<? extends Module>> modules = dependencyMap.get(plugin);

        if (modules == null || modules.isEmpty()) {
            return;
        }

        for (Class<? extends Module> entry : modules) {
            if (cannotLoad.contains(entry)) {
                instance.getLogger().info("Cannot load module " + entry.getSimpleName() + ". Seems like on of it's dependencies has problems loading.");
                //TODO: display the dependencies of the module that has trouble loading.
            }


            try {
                ConstructorAccessor<? extends Module> accessor = ReflectUtil.getConstructor(entry, ReflectUtil.Type.DECLARED);
                Module m = accessor.call();
                loadedModules.put(plugin, m);
            } catch (Exception e) {
                //TODO: store the exception in the a map. Make a commend to display a formatted error on the server.
                //TODO: or make a Bugsnag instance to direct all stacktraces to.

                cannotLoad.add(entry);
                cannotLoad.addAll(getOnlyModuleDependencies(entry));
            }
        }
    }

    public List<Class<? extends Module>> getOnlyModuleDependencies(Class<? extends Module> module)
    {
        if (!module.isAnnotationPresent(Dependencies.class)) {
            return Lists.newArrayList();
        }

        Dependencies dependencies = module.getAnnotation(Dependencies.class);
        ArrayList<Class<? extends Module>> list = Lists.newArrayList();

        for (Class<?> depend : dependencies.value()) {
            if (depend.isAssignableFrom(Manager.class)) {
                continue;
            }

            list.add((Class<? extends Module>) depend);
        }

        return list;
    }

}
