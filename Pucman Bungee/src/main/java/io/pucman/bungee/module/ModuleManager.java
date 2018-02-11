package io.pucman.bungee.module;

import com.google.common.collect.Lists;
import com.google.common.collect.TreeTraverser;
import io.pucman.bungee.PLibrary;
import io.pucman.bungee.manager.Manager;
import io.pucman.common.exception.DeveloperException;
import io.pucman.common.reflect.ReflectUtil;
import io.pucman.common.reflect.accessors.ConstructorAccessor;
import io.pucman.module.DefaultModuleTraverserFunction;
import io.pucman.module.Dependencies;
import io.pucman.module.Module;
import net.md_5.bungee.api.plugin.Plugin;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manager for managing modules.
 */
@ParametersAreNonnullByDefault
public class ModuleManager extends Manager<PLibrary>
{
    private TreeMap<Plugin, List<Class<? extends Module>>> dependencyMap = new TreeMap<>();
    private TreeMap<Plugin, List<Module>> loadedModules = new TreeMap<>();
    private TreeTraverser traverser = TreeTraverser.using(new DefaultModuleTraverserFunction(Manager.class));
    private ArrayList<Class<? extends Module>> cannotLoad = Lists.newArrayList();

    public ModuleManager()
    {
        super(PLibrary.get(), Priority.HIGH);
    }

    /**
     * Registers the immutable array.
     * @param plugin - plugin the modules belong to.
     * @param modules - array of modules.
     */
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

    /**
     * Get the load order of all the modules on the server.
     * @return load order.
     */
    public TreeMap<Plugin, List<Class<? extends Module>>> getLoadOrder()
    {
        return dependencyMap.descendingKeySet().stream().collect(Collectors.toMap(key -> key, this::getLoadOrder, (a, b) -> b, TreeMap::new));
    }

    /**
     * Gets the load order of the modules.
     * @param plugin - plugin the modules belong to.
     * @return load order.
     */
    public LinkedList<Class<? extends Module>> getLoadOrder(Plugin plugin)
    {
        List<Class<? extends Module>> modules = this.dependencyMap.get(plugin);
        Collections.reverse(modules);
        return Lists.newLinkedList(modules);
    }

    /**
     * Boots all the modules belonging to the plugin.
     * @param plugin - plugin instance.
     */
    public void boot(Plugin plugin)
    {
        List<Class<? extends Module>> modules = dependencyMap.get(plugin);

        if (modules == null || modules.isEmpty()) {
            return;
        }

        if (!loadedModules.containsKey(plugin)) loadedModules.put(plugin, Lists.newLinkedList());

        for (Class<? extends Module> entry : modules) {
            if (cannotLoad.contains(entry)) {
                instance.getLogger().info("Cannot load module " + entry.getSimpleName() + ". Seems like on of it's dependencies has problems loading.");
                //TODO: display the dependencies of the module that has trouble loading.
            }


            try {
                ConstructorAccessor<? extends Module> accessor = ReflectUtil.getConstructor(entry, ReflectUtil.Type.DECLARED, Plugin.class);
                Module m = accessor.call(plugin);
                loadedModules.get(plugin).add(m);
            } catch (Exception e) {
                e.printStackTrace();
                //TODO: store the exception in the a map. Make a commend to display a formatted error on the server.
                //TODO: or make a Bugsnag instance to direct all stacktraces too.

                cannotLoad.add(entry);
                cannotLoad.addAll(getModulesDepending(entry));
            }
        }
    }


    public void shutdown(Plugin plugin)
    {
        loadedModules.get(plugin).forEach(this::shutdown);
    }

    /**
     * Gets a list of modules depending on the dependency.
     * @param dependency - module other modules may be depending on.
     * @return a list of modules depending on that particular dependency.
     */
    public List<Class<? extends Module>> getModulesDepending(Class<? extends Module> dependency)
    {
        if (!dependency.isAnnotationPresent(Dependencies.class)) {
            return Lists.newArrayList();
        }

        Dependencies dependencies = dependency.getAnnotation(Dependencies.class);
        List<Class<? extends Module>> modules = Lists.newLinkedList();

        for (Class<?> depend : dependencies.value()) {
            if (depend.isAssignableFrom(Manager.class)) {
                continue;
            }

            modules.add((Class<? extends Module>) depend);
        }

        return modules;
    }

    /**
     * Gets a list of dependencies the module depends on.
     * @param module - module to examine.
     * @return dependency list.
     */
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

    /**
     * Initiates the shutdown process of the module passed in the parameters.
     *
     * The module will also check and, if successful, shutdown the dependencies
     * of the module that will be shutdown to remove the concern of ram being
     * unnecessarily used up.
     *
     * @param module - module to be shutdown.
     */
    public void shutdown(Module module)
    {
        if (module.getInfo().isImmutableModule()) {
            throw new DeveloperException("Cannot shutdown module " + module.getInfo().getName() + ". It is an immutable module.");
        }

        List<Class<? extends Module>> dependencies = getOnlyModuleDependencies(module.getClass());

        if (!dependencies.isEmpty()) {
            for (Class<? extends Module> dependency : dependencies) {
                Module depend = null;

                for (List<Module> modules : loadedModules.values()) {
                    if (modules.stream().anyMatch(singleModule -> singleModule.getClass().equals(dependency))) {
                        depend = module;
                        break;
                    }
                }

                if (depend == null) {
                    instance.getLogger().warning("Could not get loaded dependency " + dependency.getSimpleName() + ". Maybe it wasn't loaded at all?");
                    continue;
                }

                if (dependedBy(dependency) < 2) {
                    shutdown(depend);
                } else {
                    instance.getLogger().warning("Could not shutdown module " + dependency.getSimpleName() + " as it is being used by one or more loaded module.");
                }
            }
        }

        module.shutdown();
    }

    /**
     * Gets the amount of modules depending on this dependency.
     * @param module - module to examine.
     * @return amount of modules depending.
     */
    public int dependedBy(Class<? extends Module> module)
    {
        return loadedModules.values().stream().mapToInt(modules -> (int) modules.stream().filter(singleModule -> singleModule.getClass().isAnnotationPresent(Dependencies.class)).map(singleModule -> singleModule.getClass().getAnnotation(Dependencies.class)).flatMap(dependencies -> Arrays.stream(dependencies.value())).filter(depend -> depend.getClass().equals(module)).count()).sum();
    }
}
