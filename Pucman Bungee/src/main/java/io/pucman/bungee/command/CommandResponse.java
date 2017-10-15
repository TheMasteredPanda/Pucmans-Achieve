package io.pucman.bungee.command;

import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class CommandResponse
{
    private final Type type;
    private Map<String, Object> data = Maps.newLinkedHashMap();

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
