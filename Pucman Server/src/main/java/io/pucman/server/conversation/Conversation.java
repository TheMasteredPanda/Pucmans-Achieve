package io.pucman.server.conversation;

import com.google.common.collect.Lists;
import io.pucman.common.exception.DeveloperException;
import io.pucman.server.conversation.action.Action;
import io.pucman.server.conversation.conversable.Conversable;
import io.pucman.server.conversation.event.ConversationEndedEvent;
import io.pucman.server.conversation.event.DefaultConversationAbandonEvent;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.LinkedList;

@Getter
public class Conversation<P extends JavaPlugin>
{
    private P instance;
    private Action currentAction;
    private Action firstAction;
    private ConversationContext context;
    private ConversationEndedEvent event;
    private String prefix;
    private LinkedList<ConversationCanceller> cancellers = Lists.newLinkedList();
    private boolean echoInput = false;
    private State state;

    public Conversation(P instance, Action firstAction, String prefix, boolean echoInput)
    {
        this.state = State.NOT_STARTED;
        this.instance = instance;
        this.firstAction = firstAction;
        this.prefix = prefix;
        this.echoInput = echoInput;
    }

    public void setConversationEndEvent(ConversationEndedEvent event)
    {
        this.event = event;
    }

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

    public void abandon(ConversationEndedEvent event)
    {
        if (this.state == State.STARTED || this.state == State.RUNNING) {
            if (this.currentAction != null) {
                this.state = State.ABANDONED;
            }

            event.abandon(context, state);
        }
    }

    public void addCancellers(ConversationCanceller... cancellers)
    {
        this.cancellers.addAll(Arrays.asList(cancellers));
    }

    public void begin(Conversable conversable)
    {
        if (currentAction == null) {
            this.context = new ConversationContext<>(conversable, this.instance);
            this.currentAction = firstAction;
            this.context.getForWhom().beginConversation(this);
            this.state = State.STARTED;
        }
    }

    public boolean validateInput(Object input)
    {
        if (this.currentAction != null) {
            this.currentAction.setAwaitingInput(false);
            if (!this.cancellers.isEmpty()) {
                for (ConversationCanceller canceller : this.cancellers) {
                    if (canceller.validate(input)) {
                        this.currentAction = canceller.onCancel(context);
                        return false;
                    }
                }
            }


            if (this.currentAction.validate(input)) {
                this.currentAction = this.currentAction.onValidationSuccess(input);
                return true;
            } else {
                this.currentAction = this.currentAction.onValidationFail(input);
                return false;
            }
        }


        throw new DeveloperException("validateInput in Conversation API got to this point when it shouldn't of.");
    }

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

    public enum State
    {
        STARTED,
        RUNNING,
        ENDED_GRACEFULLY,
        NOT_STARTED,
        ABANDONED
    }
}
