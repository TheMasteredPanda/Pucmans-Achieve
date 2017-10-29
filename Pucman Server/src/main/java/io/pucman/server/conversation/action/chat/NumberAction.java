package io.pucman.server.conversation.action.chat;

import io.pucman.common.math.NumberUtil;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class NumberAction<P extends JavaPlugin, N extends Number> extends ChatAction<P, N>
{
    private Class<N> numberType;

    public NumberAction(Class<N> numberType)
    {
        this.numberType = numberType;
    }

    @Override
    public boolean validate(Object inputType)
    {
        return NumberUtil.parseable((String) inputType, numberType);
    }
}
