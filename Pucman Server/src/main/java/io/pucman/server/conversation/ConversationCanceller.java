package io.pucman.server.conversation;

import io.pucman.server.conversation.action.Action;

public interface ConversationCanceller
{
    boolean validate(Object input);

    Action onCancel(ConversationContext context);
}
