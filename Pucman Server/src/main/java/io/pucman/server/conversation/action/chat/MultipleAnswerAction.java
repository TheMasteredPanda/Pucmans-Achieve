package io.pucman.server.conversation.action.chat;

import com.google.common.collect.ImmutableList;

public abstract class MultipleAnswerAction extends ChatAction
{
    private ImmutableList<String> answers;

    public MultipleAnswerAction(String... answers)
    {
        this.answers = ImmutableList.of(answers);
    }

    @Override
    public boolean validate(Object inputType)
    {
        return this.answers.contains((String) inputType);
    }
}
