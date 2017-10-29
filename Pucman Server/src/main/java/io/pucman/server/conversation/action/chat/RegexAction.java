package io.pucman.server.conversation.action.chat;

import java.util.regex.Pattern;

public abstract class RegexAction extends ChatAction
{
    private Pattern pattern;

    public RegexAction(Pattern pattern)
    {
        this.pattern = pattern;
    }

    @Override
    public boolean validate(Object inputType)
    {
        return this.pattern.matcher((String) inputType).matches();
    }
}
