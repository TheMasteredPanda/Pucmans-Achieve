package io.pucman.server.conversation.event;

import io.pucman.server.conversation.Conversation;
import io.pucman.server.conversation.ConversationContext;

public class ForciblyDroppedAllConversationsAbandonEvent implements ConversationEndedEvent
{
    @Override
    public void abandon(ConversationContext context, Conversation.State conversationState)
    {
        context.getForWhom().send("Current conversation ended forcibly by server.", false);
    }
}
