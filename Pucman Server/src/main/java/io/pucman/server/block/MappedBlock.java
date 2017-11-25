package io.pucman.server.block;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@AllArgsConstructor
@Getter
public class MappedBlock
{
    private Action  action;
    private Class<? extends Event> event;
}
