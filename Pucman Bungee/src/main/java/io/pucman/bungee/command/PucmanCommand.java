package io.pucman.bungee.command;

import com.google.common.collect.Lists;
import io.pucman.bungee.PLibrary;
import io.pucman.bungee.file.ConfigPopulate;
import io.pucman.bungee.locale.Format;
import io.pucman.bungee.locale.Locale;
import io.pucman.bungee.sender.Sender;
import io.pucman.common.generic.GenericUtil;
import io.pucman.common.math.NumberUtil;
import lombok.Getter;
import lombok.NonNull;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * Wrapper for the bungee Command class. Handles most of the conditions for the benefit
 * that the developer will not have to write them for every parent and child command.
 *
 * @see this#execute(T1, LinkedList)
 *
 * ArgumentField is a class that holds the name of the argument field and it's default value.
 * Argument fields that have their default value assigned to a non-null object will be treated
 * as required argument fields, fields that are required in order for the command to run.
 *
 */
//TODO asynchronously.
public abstract class PucmanCommand<T extends Plugin, T1> extends Command
{
    /**
     * Instance of the main library class.
     */
    private final PLibrary LIB = PLibrary.get();

    /**
     * Instance of the plugin passed in the constructors.
     */
    protected T instance;

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
     * The following fields are primarily locale.
     */
    @ConfigPopulate(value = "Plugin.Command.PlayerOnlyCommand", format = true)
    private String PLAYER_ONLY_COMMAND;

    @ConfigPopulate(value = "Plugin.Command.NoPermission", format = true)
    private String NO_PERMISSION;

    @ConfigPopulate(value = "Plugin.Command.NotEnoughArguments", format = true)
    private String NOT_ENOUGH_ARGUMENTS;

    @ConfigPopulate(value = "Plugin.Command.IncorrectArgumentInput", format = true)
    public String INCORRECT_ARGUMENT_INPUT;

    @ConfigPopulate(value = "Plugin.Command.ParentCommandHeader", color = true)
    private String PARENT_COMMAND_HEADER;

    @ConfigPopulate(value = "Plugin.Command.ChildCommandHeader", color = true)
    private String CHILD_COMMAND_HEADER;

    @ConfigPopulate(value = "Plugin.Command.CommandEntry", color = true)
    private String COMMAND_ENTRY;


    /**
     * Instance of the command manager.
     *
     * @see CommandManager
     */
    private CommandManager manager = PLibrary.get().get(CommandManager.class);


    /**
     * Main constructor to this wrapper, contains all the necessary information.
     * @param instance - instance of the plugin the command belongs to.
     * @param name - the 'main alias' of this command.
     * @param permission - the permission node of this command, if this is set to null the wrapper will consider this
     *                   command to not have a permission.
     * @param description - the description of this command.
     * @param playerOnlyCommand - whether this is a command only executable via a player instance.
     * @param aliases - the aliases of this command.
     */
    public PucmanCommand(@NonNull T instance, @NonNull Locale locale, @NonNull String name, String permission, String description, boolean playerOnlyCommand, String... aliases)
    {
        super(name, permission, aliases);

        instance = instance;
        locale.populate(this);

        if (description != null) {
            description = description;
        }

        playerOnlyCommand = playerOnlyCommand;
    }

    public PucmanCommand(T instance, Locale locale, String name, String description, boolean playerOnlyCommand)
    {
        this(instance, locale, name, null, description, playerOnlyCommand);
    }

    public PucmanCommand(T instance, Locale locale, String name, String description)
    {
        this(instance, locale, name, null, description, false);
    }

    /**
     * To add argument fields to this command.
     * @param fields - the argument fields.
     */
    public void arguments(ArgumentField... fields)
    {
        argumentFields.addAll(Arrays.asList(fields));
        requiredArgumentFields = argumentFields.stream().filter(field -> field.getDef() != null).collect(Collectors.toCollection(Lists::newLinkedList));
    }

