package io.pucman.server.conversation.event;

import io.pucman.server.conversation.Conversation;
import io.pucman.server.conversation.ConversationContext;

/**
 * @see DefaultConversationAbandonEvent
 */
public class DefaultConversationAbandonEvent implements ConversationEndedEvent
{
    @Override
    public void abandon(ConversationContext context, Conversation.State conversationState)
    {
    }
}
