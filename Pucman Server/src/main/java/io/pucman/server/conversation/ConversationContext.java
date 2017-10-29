package io.pucman.server.conversation;

import com.google.common.collect.Maps;
import io.pucman.server.conversation.conversable.Conversable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.Map;

/**
 * Used in storing and easy distribution between every action in a conversation.
 * @param <P> - plugin the conversation belongs to.
 */
@Getter
@NotThreadSafe
@AllArgsConstructor
@RequiredArgsConstructor
public class ConversationContext<P extends JavaPlugin>
{
    private final Conversable forWhom;
    private final P instance;
    private Map<String, Object> context = Maps.newHashMap();

}
