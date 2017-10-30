package io.pucman.server.conversation.action.block;

import org.bukkit.event.block.BlockBreakEvent;

public abstract class BlockBreakAction extends BlockAction<BlockBreakEvent>
{
    public BlockBreakAction()
    {
        super(BlockBreakEvent.class);
    }
}
