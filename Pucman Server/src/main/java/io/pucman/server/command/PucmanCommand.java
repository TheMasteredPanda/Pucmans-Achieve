package io.pucman.server.command;

import com.google.common.collect.Lists;
import io.pucman.common.generic.GenericUtil;
import io.pucman.common.math.NumberUtil;
import io.pucman.server.PLibrary;
import io.pucman.server.file.ConfigPopulate;
import io.pucman.server.locale.Format;
import io.pucman.server.locale.Locale;
import io.pucman.server.sender.Sender;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;


/**
 * Wrapper for the bungee Command class. Handles most of the conditions for the benefit
 * that the developer will not have to write them for every parent and child command.
 *
 * ArgumentField is a class that holds the name of the argument field and it's default value.
 * Argument fields that have their default value assigned to a non-null object will be treated
 * as required argument fields, fields that are required in order for the command to run.
 */
@NotThreadSafe
public abstract class PucmanCommand<T extends JavaPlugin, T1> implements CommandExecutor
{
    private final PLibrary LIB  = PLibrary.get();

    /**
     * Instance of the plugin this command belongs to.
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
     * Command aliases.
     */
    @Getter
    private LinkedList<String> aliases = Lists.newLinkedList();

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
    private String PARENT_COMMAND_HEADER;

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
     * @param aliases - the aliases of this command.
     */
    public PucmanCommand(T instance, Locale locale, String name, String description, String permission, boolean playerOnlyCommand, String... aliases)
    {
        this.instance = instance;
        locale.populate(this.getClass());
        this.description = description;
        this.permission = permission;
        this.playerOnlyCommand = playerOnlyCommand;
        this.aliases.add(name);
        this.aliases.addAll(Arrays.asList(aliases));
    }

    public PucmanCommand(T instance, Locale locale, String name, String description, boolean playerOnlyCommand, String... aliases)
    {
        this(instance, locale, name, description, null, playerOnlyCommand, aliases);
    }

    public PucmanCommand(T instance, Locale locale, String name, String description)
    {
        this(instance, locale, name, description, null, false);
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
            if (!isParentCommand(parent) && !isChildCommand(parent) && parent != null) {
                parentCommands.add(parent);
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
        requiredArgumentFields = Lists.newLinkedList();
        argumentFields = Lists.newLinkedList();

        for (ArgumentField field : fields) {
            if (this.isRequiredArgumentField(field)) {
                this.requiredArgumentFields.add(field);
            }

            this.argumentFields.add(field);
        }
    }

    public boolean hasPermission(CommandSender sender)
    {
        return this.permission == null || sender.hasPermission(this.permission);
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
     * /parent command this command <argument1> [argument2] [argument3]
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


    @Override
    public final boolean onCommand(CommandSender sender, Command command, String s, String[] args)
    {
        if (isPlayerOnlyCommand() && !(sender instanceof Player)) {
            LIB.debug(this, "Is player only command.");
            Sender.send(sender, PLAYER_ONLY_COMMAND);
            return false;
        }

        if (!hasPermission(sender)) {
            LIB.debug(this, "No permission.");
            Sender.send(sender, NO_PERMISSION);
            return false;
        }

        if (args.length >= 1) {
            LIB.debug(this, "Argument length is bigger or equal to 1.");
            if (args[0].equalsIgnoreCase("help")) {
                LIB.debug(this, "First argument is 'help'");
                LinkedList<String> content = Lists.newLinkedList();

                if (PARENT_COMMAND_HEADER == null) {
                    LIB.debug(this, "PARENT_COMMAND_HEADER is null.");
                }

                content.add(PARENT_COMMAND_HEADER.replace("{commandusage}",
                        getCommandUsage()).replace("{commanddescription}",
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
                    LIB.debug(this, "No second argument found, printing first page.");
                    Sender.send(sender, pages.get(1));
                }
                return false;
            }

            LIB.debug(this, "Checking if the second argument is a child command.");
            if (childCommands.size() > 0) {
                for (PucmanCommand child : childCommands) {
                    if (!child.isAlias(args[0])) {
                        LIB.debug(this, child.getMainAlias() + " is not the second argument.");
                        continue;
                    }

                    LIB.debug(this, child.getMainAlias() + " is the second argument.");
                    LinkedList<String> newArgs = Lists.newLinkedList(Arrays.asList(args));
                    newArgs.remove(args[0]);
                    LIB.debug(this, "Invoking constructor of command " + child.getMainAlias() + ".");
                    child.execute(sender, newArgs);
                    return false;
                }
            }
        } else {
            LIB.debug(this, "There are no arguments.");
        }

        if (getRequiredArgumentFields().size() > args.length) {
            LIB.debug(this, "Required arguments not found.");
            Sender.send(sender, NOT_ENOUGH_ARGUMENTS.replace("{commandusage}", getCommandUsage()));
            return false;
        }

        LinkedList<String> newArgs = Lists.newLinkedList(Arrays.asList(args));

        LIB.debug(this, "Invoking main command body.");
        execute(GenericUtil.cast(sender), newArgs);

        return true;
    }

    /**
     * Where the main body of the command will be defined.
     * @param sender - the sender of the command.
     * @param parameters - the parameters.
     */
    public abstract void execute(T1 sender, LinkedList<String> parameters);
}