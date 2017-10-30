package io.pucman.server.conversation.action.block;

import org.bukkit.event.block.BlockPlaceEvent;

public abstract class BlockPlaceAction extends BlockAction<BlockPlaceEvent>
{
    public BlockPlaceAction()
    {
        super(BlockPlaceEvent.class);
    }
}