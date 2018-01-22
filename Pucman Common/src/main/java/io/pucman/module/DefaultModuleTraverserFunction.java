package io.pucman.module;

import com.google.common.collect.Lists;

import java.util.LinkedList;

public class DefaultModuleTraverserFunction implements com.google.common.base.Function<Class<? extends Module>, Iterable<Class<? extends Module>>>
{
    private Class<?> managerClass;

    public DefaultModuleTraverserFunction(Class<?> managerClass)
    {
        this.managerClass = managerClass;
    }

    @Override
    public Iterable<Class<? extends Module>> apply(Class<? extends Module> module)
    {
        LinkedList<Class<? extends Module>> dependencies = Lists.newLinkedList();

        if (module.isAnnotationPresent(Dependencies.class)) {
            Dependencies dependencies1 = module.getAnnotation(Dependencies.class);

            for (Class<?> dependency : dependencies1.value()) {
                if (dependency.isAssignableFrom(managerClass)) {
                    continue;
                }

                if (dependency.isAssignableFrom(Dependencies.class)) {
                    apply((Class<? extends Module>) dependency);
                }

                dependencies.add((Class<? extends Module>) dependency);
            }

            dependencies.add(module);
        }

        return dependencies;
    }
}
