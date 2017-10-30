package io.pucman.server.conversation.action.block;

import io.pucman.common.generic.GenericUtil;
import io.pucman.server.conversation.action.Action;
import io.pucman.server.conversation.conversable.Conversable;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockEvent;

/**
 * Base class for actions that work with blocks only.
 * @param <E> - used in specifying the correct parameter
 *          type in the method declaration at the bottom.
 *
 * @see Action
 */
public abstract class BlockAction<E extends BlockEvent> extends Action<Block> implements Listener
{
    private Conversable conversable = this.getContext().getForWhom();
    private Class<E> blockEvent;

    public BlockAction(Class<E> blockEvent)
    {
        this.blockEvent = blockEvent;
    }

    @EventHandler
    public final void onBlockEvent(BlockEvent e)
    {
        if (!e.getEventName().equals(blockEvent.getName())) {
            return;
        }

        if (!this.hasStarted() || !this.isAwaitingInput()) {
            return;
        }

        this.on(GenericUtil.cast(e, blockEvent));
    }

    public abstract void on(E event);
}
