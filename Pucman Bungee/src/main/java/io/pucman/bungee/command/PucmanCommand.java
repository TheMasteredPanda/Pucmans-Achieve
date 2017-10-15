package io.pucman.bungee.command;

import com.google.common.collect.Lists;
import io.pucman.bungee.file.ConfigPopulate;
import lombok.Getter;
import lombok.NonNull;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * Wrapper for the bungee Command class. Handles most of the conditions for the benefit
 * that the developer will not have to write them for every parent and child command.
 * it also has the added benefit of handling asynchronous execution of the command
 * definition. Which is useful if the command body can be executed on another thread.
 *
 * Asynchronous states indicate what level of asynchronous execution the command body
 * will undergo. The following the states are explained as follows:
 *
 * FULL - All three methods (this#execute, this#onFailure, and this#onSuccess) are executed
 * asynchronously on the same thread.
 *
 * SEMI - The method body, which would reside in this#execute, would be executed asynchronously,
 * but either methods this#onFailure or this#onSuccess, depending on the response give inside the
 * body of the command, will not be executed asynchronously.
 *
 * NONE - All three methods will not be executed asynchronously in any capacity.
 *
 * @see AsynchronousState
 * @see this#execute(CommandSender, LinkedList)
 * @see this#onSuccess(CommandSender, LinkedList)
 * @see this#onFailure(CommandSender, LinkedList)
 *
 * ArgumentField is a class that holds the name of the argument field and it's default value.
 * Argument fields that have their default value assigned to a non-null object will be treated
 * as required argument fields, fields that are required in order for the command to run.
 *
 */
//TODO
public abstract class PucmanCommand extends Command
{
    /**
     * Parent commands essentially does what it says on the tin. These are commands that consider
     * this command to be a child command, and vise versa. A parent command cannot also be a child
     * command of the same instance, and a child command cannot also be a parent command of the instance.
     */
    @Getter
    private LinkedList<PucmanCommand> parentCommands = Lists.newLinkedList();

    /**
     * Child commands consider this instance to be their parent command. Child commands are essentially sub
     * commands. This command system can chain as many instances of this class as possible.
     */
    @Getter
    private LinkedList<PucmanCommand> childCommands = Lists.newLinkedList();

    /**
     * A list of all the argument fields for this command.
     */
    @Getter
    private LinkedList<ArgumentField> argumentFields = Lists.newLinkedList();

    /**
     * A list of all the required argument fields for this command.
     */
    @Getter
    private LinkedList<ArgumentField> requiredArgumentFields = Lists.newLinkedList();

    @Getter
    private String description;

    @Getter
    private boolean playerOnlyCommand;

    @Getter
    private AsynchronousState state;

    /**
     * The following fields are primarily locale.
     */
    @ConfigPopulate(value = "PlayerOnlyCommand", format = true)
    private String PLAYER_ONLY_COMMAND;

    @ConfigPopulate(value = "NoPermission", format = true)
    private String NO_PERMISSION;

    @ConfigPopulate(value = "NotEnoughArguments", format = true)
    private String NOT_ENOUGH_ARGUMENTS;

    @ConfigPopulate(value = "ParentCommandHeader", color = true)
    private String PARENT_COMMAND_HEADER;

    @ConfigPopulate(value = "ChildCommandHeader", color = true)
    private String CHILD_COMMAND_HEADER;

    @ConfigPopulate(value = "CommandEntry", color = true)
    private String COMMAND_ENTRY;

    /**
     * Main constructor to this wrapper, contains all the necessary information.
     * @param name - the 'main alias' of this command.
     * @param permission - the permission node of this command, if this is set to null the wrapper will consider this
     *                   command to not have a permission.
     * @param description - the description of this command.
     * @param playerOnlyCommand - whether this is a command only executable via a player instance.
     * @param asynchronousState - the asynchronous state of this command, described above.
     * @param aliases - the aliases of this command.
     */
    public PucmanCommand(@NonNull String name, String permission, String description, boolean playerOnlyCommand, @NonNull AsynchronousState asynchronousState, String... aliases)
    {
        super(name, permission, aliases);

        if (description != null) {
            this.description = description;
        }

        this.playerOnlyCommand = playerOnlyCommand;
        this.state = asynchronousState;
    }

    public PucmanCommand(String name, String description, boolean playerOnlyCommand, AsynchronousState state)
    {
        this(name, null, description, playerOnlyCommand, state);
    }

    public PucmanCommand(String name, String description, AsynchronousState state)
    {
        this(name, null, description, false, state);
    }

    public void arguments(ArgumentField... fields)
    {
        this.argumentFields.addAll(Arrays.asList(fields));
        this.requiredArgumentFields = this.argumentFields.stream().filter(field -> field.getDef() != null).collect(Collectors.toCollection(Lists::newLinkedList));
    }

    public void addParentCommands(PucmanCommand... commands)
    {
        for (PucmanCommand command : commands) {
            if (!this.isParentCommand(command) && !this.isChildCommand(command) && command != null) {
                this.parentCommands.add(command);
                command.addChildCommands(this);
            }
        }
    }

    public boolean isParentCommand(PucmanCommand command)
    {
        return this.parentCommands.contains(command);
    }

    public void addChildCommands(PucmanCommand... commands)
    {
        for (PucmanCommand command : commands) {
            if (!this.isParentCommand(command) && !this.isChildCommand(command) && command != null) {
                this.childCommands.add(command);
                command.addParentCommands(this);
            }
        }
    }

    public boolean isChildCommand(PucmanCommand command)
    {
        return this.childCommands.contains(command);
    }

    public boolean isAlias(String alias)
    {
        return Arrays.asList(this.getAliases()).contains(alias);
    }

    public boolean hasPermission(CommandSender sender)
    {
        return this.getPermission() != null || sender.hasPermission(this.getPermission());
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings)
    {

    }

    /**
     * Where the main body of the command will be defined.
     * @param sender - the sender of the command.
     * @param parameters - the parameters.
     */
    public abstract void execute(CommandSender sender, LinkedList<String> parameters);

    /**
     * If the commands body returns a failed command response, this part of the command
     * body will be invoked.
     * @param sender - the seconder of the command
     * @param parameters - the parameters.
     */
    public abstract void onFailure(CommandSender sender, LinkedList<String> parameters);

    /**
     * If the commands body returns a successful command response, this part of the command
     * body will be invoked.
     * @param sender - the sender of the command.
     * @param parameters - the parameters.
     */
    public abstract void onSuccess(CommandSender sender, LinkedList<String> parameters);
}
