package io.pucman.server.command;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListenableFuture;
import io.pucman.common.exception.TrySupplier;
import io.pucman.common.exception.TryUtil;
import io.pucman.common.math.NumberUtil;
import io.pucman.server.PLibrary;
import io.pucman.server.file.ConfigPopulate;
import io.pucman.server.locale.Format;
import io.pucman.server.locale.Locale;
import io.pucman.server.sender.Sender;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;

/**
 * Wrapper for the bungee Command class. Handles most of the conditions for the benefit
 * that the developer will not have to write them for every parent and child command.
 * it also has the added benefit of handling asynchronous execution of the command
 * definition. Which is useful if the command body can be executed on another thread.
 *
 * Asynchronous states indicate what level of asynchronous execution the command body
 * will undergo. The following the states are explained as follows:
 *
 * 2 - All three methods (this#execute, this#onFailure, and this#onSuccess) are executed
 * asynchronously on the same thread.
 *
 * 1 - The method body, which would reside in this#execute, would be executed asynchronously,
 * but either methods this#onFailure or this#onSuccess, depending on the response give inside the
 * body of the command, will not be executed asynchronously.
 *
 * 0 - All three methods will not be executed asynchronously in any capacity.
 *
 * @see this#execute(CommandSender, LinkedList)
 * @see this#onSuccess(CommandSender, Map, LinkedList)
 * @see this#onFailure(CommandSender, Map, LinkedList)
 *
 * ArgumentField is a class that holds the name of the argument field and it's default value.
 * Argument fields that have their default value assigned to a non-null object will be treated
 * as required argument fields, fields that are required in order for the command to run.
 *
 */
public abstract class PucmanCommand<P extends JavaPlugin>
{
    /**
     * Instance of the plugin this command belongs to.
     */
    protected P instance;

    /**
     * The command this wrapper is wrapping.
     */
    protected Command command;

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
     * Command aliases.
     */
    @Getter
    private LinkedList<String> aliases = Lists.newLinkedList();

    /**
     * Instance of the command manager.
     *
     * @see CommandManager
     */
    private CommandManager manager = PLibrary.get().get(CommandManager.class);

    /**
     * Command description.
     */
    @Getter
    private String description;

    /**
     * Command permission.
     */
    @Getter
    private String permission;

    /**
     * If true, it is a player only command, otherwise both the console and player can execute it.
     */
    @Getter
    private boolean playerOnlyCommand = false;

    /**
     * Asynchronous state, cannot be null.
     */
    private int state;

    /**
     * The following fields are primarily locale.
     */
    @ConfigPopulate("NoPermission")
    private String NO_PERMISSION;

    @ConfigPopulate("PlayerOnlyCommand")
    private String PLAYER_ONLY_COMMAND;

    @ConfigPopulate("NotEnoughArguments")
    private String NOT_ENOUGH_ARGUMENTS;

    @ConfigPopulate("IncorrectArgumentInput")
    private String INCORRECT_ARGUMENT_INPUT;

    @ConfigPopulate("ParentCommandHeader")
    public String PARENT_COMMAND_HEADER;

    @ConfigPopulate("ChildCommandHeader")
    private String CHILD_COMMAND_HEADER;

    @ConfigPopulate("CommandEntry")
    private String COMMAND_ENTRY;

