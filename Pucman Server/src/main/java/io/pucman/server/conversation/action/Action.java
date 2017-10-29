package io.pucman.server.conversation.action;

import io.pucman.server.conversation.ConversationContext;
import lombok.Getter;
import lombok.Setter;

public abstract class Action<I>
{
    public static final Action END_OF_CONVERSATION = null;

    @Getter
    private ConversationContext context;

    @Getter @Setter
    private boolean awaitingInput = false;

    private boolean started = false;

    public boolean hasStarted()
    {
        return this.started;
    }

    public void init(ConversationContext context)
    {
        this.context = context;
        this.ask();
        this.awaitingInput = true;
        this.started = true;
    }

    public abstract void ask();

    public abstract boolean validate(I inputType);

    public abstract Action onValidationSuccess(I input);

    public abstract Action onValidationFail(I input);
}
