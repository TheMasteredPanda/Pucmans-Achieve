package io.pucman.server.command;

import io.pucman.server.PLibrary;
import io.pucman.server.manager.Manager;
import org.bukkit.plugin.java.JavaPlugin;

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
     * @param commands - the command array.
     * @param <P> - the plugin generic type.
     */
    public <P extends JavaPlugin> void register(PucmanCommand... commands)
    {
        for (PucmanCommand command : commands) {
            this.instance.getLogger().info("Registering command " + command.getCommandPath() + ".");
            this.instance.getCommand(command.getMainAlias()).setExecutor(command);
        }
    }
}
