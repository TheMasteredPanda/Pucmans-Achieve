package io.pucman.server.conversation.event;

import io.pucman.server.conversation.Conversation;
import io.pucman.server.conversation.ConversationContext;

@FunctionalInterface
public interface ConversationEndedEvent
{
    void abandon(ConversationContext context, Conversation.State conversationState);
}
