package io.pucman.server.conversation.conversable;

import com.google.common.collect.Queues;
import io.pucman.server.PLibrary;
import io.pucman.server.conversation.Conversation;
import io.pucman.server.conversation.event.ForciblyDroppedAllConversationsAbandonEvent;
import io.pucman.server.locale.Format;
import io.pucman.server.player.PlayerWrapper;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Queue;
import java.util.UUID;
import java.util.logging.Level;

@Getter
public class ConversablePlayer extends PlayerWrapper implements Conversable
{
    private Conversation currentConversation;
    private Queue<Conversation> conversationQueue = Collections.asLifoQueue(Queues.newArrayDeque());

    public ConversablePlayer(Player player)
    {
        super(player);
    }

    public ConversablePlayer(UUID player)
    {
        this(PLibrary.get().getServer().getPlayer(player));
    }

    @Override
    public boolean isConversing()
    {
        return this.currentConversation != null;
    }

    @Override
    public void beginConversation(Conversation conversation)
    {
        if (this.currentConversation != null) {
            this.conversationQueue.add(conversation);
        } else {
            this.currentConversation = conversation;

        }
    }

    @Override
    public void dropConversation(Conversation conversation)
    {
        if (this.currentConversation != null) {
            this.conversationQueue.add(conversation);
            conversation.getContext().getInstance().getLogger().log(Level.INFO, "Added conversation to conversation queue.");
        }
    }

    @Override
    public void dropAllQueuedConversations()
    {
        if (!this.conversationQueue.isEmpty()) {
            this.conversationQueue.clear();
        }
    }

    @Override
    public void dropAllConversations()
    {
        this.dropAllQueuedConversations();
        this.getCurrentConversation().abandon(new ForciblyDroppedAllConversationsAbandonEvent());
    }

    @Override
    public Conversation getNextQueuedConversation()
    {
        //Make sure this is the right method to use to view the next element in the queue, not view and remove it from the queue.
        return this.conversationQueue.peek();
    }

    @Override
    public void send(String message, boolean colored)
    {
        if (!colored) {
            this.get().sendMessage(message);
        } else {
            this.get().sendMessage(Format.color(message));
        }
    }
}
