package io.pucman.server.command;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A response returned from the main body of the command wrapper.
 *
 * @see PucmanCommand
 */

@Getter
@RequiredArgsConstructor
@ParametersAreNonnullByDefault
public class CommandResponse
{
    private final CommandSender sender;
    private final LinkedList<String> arguments;
    private final Type type;
    private Map<String, Object> data = Maps.newHashMap();

    /**
     * To add data to the map to be passed to the next respective body.
     * @param key - key.
     * @param value - value.
     * @return this.
     */
    public CommandResponse with(String key, Object value)
    {
        this.data.put(key, value);
        return this;
    }

    /**
     * For determining the type of command body that should be invoked after the main body has been invoked.
     */
    public enum Type
    {
        SUCCESS, FAIL
    }
}
