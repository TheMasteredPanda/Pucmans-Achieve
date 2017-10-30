package io.pucman.server.conversation.action.block;

import org.bukkit.event.block.BlockPlaceEvent;

/**
 * For actions that are invoked upon a block placing input.
 *
 * @see BlockAction
 */
public abstract class BlockPlaceAction extends BlockAction<BlockPlaceEvent>
{
    public BlockPlaceAction()
    {
        super(BlockPlaceEvent.class);
    }
}