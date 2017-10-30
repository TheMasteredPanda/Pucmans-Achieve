package io.pucman.server.conversation.action.block;

import org.bukkit.event.block.BlockDamageEvent;

/**
 * For actions that are invoked on a block damaging input.
 *
 * @see BlockAction
 */
public abstract class BlockDamageAction extends BlockAction<BlockDamageEvent>
{
    public BlockDamageAction()
    {
        super(BlockDamageEvent.class);
    }
}
