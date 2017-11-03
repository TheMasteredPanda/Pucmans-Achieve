package io.pucman.bungee.command;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.CommandSender;

import java.util.LinkedList;
import java.util.Map;

/**
 * The response returned by the main body of the command wrapper.
 *
 * @see PucmanCommand
 */
@Getter
@RequiredArgsConstructor
public class CommandResponse
{
    private final CommandSender sender;
    private final LinkedList<String> arguments;
    private final Type type;
    private Map<String, Object> data = Maps.newLinkedHashMap();

    /**#
     * To add data that will be used within the onFailure and onSuccess method definitions.
     * @param key - key.
     * @param value - value.
     * @return the command response.
     */
    public CommandResponse with(String key, Object value)
    {
        if (!this.data.containsKey(key)) {
            this.data.put(key, value);

        }

        return this;
    }

    public enum  Type
    {
        SUCCESS, FAILURE
    }
}
