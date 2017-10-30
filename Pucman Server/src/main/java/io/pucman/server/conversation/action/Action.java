package io.pucman.server.conversation.action;

import io.pucman.server.conversation.ConversationContext;
import lombok.Getter;
import lombok.Setter;

/**
 * Used to define how each conversation will be mapped. Each action can be defined at the developers will, using the same
 * concept as Prompts in the Conversation API in Spigots Server Software.
 * @param <I> - the specific player input.
 */
public abstract class Action<I>
{
    /**
     * Used for marking the ending of a conversation.
     */
    public static final Action END_OF_CONVERSATION = null;

    /**
     * The context of the conversation this action is binded to.
     */
    @Getter
    private ConversationContext context;

    /**
     * Whether the action is awaiting the players input.
     */
    @Getter @Setter
    private boolean awaitingInput = false;

    /**
     * Whether the action is active or not.
     */
    private boolean started = false;

    public boolean hasStarted()
    {
        return this.started;
    }

    /**
     * Invoked when the action is being activated.
     * @param context
     */
    public void init(ConversationContext context)
    {
        this.context = context;
        this.ask();
        this.awaitingInput = true;
        this.started = true;
    }

    /**
     * For printing text to the players chat.
     */
    public abstract void ask();

    /**
     * To validate the players input.
     * @param inputType - players input.
     * @return true if the input is valid, else false.
     */
    public abstract boolean validate(Object inputType);

    /**
     * Invoked if it is the correct input.
     * @param input - players input.
     * @return the next action in the conversation.
     */
    public abstract Action onValidationSuccess(I input);

    /**
     * Invoked if it is not the correct input.
     * @param input - players input.
     * @return the next action in the conversation.
     */
    public abstract Action onValidationFail(I input);
}