    /**
     * Main constructor to this wrapper, contains all the necessary information.
     * @param instance - instance of the plugin the command belongs to.
     * @param name - the 'main alias' of this command.
     * @param permission - the permission node of this command, if this is set to null the wrapper will consider this
     *                   command to not have a permission.
     * @param description - the description of this command.
     * @param playerOnlyCommand - whether this is a command only executable via a player instance.
     * @param state - the asynchronous state of this command, described above.
     * @param aliases - the aliases of this command.
     */
    public PucmanCommand(P instance, Locale locale, String name, String description, String permission, boolean playerOnlyCommand, int state, String... aliases)
    {
        this.instance = instance;
        locale.populate(this.getClass());
        this.description = description;
        this.permission = permission;
        this.playerOnlyCommand = playerOnlyCommand;
        this.state = state;
        this.aliases.add(name);
        this.aliases.addAll(Arrays.asList(aliases));

        /**
         * Command boxy.
         */
        this.command = new Command(name, description, this.getCommandPath(), Arrays.asList(aliases))
        {
            @Override
            public boolean execute(CommandSender sender, String s, String[] args)
            {

                if (isPlayerOnlyCommand() && !(sender instanceof Player)) {
                    Sender.send(sender, PLAYER_ONLY_COMMAND);
                    return false;
                }

                if (!this.testPermission(sender)) {
                    Sender.send(sender, NO_PERMISSION);
                    return false;
                }

                if (args.length > 1) {
                    if (args[0].equalsIgnoreCase("help")) {
                        LinkedList<String> content = Lists.newLinkedList();
                        content.add(PARENT_COMMAND_HEADER.replace("{commandusage}", getCommandUsage()).replace("{commanddescrption}", this.getDescription()));

                        if (childCommands.size() > 0) {
                            content.add(CHILD_COMMAND_HEADER);

                            for (PucmanCommand child : childCommands) {
                                content.add(COMMAND_ENTRY.replace("{commandusage}", child.getCommandPath()).replace("{commanddescription}", child.getDescription()));
                            }
                        }

                        LinkedListMultimap<Integer, String> pages = Format.paginate(String.class, content, null, null, 5);

                        if (args.length == 2 && NumberUtil.parseable(args[1], Integer.class)) {
                            Sender.send(sender, pages.get(NumberUtil.parse(args[1], Integer.class)));
                        } else {
                            Sender.send(sender, pages.get(1));
                        }

                        return true;
                    }

                    for (PucmanCommand child : parentCommands) {
                        if (!child.isAlias(args[0])) {
                            continue;
                        }

                        LinkedList<String> newArgs = Lists.newLinkedList(Arrays.asList(args));
                        newArgs.remove(args[0]);
                        child.execute(sender, newArgs);
                        return true;
                    }

                    if (args.length - 1 < getRequiredArgumentFields().size()) {
                        Sender.send(sender, NOT_ENOUGH_ARGUMENTS.replace("{commandusage}", getCommandUsage()));
                        return false;
                    }

                    LinkedList<String> newArgs = Lists.newLinkedList();
                    newArgs.remove(args[0]);

                    switch (state) {
                        case 2: {
                            ListenableFuture<CommandResponse> fullFuture = manager.service.submit(() -> PucmanCommand.this.execute(sender, newArgs));
                            CommandResponse fullResponse = TryUtil.sneaky((TrySupplier<CommandResponse>) fullFuture::get);
                            fullFuture.addListener(() -> {
                                if (fullResponse.getType() == CommandResponse.Type.SUCCESS) {
                                    onSuccess(sender, fullResponse.getData(), newArgs);
                                } else {
                                    onFailure(sender, fullResponse.getData(), newArgs);
                                }
                            }, manager.service);
                            return true;
                        }
                        case 1: {
                            ListenableFuture<CommandResponse> semiFuture = manager.service.submit(() -> PucmanCommand.this.execute(sender, newArgs));
                            CommandResponse semiResponse = TryUtil.sneaky((TrySupplier<CommandResponse>) semiFuture::get);
                            if (semiResponse.getType() == CommandResponse.Type.SUCCESS) {
                                onSuccess(sender, semiResponse.getData(), newArgs);
                            } else {
                                onFailure(sender, semiResponse.getData(), newArgs);
                            }
                            return true;
                        }
                        case 0: {
                            CommandResponse noneResponse = PucmanCommand.this.execute(sender, newArgs);

                            if (noneResponse.getType() == CommandResponse.Type.SUCCESS) {
                                onSuccess(sender, noneResponse.getData(), newArgs);
                            } else {
                                onFailure(sender, noneResponse.getData(), newArgs);
                            }
                        }
                    }
                }

                return true;
            }
        };
    }

    public PucmanCommand(P instance, Locale locale, String name, String description, boolean playerOnlyCommand, int state, String... aliases)
    {
        this(instance, locale, name, description, null, playerOnlyCommand, state, aliases);
    }

    public PucmanCommand(P instance, Locale locale, String name, String description, int state)
    {
        this(instance, locale, name, description, null, false, state);
    }

    public String getMainAlias()
    {
        return this.aliases.getFirst();
    }

    public boolean isAlias(String alias)
    {
        return this.aliases.contains(alias);
    }

    /**
     * To add child commands to this command.
     * @param commands - commands to add.
     */
    public void addChildCommand(PucmanCommand... commands)
    {
        for (PucmanCommand child : commands) {
            if (!this.isChildCommand(child) && !this.isParentCommand(child) && child != this)  {
                this.childCommands.add(child);
                child.addParentCommand(this);
            }
        }
    }

    /**
     * To check if a command is a child command of this command.
     * @param command - command to check.
     * @return true if yes, else false.
     */
    public boolean isChildCommand(PucmanCommand command)
    {
        return this.childCommands.contains(command);
    }

    /**
     * To check if a command is a parent command of this command.
     * @param command - command to check.
     * @return true if yes, else false.
     */
    public boolean isParentCommand(PucmanCommand command)
    {
        return this.parentCommands.contains(command);
    }

    /**
     * For adding parent commands to this command..
     * @param commands - commands to add.
     */
    public void addParentCommand(PucmanCommand... commands)
    {
        for (PucmanCommand parent : commands) {
            if (!this.isParentCommand(parent) && !this.isChildCommand(parent) && parent != null) {
                this.parentCommands.add(parent);
                parent.addChildCommand(this);
            }
        }
    }

    /**
     * To add argument fields to this command.
     * @param fields - the argument fields.
     */
    public void arguments(ArgumentField... fields)
    {
        this.requiredArgumentFields = Lists.newLinkedList();
        this.argumentFields = Lists.newLinkedList();

        for (ArgumentField field : fields) {
            if (this.isRequiredArgumentField(field)) {
                this.requiredArgumentFields.add(field);
            }

            this.argumentFields.add(field);
        }
    }

    /**
     * To check if a field is required.
     * @param field - field to check.
     * @return true if the field it required, else false.
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
            sb.append(parent.getMainAlias());

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
        StringBuilder sb = new StringBuilder(this.getCommandPath());

        for (ArgumentField field : this.argumentFields) {
            sb.append(this.isRequiredArgumentField(field) ? "<" : "[").append(field.getName()).append(this.isRequiredArgumentField(field) ? ">" : "]");

            if (field != this.argumentFields.getLast()) {
                sb.append(" ");
            }
        }

        sb.append(" ").append(this.getDescription());

        return sb.toString();
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
    public void onSuccess(CommandSender sender, Map<String, Object> data, LinkedList<String> parameters) {
    }

    /**
     * If the commands body returns a successful command response, this part of the command
     * body will be invoked.
     * @param sender - the sender of the command.
     * @param parameters - the parameters.
     */
    public void onFailure(CommandSender sender, Map<String, Object> data, LinkedList<String> parameters) {
    }
}