package io.pucman.server.block;

import com.google.common.collect.ArrayListMultimap;
import com.google.inject.Singleton;
import io.pucman.common.exception.DeveloperException;
import io.pucman.server.PLibrary;
import io.pucman.server.locale.Locale;
import io.pucman.server.manager.Manager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.ThreadSafe;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


@ThreadSafe
@Singleton
@ParametersAreNonnullByDefault
public class BlockMappingManger extends Manager<PLibrary>
{
    private ArrayListMultimap<Block, MappedBlock> handles;
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public BlockMappingManger(PLibrary instance, Priority priority)
    {
        super(instance, priority);
        this.instance.getPluginManager().registerEvents(new BlockMappingListener(), this.instance);
    }

    @Override
    public void onEnable()
    {
        this.handles = ArrayListMultimap.create();
    }

    public void set(Block block, Action action, Class<? extends Event> event)
    {
        try {

            lock.writeLock().lock();
            List<MappedBlock> mappedBlocks = this.handles.get(block);

            if (mappedBlocks != null) {
                for (MappedBlock mappedBlock : mappedBlocks) {
                    if (!mappedBlock.getEvent().getName().equals(event.getName())) {
                        continue;
                    }

                    throw new DeveloperException("You can't se the same event, on the same block, again.");
                }
            }

            this.handles.put(block, new MappedBlock(action, event));
        } finally {
            lock.writeLock().unlock();
        }
    }

    public class BlockMappingListener implements Listener
    {
        @EventHandler
        public void on(BlockEvent event)
        {
            try {
                lock.readLock().lock();
                List<MappedBlock> mappedBlocks = handles.get(event.getBlock());

                if (mappedBlocks == null) {
                    return;
                }

                for (MappedBlock mappedBlock : mappedBlocks) {
                    if (!mappedBlock.getEvent().getName().equals(event.getEventName())) {
                        continue;
                    }

                    mappedBlock.getAction().on(event);
                }
            } finally {
                lock.readLock().unlock();
            }
        }
    }
}
