package io.pucman.server.conversation;

public interface ConversationCanceller
{
    boolean validate(Object input);

    boolean onCancel(ConversationContext context);
}
