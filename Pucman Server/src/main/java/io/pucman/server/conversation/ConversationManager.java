package io.pucman.server.conversation;

import com.google.common.collect.Maps;
import io.pucman.server.PLibrary;
import io.pucman.server.conversation.conversable.Conversable;
import io.pucman.server.manager.Manager;

import java.util.HashMap;
import java.util.UUID;

public class ConversationManager extends Manager<PLibrary>
{
    private HashMap<UUID, Conversable> conversables = Maps.newHashMap();

    public ConversationManager(PLibrary instance, Priority priority)
    {
        super(instance, priority);
    }

}
