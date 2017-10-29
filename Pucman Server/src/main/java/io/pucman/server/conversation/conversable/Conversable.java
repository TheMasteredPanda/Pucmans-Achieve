package io.pucman.server.conversation.conversable;

import io.pucman.server.conversation.Conversation;
import io.pucman.server.conversation.event.ConversationEndedEvent;

public interface Conversable
{
    boolean isConversing();

    void beginConversation(Conversation conversation);

    void dropConversation(Conversation conversation, ConversationEndedEvent endedEvent);

    void dropAllQueuedConversations();

    void dropAllConversations();

    void send(String message, boolean colored);

    Conversation getActiveConversation();
}