    /**
     * For adding parent commands.
     * @param commands - commands.
     */
    public void addParentCommands(PucmanCommand... commands)
    {
        for (PucmanCommand command : commands) {
            if (!isParentCommand(command) && !isChildCommand(command) && command != null) {
                parentCommands.add(command);
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
        return parentCommands.contains(command);
    }

    /**
     * For adding child commands.
     * @param commands - commands
     */
    public void addChildCommands(PucmanCommand... commands)
    {
        for (PucmanCommand command : commands) {
            if (!isParentCommand(command) && !isChildCommand(command) && command != null) {
                childCommands.add(command);
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
        return childCommands.contains(command);
    }

    /**
     * Check if the command is an alias of this command.
     * @param alias - alias to check.
     * @return true if it is, else false.
     */
    public boolean isAlias(String alias)
    {
        return Arrays.asList(getAliases()).contains(alias) || alias.equals(getName());
    }

    /**
     * To check if the player has the permission to execute this command.
     * @param sender - the sender.
     * @return true if they that have permission, else false.
     */
    public boolean hasPermission(CommandSender sender)
    {
        return getPermission() != null || sender.hasPermission(getPermission());
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
        return requiredArgumentFields.contains(field);
    }

    /**
     * To generate the command path of this command. This generates the full path of the command.
     * For example:
     * /parentcommand thiscommand
     * @return the command path.
     */
    public String getCommandPath()
    {
        LIB.debug(this, "Invoked getCommandPath()");
        StringBuilder sb = new StringBuilder("/");

        for (PucmanCommand parent : parentCommands) {
            sb.append(parent.getName());

            if (parent != parentCommands.getLast()) {
                sb.append(" ");
            }
        }

        LIB.debug(this, "Returning unary value getCommandPath().");
        return sb.append(getName()).toString();
    }

    /**
     * Generates the command usage. This takes the command path and adds all argument fields to the
     * command. For example:
     * /parentcommand thiscommand <argument1> [argument2] [argument3]
     * @return the command usage;
     */
    public String getCommandUsage()
    {
        StringBuilder sb = new StringBuilder(getCommandPath()).append(" ");

        for (ArgumentField field : argumentFields) {
            sb.append(isRequiredArgumentField(field) ? "<" : "[").append(field.getName()).append(isRequiredArgumentField(field) ? ">" : "]");

            if (field != argumentFields.getLast()) {
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

        if (isPlayerOnlyCommand() && !(sender instanceof ProxiedPlayer)) {
            LIB.debug(this, "Is player only command.");
            Sender.send(sender, PLAYER_ONLY_COMMAND);
            return;
        }

        if (!hasPermission(sender)) {
            LIB.debug(this, "No permission.");
            Sender.send(sender, NO_PERMISSION);
            return;
        }

        if (args.length >= 1) {
            LIB.debug(this, "Argument length is bigger or equal than 1.");
            if (args[0].equalsIgnoreCase("help")) {
                LIB.debug(this, "First argument is 'help'");
                LinkedList<String> content = Lists.newLinkedList();
                
                if (PARENT_COMMAND_HEADER == null) {
                    LIB.debug(this, "PARENT_COMMAND_HEADER is null.");
                }
                
                content.add(PARENT_COMMAND_HEADER.replace("{commandusage}",
                        getCommandUsage()).replace("{commanddescrption}",
                        getDescription()));

                if (childCommands.size() > 0) {
                    LIB.debug(this, "Invoked command has child commands.");
                    content.add(CHILD_COMMAND_HEADER);

                    for (PucmanCommand child : childCommands) {
                        content.add(COMMAND_ENTRY.replace("{commandusage}", child.getCommandPath()).replace("{commanddescription}", child.getDescription()));
                    }
                }

                LIB.debug(this, "Paginating.");
                LinkedHashMap<Integer, String> pages = Format.paginate(content, null, null, 5);

                if (args.length == 2 && NumberUtil.parseable(args[1], Integer.class)) {
                    LIB.debug(this, "2 argument was found and parsable as an integer.");
                    Sender.send(sender, pages.get(NumberUtil.parse(args[1], Integer.class)));
                } else {
                    LIB.debug("No second argument found, printing first page.");
                    Sender.send(sender, pages.get(1));
                }
                return;
            }

            LIB.debug(this, "Checking if the second argument is a child command.");
            if (childCommands.size() > 0) {
                for (PucmanCommand child : childCommands) {
                    if (!child.isAlias(args[0])) {
                        LIB.debug(this, child.getName() + " is not the second argument.");
                        continue;
                    }

                    LIB.debug(this, child.getName() + " is the second argument.");
                    LinkedList<String> newArgs = Lists.newLinkedList(Arrays.asList(args));
                    newArgs.remove(args[0]);
                    LIB.debug(this, "Invoking constructor of command " + child.getName() + ".");
                    child.execute(sender, newArgs.toArray(new String[newArgs.size()]));
                    return;
                }
            }
        } else {
            LIB.debug(this, "There are no arguments.");
        }

        if (args.length < getRequiredArgumentFields().size()) {
            LIB.debug("Required arguments not found.");
            Sender.send(sender, NOT_ENOUGH_ARGUMENTS.replace("{commandusage}", getCommandUsage()));
            return;
        }

        LinkedList<String> newArgs = Lists.newLinkedList(Arrays.asList(args));

        LIB.debug(this, "Invoking main command body.");
        execute(GenericUtil.cast(sender), newArgs);
    }

    /**
     * Main body of the command.
     * @param sender - the sender of the command.
     * @param parameters - the parameters.
     */
    public abstract void execute(T1 sender, LinkedList<String> parameters);
}