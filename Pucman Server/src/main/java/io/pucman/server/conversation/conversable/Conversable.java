package io.pucman.server.conversation.conversable;

import io.pucman.server.conversation.Conversation;
import io.pucman.server.conversation.event.ConversationEndedEvent;
import lombok.NonNull;

/**
 * Template for conversables.
 */
public interface Conversable
{
    /**
     * Check the conversable is in a conversation or not.
     * @return
     */
    boolean isConversing();

    /**
     * Begin a conversation.
     * @param conversation
     */
    void beginConversation(@NonNull Conversation conversation);

    /**
     * Drop a conversation.
     * @param conversation
     * @param endedEvent
     */
    void dropConversation(@NonNull Conversation conversation, @NonNull ConversationEndedEvent endedEvent);

    /**
     * Drop all queued conversations.
     */
    void dropAllQueuedConversations();

    /**
     * Drop all conversations, including the active one.
     */
    void dropAllConversations();

    /**
     * Send a message to the conversable.
     * @param message
     * @param colored
     */
    void send(@NonNull String message, @NonNull boolean colored);

    /**
     * Get the active conversation.
     * @return the active conversation.
     */
    Conversation getActiveConversation();
}
