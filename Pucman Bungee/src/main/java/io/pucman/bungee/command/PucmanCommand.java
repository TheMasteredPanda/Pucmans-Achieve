package io.pucman.bungee.command;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListenableFuture;
import io.pucman.bungee.PLibrary;
import io.pucman.bungee.file.ConfigPopulate;
import io.pucman.bungee.locale.Format;
import io.pucman.bungee.locale.Locale;
import io.pucman.bungee.sender.Sender;
import io.pucman.common.exception.TrySupplier;
import io.pucman.common.exception.TryUtil;
import io.pucman.common.test.math.NumberUtil;
import lombok.Getter;
import lombok.NonNull;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
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
 * @see this#onSuccess(CommandSender, Map, LinkedList)
 * @see this#onFailure(CommandSender, Map, LinkedList)
 *
 * ArgumentField is a class that holds the name of the argument field and it's default value.
 * Argument fields that have their default value assigned to a non-null object will be treated
 * as required argument fields, fields that are required in order for the command to run.
 *
 */
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

    /**
     * Command description.
     */
    @Getter
    private String description;

    /**
     * If true, it is a player only command, otherwise both the console and player can execute it.
     */
    @Getter
    private boolean playerOnlyCommand;

    /**
     * Asynchronous state, cannot be null.
     */
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

    @ConfigPopulate(value = "IncorrectArgumentInput", format = true)
    protected String INCORRECT_ARGUMENT_INPUT;

    @ConfigPopulate(value = "ParentCommandHeader", color = true)
    private String PARENT_COMMAND_HEADER;

    @ConfigPopulate(value = "ChildCommandHeader", color = true)
    private String CHILD_COMMAND_HEADER;

    @ConfigPopulate(value = "CommandEntry", color = true)
    private String COMMAND_ENTRY;

    /**
     * Instance of the command manager.
     *
     * @see CommandManager
     */
    private CommandManager manager = PLibrary.get().get(CommandManager.class);


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
    public PucmanCommand(@NonNull Locale locale, @NonNull String name, String permission, String description, boolean playerOnlyCommand, @NonNull AsynchronousState asynchronousState, String... aliases)
    {
        super(name, permission, aliases);
        locale.populate(this.getClass());

        if (description != null) {
            this.description = description;
        }

        this.playerOnlyCommand = playerOnlyCommand;
        this.state = asynchronousState;
    }

    public PucmanCommand(Locale locale, String name, String description, boolean playerOnlyCommand, AsynchronousState state)
    {
        this(locale, name, null, description, playerOnlyCommand, state);
    }

    public PucmanCommand(Locale locale, String name, String description, AsynchronousState state)
    {
        this(locale, name, null, description, false, state);
    }

    /**
     * To add argument fields to this command.
     * @param fields - the argument fields.
     */
    public void arguments(ArgumentField... fields)
    {
        this.argumentFields.addAll(Arrays.asList(fields));
        this.requiredArgumentFields = this.argumentFields.stream().filter(field -> field.getDef() != null).collect(Collectors.toCollection(Lists::newLinkedList));
    }

    /**
     * For adding parent commands.
     * @param commands - commands.
     */
    public void addParentCommands(PucmanCommand... commands)
    {
        for (PucmanCommand command : commands) {
            if (!this.isParentCommand(command) && !this.isChildCommand(command) && command != null) {
                this.parentCommands.add(command);
                command.addChildCommands(this);
            }
        }
    }

    /**
     * Checking if a command is a parent command.
     * @param command - command to check.
     * @return if if is a parent command of this class it will return true, else false.
     */
    public boolean isParentCommand(PucmanCommand command)
    {
        return this.parentCommands.contains(command);
    }

    /**
     * For adding child commands.
     * @param commands - commands
     */
    public void addChildCommands(PucmanCommand... commands)
    {
        for (PucmanCommand command : commands) {
            if (!this.isParentCommand(command) && !this.isChildCommand(command) && command != null) {
                this.childCommands.add(command);
                command.addParentCommands(this);
            }
        }
    }

    /**
     * Checking if a command is a child command.
     * @param command - command to check.
     * @return if it is a child command of this class it will return true, else false.
     */
    public boolean isChildCommand(PucmanCommand command)
    {
        return this.childCommands.contains(command);
    }

    /**
     * Check if the command is an alias of this command.
     * @param alias - alias to check.
     * @return true if it is, else false.
     */
    public boolean isAlias(String alias)
    {
        return Arrays.asList(this.getAliases()).contains(alias) || alias.equals(this.getName());
    }

    /**
     * To check if the player has the permission to execute this command.
     * @param sender - the sender.
     * @return true if they that have permission, else false.
     */
    public boolean hasPermission(CommandSender sender)
    {
        return this.getPermission() != null || sender.hasPermission(this.getPermission());
    }

    /**
     * For checking if an argument field is required.
     *
     * @see ArgumentField
     * @param field - field to check.
     * @return true if required, else false.
     */
    public boolean isRequiredArgumentField(ArgumentField field)
    {
        return this.requiredArgumentFields.contains(field);
    }

    /**
     * To generate the command path of this command. This generates the full path of the command.
     * For example:
     * /parentcommand thiscommand
     * @return the command path.
     */
    public String getCommandPath()
    {
        StringBuilder sb = new StringBuilder("/");

        for (PucmanCommand parent : this.parentCommands) {
            sb.append(parent.getName());

            if (parent != this.parentCommands.getLast()) {
                sb.append(" ");
            }
        }

        return sb.toString();
    }

    /**
     * Generates the command usage. This takes the command path and adds all argument fields to the
     * command. For example:
     * /parentcommand thiscommand <argument1> [argument2] [argument3]
     * @return the command usage;
     */
    public String getCommandUsage()
    {
        StringBuilder sb = new StringBuilder(this.getCommandPath()).append(" ");

        for (ArgumentField field : this.argumentFields) {
            sb.append(this.isRequiredArgumentField(field) ? "<" : "[").append(field.getName()).append(this.isRequiredArgumentField(field) ? ">" : "]");

            if (field != this.argumentFields.getLast()) {
                sb.append(" ");
            }
        }

        return sb.toString();
    }

    /**
     * Body of BungeeCord command.
     * @param sender - sender.
     * @param args - arguments.
     */
    @Override
    public void execute(CommandSender sender, String[] args)
    {
        if (!this.isAlias(args[0])) {
            return;
        }

        if (this.isPlayerOnlyCommand() && !(sender instanceof ProxiedPlayer)) {
            Sender.sender(sender, this.PLAYER_ONLY_COMMAND);
            return;
        }

        if (!this.hasPermission(sender)) {
            Sender.sender(sender, this.NO_PERMISSION);
            return;
        }

        if (args.length > 1) {
            if (args[0].equalsIgnoreCase("help")) {
                LinkedList<String> content = Lists.newLinkedList();
                content.add(this.PARENT_COMMAND_HEADER.replace("{commandusage}", this.getCommandUsage()).replace("{commanddescrption}", this.getDescription()));

                if (this.childCommands.size() > 0) {
                    content.add(this.CHILD_COMMAND_HEADER);

                    for (PucmanCommand child : this.childCommands) {
                        content.add(this.COMMAND_ENTRY.replace("{commandusage}", child.getCommandPath()).replace("{commanddescription}", child.getDescription()));
                    }
                }

                LinkedListMultimap<Integer, String> pages = Format.paginate(String.class, content, null, null, 5);

                if (args.length == 2 && NumberUtil.parseable(args[1], Integer.class)) {
                    Sender.sender(sender, pages.get(NumberUtil.parse(args[1], Integer.class)));
                } else {
                    Sender.sender(sender, pages.get(1));
                }

                return;
            }

            for (PucmanCommand child : this.parentCommands) {
                if (!child.isAlias(args[0])) {
                    continue;
                }

                LinkedList<String> newArgs = Lists.newLinkedList(Arrays.asList(args));
                newArgs.remove(args[0]);
                child.execute(sender, newArgs.toArray(new String[newArgs.size()]));
                return;
            }

            if (args.length - 1 < this.getRequiredArgumentFields().size()) {
                Sender.sender(sender, this.NOT_ENOUGH_ARGUMENTS.replace("{commandusage}", this.getCommandUsage()));
                return;
            }

            LinkedList<String> newArgs = Lists.newLinkedList();
            newArgs.remove(args[0]);

            switch (this.state) {
                case FULL: {
                    ListenableFuture<CommandResponse> fullFuture = this.manager.service.submit(() -> this.execute(sender, newArgs));
                    CommandResponse fullResponse = TryUtil.sneaky((TrySupplier<CommandResponse>) fullFuture::get);
                    fullFuture.addListener(() -> {
                        if (fullResponse.getType() == CommandResponse.Type.SUCCESS) {
                            this.onSuccess(sender, fullResponse.getData(), newArgs);
                        } else {
                            this.onFailure(sender, fullResponse.getData(), newArgs);
                        }
                    }, this.manager.service);
                    return;
                }
                case SEMI: {
                    ListenableFuture<CommandResponse> semiFuture = this.manager.service.submit(() -> this.execute(sender, newArgs));
                    CommandResponse semiResponse = TryUtil.sneaky((TrySupplier<CommandResponse>) semiFuture::get);
                    if (semiResponse.getType() == CommandResponse.Type.SUCCESS) {
                        this.onSuccess(sender, semiResponse.getData(), newArgs);
                    } else {
                        this.onFailure(sender, semiResponse.getData(), newArgs);
                    }
                    return;
                }
                case NONE: {
                    CommandResponse noneResponse = this.execute(sender, newArgs);

                    if (noneResponse.getType() == CommandResponse.Type.SUCCESS) {
                        this.onSuccess(sender, noneResponse.getData(), newArgs);
                    } else {
                        this.onFailure(sender, noneResponse.getData(), newArgs);
                    }
                }
            }
        }
    }

    /**
     * Where the main body of the command will be defined.
     * @param sender - the sender of the command.
     * @param parameters - the parameters.
     */
    public abstract CommandResponse execute(CommandSender sender, LinkedList<String> parameters);

    /**
     * If the commands body returns a failed command response, this part of the command
     * body will be invoked.
     * @param sender - the seconder of the command
     * @param parameters - the parameters.
     */
    public abstract void onFailure(CommandSender sender, Map<String, Object> data, LinkedList<String> parameters);

    /**
     * If the commands body returns a successful command response, this part of the command
     * body will be invoked.
     * @param sender - the sender of the command.
     * @param parameters - the parameters.
     */
    public abstract void onSuccess(CommandSender sender, Map<String, Object> data, LinkedList<String> parameters);
}