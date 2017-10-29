package io.pucman.server.conversation.conversable;

import com.google.common.collect.Queues;
import io.pucman.server.PLibrary;
import io.pucman.server.conversation.Conversation;
import io.pucman.server.conversation.event.ConversationEndedEvent;
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
        return !this.conversationQueue.isEmpty();
    }

    @Override
    public void beginConversation(Conversation conversation)
    {
        if (!this.conversationQueue.isEmpty()) {
            this.conversationQueue.add(conversation);
            this.conversationQueue.peek().begin(this);
            conversation.getContext().getInstance().getLogger().log(Level.INFO, "Added conversation to conversation queue.");
        } else {
            this.conversationQueue.add(conversation);
        }
    }

    @Override
    public void dropConversation(Conversation conversation, ConversationEndedEvent endedEvent)
    {
        if (this.conversationQueue.peek() == conversation) {
            this.conversationQueue.peek().abandon(endedEvent);
            this.conversationQueue.remove();
        }

        if (!this.conversationQueue.isEmpty()) {
            this.conversationQueue.peek().begin(this);
        }
    }

    @Override
    public void dropAllQueuedConversations()
    {
        if (!this.conversationQueue.isEmpty()) {
            this.conversationQueue.forEach(conversation -> conversation.abandon(new ForciblyDroppedAllConversationsAbandonEvent()));
        }
    }

    @Override
    public void dropAllConversations()
    {
        this.dropAllQueuedConversations();
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
