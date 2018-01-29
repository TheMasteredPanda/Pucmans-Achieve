package io.pucman.bungee.player;

import io.pucman.common.exception.UtilException;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Collection;
import java.util.UUID;

/**
 * Player utility class.
 */
public final class Players
{
    private static ProxyServer proxy = ProxyServer.getInstance();

    private Players()
    {
        throw new UtilException();
    }

    /**
     * Get a player by their UUID.
     * @param player - player UUID.
     * @return player, else null.
     */
    public static ProxiedPlayer get(UUID player)
    {
        return proxy.getPlayer(player);
    }

    /**
     * Get a player by their name.
     * @param name - player name.
     * @return player, else null.
     */
    public static ProxiedPlayer get(String name)
    {
        return proxy.getPlayer(name);
    }

    /**
     * Gets all the player online.
     * @return player collection.
     */
    public static Collection<ProxiedPlayer> online()
    {
        return proxy.getPlayers();
    }
}
