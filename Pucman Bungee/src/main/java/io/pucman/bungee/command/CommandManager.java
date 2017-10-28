package io.pucman.bungee.command;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import io.pucman.bungee.PLibrary;
import io.pucman.bungee.manager.Manager;
import lombok.AccessLevel;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.Executors;

/**
 * Command manager for Pucman Commands.
 *
 * @see PucmanCommand
 */
@ParametersAreNonnullByDefault
public class CommandManager extends Manager<PLibrary>
{
    /**
     * Used for asynchronous execution of the three bodies in the command wrapper.
     *
     * @see PucmanCommand
     */
    @Getter(value = AccessLevel.PROTECTED)
    private ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());


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
            this.instance.getPluginManager().registerCommand(instance, command);
        }
    }
}
