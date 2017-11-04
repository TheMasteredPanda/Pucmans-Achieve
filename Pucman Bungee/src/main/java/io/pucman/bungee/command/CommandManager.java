package io.pucman.bungee.command;

import io.pucman.bungee.PLibrary;
import io.pucman.bungee.manager.Manager;
import net.md_5.bungee.api.plugin.Plugin;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Command manager for Pucman Commands.
 *
 * @see PucmanCommand
 */
@ParametersAreNonnullByDefault
public class CommandManager extends Manager<PLibrary>
{

    public CommandManager(PLibrary instance)
    {
        super(instance, Priority.HIGH);
    }

    /**
     * To register commands.
     * @param instance - the instance of the plugin the command is written for.
     * @param commands - the command array.
     * @param <P> - the plugin generic type.
     */
    public <P extends Plugin> void register(P instance, PucmanCommand... commands)
    {
        for (PucmanCommand command : commands) {
            this.instance.getLogger().info("Registering command " + command.getCommandPath() + ".");
            this.instance.getPluginManager().registerCommand(instance, command);
        }
    }
}
