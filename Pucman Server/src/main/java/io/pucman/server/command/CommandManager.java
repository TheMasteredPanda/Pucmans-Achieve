package io.pucman.server.command;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import io.pucman.common.reflect.ReflectUtil;
import io.pucman.common.reflect.accessors.FieldAccessor;
import io.pucman.server.PLibrary;
import io.pucman.server.manager.Manager;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.concurrent.Executors;

/**
 * Manager for PucmanCommands.
 * @param <P>
 */
//TODO REDO
public class CommandManager<P extends JavaPlugin> extends Manager<PLibrary>
{
    /**
     * Executor service used for execution various bodies of the PucmanCommand wrapper.
     */
    protected ListeningExecutorService service;

    public CommandManager(PLibrary instance)
    {
        super(instance, Priority.HIGH);
        this.service = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
    }

    /**
     * Registers an array of commands.
     * @param commands - the PucmanCommands.
     */
    public void register(P instance, PucmanCommand... commands)
    {
        Arrays.stream(commands).forEachOrdered(cmd -> instance.getCommand(cmd.getMainAlias()).setExecutor(cmd);
    }
}
