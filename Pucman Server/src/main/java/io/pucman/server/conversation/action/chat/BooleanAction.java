package io.pucman.server.conversation.action.chat;

public abstract class BooleanAction extends MultipleAnswerAction
{
    public BooleanAction(String trueAnswer, String falseAnswer)
    {
        super(trueAnswer, falseAnswer);
    }
}
