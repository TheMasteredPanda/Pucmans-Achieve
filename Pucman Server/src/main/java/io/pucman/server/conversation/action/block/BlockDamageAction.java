package io.pucman.server.conversation.action.block;

import org.bukkit.event.block.BlockDamageEvent;

public abstract class BlockDamageAction extends BlockAction<BlockDamageEvent>
{
    public BlockDamageAction()
    {
        super(BlockDamageEvent.class);
    }
}
