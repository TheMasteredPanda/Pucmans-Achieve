package io.pucman.server.conversation.action.block;

import org.bukkit.event.block.BlockBreakEvent;

/**
 * For actions that are invoked upon a block breaking related input.
 *
 * @see BlockAction
 */
public abstract class BlockBreakAction extends BlockAction<BlockBreakEvent>
{
    public BlockBreakAction()
    {
        super(BlockBreakEvent.class);
    }
}
