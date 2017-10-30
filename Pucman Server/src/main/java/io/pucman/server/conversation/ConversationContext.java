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
public class ConversationContext<P extends JavaPlugin, C extends Conversable>
{
    /**
     * Conversable this context pertains to.
     */
    private final C forWhom;

    /**
     * Plugin instance this context belongs to..
     */
    private final P instance;

    /**
     * Conversation session data that is passed from conversation action to conversation action.
     */
    private Map<String, Object> data = Maps.newHashMap();
}
