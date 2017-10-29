package io.pucman.server.conversation;

import com.google.common.collect.Maps;
import io.pucman.common.generic.GenericUtil;
import io.pucman.server.PLibrary;
import io.pucman.server.conversation.conversable.Conversable;
import io.pucman.server.conversation.conversable.ConversablePlayer;
import io.pucman.server.manager.Manager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;

public class ConversationManager<C extends Conversable> extends Manager<PLibrary> implements Listener
{
    private HashMap<UUID, C> conversables;

    public ConversationManager(PLibrary instance, Priority priority)
    {
        super(instance, priority);
        conversables = Maps.newHashMap();
    }

    @Override
    public void onEnable()
    {
        this.instance.getPluginManager().registerEvents(this, this.instance);
    }

    @Override
    public void onDisable()
    {
        HandlerList.unregisterAll(this);

    }

    public void register(UUID uuid, Conversable conversable)
    {
        if (!this.conversables.containsKey(uuid)) {
            this.conversables.put(uuid, GenericUtil.cast(conversable));
        }
    }

    public void unregister(UUID uuid)
    {
        if (!this.conversables.containsKey(uuid)) {
            Conversable conversable = this.conversables.get(uuid);
            conversable.dropAllConversations();
            this.unregister(uuid);
            this.conversables.remove(uuid);
        }
    }

    @EventHandler
    public void on(PlayerJoinEvent e)
    {
        this.register(e.getPlayer().getUniqueId(), new ConversablePlayer(e.getPlayer()));
    }

    @EventHandler
    public void on(PlayerQuitEvent e)
    {
        this.unregister(e.getPlayer().getUniqueId());
    }
}