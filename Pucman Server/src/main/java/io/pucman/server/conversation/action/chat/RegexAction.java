package io.pucman.server.conversation.action.chat;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.regex.Pattern;

public abstract class RegexAction<P extends JavaPlugin> extends ChatAction
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
