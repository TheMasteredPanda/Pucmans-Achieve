package io.pucman.bungee.command;

import io.pucman.bungee.PLibrary;
import io.pucman.bungee.file.BaseFile;
import io.pucman.bungee.manager.Manager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Manages the registration of pucman command instances.
 */
public class CommandManager extends Manager<PLibrary>
{
    @Setter
    private BaseFile file;

    @Getter(AccessLevel.PROTECTED)
    private ExecutorService service;

    public CommandManager(PLibrary instance)
    {
        super(instance, Priority.HIGH);
        service = Executors.newFixedThreadPool(instance.getMainConfig() != null ? instance.getMainConfig().get(Integer.class, "Library.Command.ThreadPoolSize") : file != null ? file.get(Integer.class, "Library.Command.ThreadPoolSize") : 50);
    }

    /**
     * Registers an array of commands.
     * @param commands - commands to register.
     */
    public void register(PucmanCommand... commands)
    {
        for (PucmanCommand command : commands) {
            if (file != null) {
                file.populate(command);
            }

            instance.getPluginManager().registerCommand(command.getInstance(), command);
            instance.getLogger().info("Registered command " + command.getCommandPath() + ".");
        }
    }
}
