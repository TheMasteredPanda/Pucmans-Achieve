package io.pucman.server.conversation.conversable;

import io.pucman.server.conversation.Conversation;

public interface Conversable
{
    boolean isConversing();

    void beginConversation(Conversation conversation);

    void dropConversation(Conversation conversation);

    void dropAllQueuedConversations();

    Conversation getNextQueuedConversation();

    void send(String message, boolean colored);
}
