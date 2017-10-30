package io.pucman.server.conversation;

import com.google.common.collect.Lists;
import io.pucman.common.exception.DeveloperException;
import io.pucman.server.conversation.action.Action;
import io.pucman.server.conversation.conversable.Conversable;
import io.pucman.server.conversation.event.ConversationEndedEvent;
import io.pucman.server.conversation.event.DefaultConversationAbandonEvent;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * The Conversation Template Class. The idea behind this is the same idea that was erected when the original
 * author of the Conversations API, the one in Spigots Server Software. This is to allow total management and
 * direction of a conversation that contains both the plugin and the player, also known as the conversable,
 * to be the only active parties in the conversation.
 * @param <P>
 */
@Getter
public class Conversation<P extends JavaPlugin>
{
    /**
     * Plugin instance this conversation belongs to.
     */
    private P instance;


    public Conversable conversable;

    /**
     * Current action the player is faced with.
     */

    private Action currentAction;

    /**
     * The first action to start the conversation.
     */
    private Action firstAction;

    /**
     * The conversation context.
     */
    private ConversationContext context;

    /**
     * The conversation end event, this is invoked when the conversation ended.
     *
     * @see ConversationEndedEvent
     */
    private ConversationEndedEvent event;

    /**
     * The prefix that is prepended in every message send by the plugin.
     */
    private String prefix;

    /**
     * Cancellers, if the correct conditions are met with one canceller, it will end the conversation.
     *
     * @see ConversationCanceller
     */
    private LinkedList<ConversationCanceller> cancellers = Lists.newLinkedList();
    private boolean echoInput = false;
    private State state;

    private Conversation(P instance, Conversable conversable, Action firstAction, String prefix, boolean echoInput)
    {
        this.conversable = conversable;
        this.state = State.NOT_STARTED;
        this.instance = instance;
        this.firstAction = firstAction;
        this.currentAction = this.firstAction;
        this.prefix = prefix;
        this.echoInput = echoInput;
    }

    /**
     * Set the conversation end event.
     * @param event - end event.
     */
    public void setConversationEndEvent(@NonNull ConversationEndedEvent event)
    {
        this.event = event;
    }

    /**
     * Abandon the conversation.
     */
    public void abandon()
    {
        if (this.currentAction != null) {
            this.state = State.ABANDONED;
        }

        if (event != null) {
            this.abandon(this.event);
        } else {
            this.abandon(new DefaultConversationAbandonEvent());
        }
    }

    /**
     * Abandon the conversation.
     * @param event - end event.
     */
    public void abandon(@NonNull ConversationEndedEvent event)
    {
        if (this.state == State.STARTED || this.state == State.RUNNING) {
            if (this.currentAction != null) {
                this.state = State.ABANDONED;
            }

            event.abandon(context, state);
        }
    }

    /**
     * Add an array of cancellers.
     * @param cancellers - the cancellers.
     */
    public void addCancellers(@NonNull ConversationCanceller... cancellers)
    {
        this.cancellers.addAll(Arrays.asList(cancellers));
    }

    /**
     * Begin the conversation.
     */
    public void begin()
    {
        if (currentAction == null) {
            this.context = new ConversationContext<>(this.conversable, this.instance);
            this.currentAction = firstAction;
            this.context.getForWhom().beginConversation(this);
            this.state = State.STARTED;
        }
    }

    /**
     * Validate the input of the currenct action.
     * @param input - input.
     */
    public void validateInput(Object input)
    {
        if (this.currentAction != null) {
            this.currentAction.setAwaitingInput(false);
            if (!this.cancellers.isEmpty()) {
                for (ConversationCanceller canceller : this.cancellers) {
                    if (canceller.validate(input)) {
                        canceller.onCancel(context);
                        this.currentAction = Action.END_OF_CONVERSATION;
                        return;
                    }
                }
            }


            if (this.currentAction.validate(input)) {
                this.currentAction = this.currentAction.onValidationSuccess(input);
                return;
            } else {
                this.currentAction = this.currentAction.onValidationFail(input);
                return;
            }
        }


        throw new DeveloperException("validateInput in Conversation API got to this point when it shouldn't of.");
    }

    /**
     * Initiate the next action available, which would next the new action set in field currentAction.
     */
    public void initiateNextAction() {
         if (this.currentAction == Action.END_OF_CONVERSATION) {
            this.state = State.ENDED_GRACEFULLY;

            if (this.event != null) {
                this.event.abandon(this.context, state);
            } else {
                this.abandon(new DefaultConversationAbandonEvent());
            }
        } else {
            this.state = State.RUNNING;
            this.currentAction.init(context);
        }
    }

    /**
     * State of the conversation.
     */
    public enum State
    {
        STARTED,
        RUNNING,
        ENDED_GRACEFULLY,
        NOT_STARTED,
        ABANDONED
    }

    /**
     * Conversation build, to build a conversation instance.
     * @param <P>
     */
    public static class Builder<P extends JavaPlugin>
    {
        private Conversable conversable;
        private P instance;
        private Action firstAction;
        private String prefix;
        private boolean echoInput;

        private Builder(Conversable conversable)
        {
            this.conversable = conversable;
        }

        /**
         * Create a new builder.
         * @param conversable - the coversable the conversation will be instantiated with.
         * @return a new conversation builder instance.
         */
        public static Builder newBuilder(Conversable conversable)
        {
            return new Builder<>(conversable);
        }

        /**
         * Set plugin instance.
         * @param instance
         */
        public void setInstance(P instance)
        {
            this.instance = instance;
        }

        /**
         * Set the first action in the conversation to be initiated on startup..
         * @param firstAction - first action.
         */
        public void setFirstAction(Action firstAction)
        {
            this.firstAction = firstAction;
        }

        /**
         * Set the prefix of the conversation.
         * @param prefix - prefix
         */
        public void setPrefix(String prefix)
        {
            this.prefix = prefix;
        }

        /**
         * Set whether the plugin should echo what the player input.
         * @param echoInput - whether the player input should be echoed back.
         */
        public void setEchoInput(boolean echoInput)
        {
            this.echoInput = echoInput;
        }

        /**
         * Build the conversation instance.
         * @return the conversation instane.
         */
        public Conversation<P> build()
        {
            return new Conversation<>(this.instance, this.conversable, this.firstAction, this.prefix, this.echoInput);
        }
    }
}
