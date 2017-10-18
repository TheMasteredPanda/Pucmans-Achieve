package io.pucman.server.command;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import io.pucman.common.test.reflect.ReflectUtil;
import io.pucman.common.test.reflect.accessors.FieldAccessor;
import io.pucman.server.manager.Manager;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.Executors;

public class CommandManager<P extends JavaPlugin> extends Manager<P>
{
    protected ListeningExecutorService service;
    private SimpleCommandMap commandMap;

    public CommandManager(P instance)
    {
        super(instance, Priority.HIGH);
        this.service = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

        if (this.commandMap == null) {
            SimplePluginManager simplePluginManager = (SimplePluginManager) this.getInstance().getServer().getPluginManager();
            FieldAccessor<SimpleCommandMap> accessor = ReflectUtil.get(simplePluginManager.getClass(), "commandMap", ReflectUtil.Type.DECLARED);
            accessor.get().setAccessible(true);
            this.commandMap = accessor.get(simplePluginManager);
        }
    }
}
