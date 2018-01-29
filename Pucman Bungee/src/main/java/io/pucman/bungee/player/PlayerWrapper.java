package io.pucman.bungee.player;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.lang.ref.SoftReference;

/**
 * Template class for wrapping the player instance.
 */
public class PlayerWrapper
{
    protected SoftReference<ProxiedPlayer> player;

    public PlayerWrapper(ProxiedPlayer player)
    {
        this.player = new SoftReference<>(player);
    }

    public ProxiedPlayer get()
    {
        return player.get();
    }
}
