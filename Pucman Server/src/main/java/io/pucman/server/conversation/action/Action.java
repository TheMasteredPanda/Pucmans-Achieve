package io.pucman.server.conversation.action;

import io.pucman.server.conversation.ConversationContext;
import lombok.AccessLevel;
import lombok.Getter;

public abstract class Action<I>
{
    public static final Action END_OF_CONVERSATION = null;

    @Getter
    private ConversationContext context;

    @Getter
    private boolean awaitingInput = false;

    @Getter
    private boolean started = false;

    public void init(ConversationContext context)
    {
        this.context = context;
        this.ask();
    }

    public abstract void ask();

    public abstract boolean validate(I inputType);

    public abstract Action onValidationSuccess(I input);

    public abstract Action onValidationFail(I input);
}
