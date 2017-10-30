package io.pucman.server.conversation.event;

import io.pucman.server.conversation.Conversation;
import io.pucman.server.conversation.ConversationContext;

/**
 * Invoked when the conversation ends.
 */
@FunctionalInterface
public interface ConversationEndedEvent
{
    /**
     * Invoked when the conversation ends in any capacity.
     * @param context
     * @param conversationState
     */
    void abandon(ConversationContext context, Conversation.State conversationState);
}
