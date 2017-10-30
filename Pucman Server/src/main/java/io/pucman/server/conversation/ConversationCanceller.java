package io.pucman.server.conversation;

/**
 * Conversation canceller. If the conditions of the input are correct this will cancel the the active conversation it has been binded to.
 */
public interface ConversationCanceller
{
    /**
     * To validate if the input aligns with the conditions of the method definition.
     * @param input - player input.
     * @return if the input is valid it will return true, else value.
     */
    boolean validate(Object input);

    /**
     * Invoked when the players input is valid with this canceller.
     * @param context - the conversation context of the conversation the canceller has cancelled.
     */
    void onCancel(ConversationContext context);
}
