package io.pucman.server.player;

import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;

public class PlayerWrapper
{
    private WeakReference<Player> reference;

    public PlayerWrapper(Player player)
    {
        this.reference = new WeakReference<>(player);
    }

    public Player get()
    {
        return this.reference.get();
    }
}
