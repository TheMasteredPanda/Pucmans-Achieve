package io.pucman.server.locale;

import io.pucman.common.exception.UtilException;
import net.md_5.bungee.api.ChatColor;

public final class Format
{
    private Format()
    {
        throw new UtilException();
    }

    public static String color(String message)
    {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
