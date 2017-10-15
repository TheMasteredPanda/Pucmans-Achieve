package io.pucman.bungee.command;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import io.pucman.bungee.PLibrary;
import io.pucman.bungee.manager.Manager;
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
    protected ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
    public CommandManager(PLibrary instance)
    {
        super(instance, Priority.HIGH);
    }

    public <P extends Plugin> void register(P instance, PucmanCommand... commands)
    {
        for (PucmanCommand command : commands) {
            this.instance.getPluginManager().registerCommand(instance, command);
        }
    }
}
