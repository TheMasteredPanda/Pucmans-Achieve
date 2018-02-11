package io.pucman.bungee.command;

import io.pucman.bungee.PLibrary;
import io.pucman.bungee.manager.Manager;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Manages the registration of pucman command instances.
 */
public class CommandManager extends Manager<PLibrary>
{
    @Getter(AccessLevel.PROTECTED)
    private ExecutorService service;

    public CommandManager(PLibrary instance)
    {
        super(instance, Priority.HIGH);
        service = Executors.newFixedThreadPool(instance.getMainConfig().get(Integer.class, "Command.ThreadPoolSize"));
    }
}
