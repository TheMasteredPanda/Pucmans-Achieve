package io.pucman.bungee.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import io.pucman.bungee.PLibrary;
import io.pucman.bungee.file.ConfigPopulate;
import io.pucman.bungee.locale.Format;
import io.pucman.bungee.manager.ManagingPlugin;
import io.pucman.bungee.sender.Sender;
import io.pucman.common.generic.GenericUtil;
import io.pucman.common.math.NumberUtil;
import lombok.Getter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Command wrapper. A wrapper to handle the repetitive yet necessary
 * tasks of most, if not all, commands. This will execute the command
 * body asynchronously.
 *
 * This had the added ability of the differentiation of required arguments
 * and optional arguments. Required arguments must be fulfilled whereas
 * optional arguments don't need to, but can.
 *
 * @param <T> - command sender.
 * @param <T1> - plugin this command belongs to.
 */
public abstract class PucmanCommand<T, T1 extends ManagingPlugin> extends Command
{
    @ConfigPopulate(value = "Library.Command.PlayerOnlyCommand", format = true)
    private String PLAYER_ONLY_COMMAND;

    @ConfigPopulate(value = "Library.Command.IncorrectArgumentDataType", format = true)
    private String INCORRECT_ARGUMENT_DATA_TYPE;

    @ConfigPopulate(value = "Library.Command.NotEnoughArguments", format = true)
    private String NOT_ENOUGH_ARGUMENTS;

    @ConfigPopulate(value = "Library.Command.CommandNotFound", format = true)
    private String COMMAND_NOT_FOUND;

    @ConfigPopulate(value = "Library.Command.HelpList.ListEntry", format = true)
    private String HELP_COMMAND_LIST_ENTRY;

    @ConfigPopulate(value = "Library.Command.HelpCommandNotEnabled", format = true)
    private String HELP_COMMAND_NOT_ENABLED;

    @ConfigPopulate(value = "Library.Command.HelpList.CurrentCommandHeader", color = true)
    private String HELP_LIST_CURRENT_COMMAND_HEADER;

    @ConfigPopulate(value = "Library.Command.HelpList.ChildCommandHeader", color = true)
    private String HELP_LIST_CHILD_COMMANDS_HEADER;

    @ConfigPopulate(value = "Library.Command.PlayerNotFound", format = true)
    private String PLAYER_NOT_FOUND;

    protected T1 instance;

    private CommandManager manager = PLibrary.get().get(CommandManager.class);

    @Getter
    private String description;

    @Getter
    private boolean playerOnlyCommand;

    @Getter
    private LinkedList<PucmanCommand> parentCommands = Lists.newLinkedList();

    @Getter
    private LinkedList<PucmanCommand> childCommands = Lists.newLinkedList();

    @Getter
    private LinkedList<ArgumentField> arguments = Lists.newLinkedList();

    @Getter
    private LinkedList<ArgumentField> requiredArguments = Lists.newLinkedList();

    private boolean helpCmd;

    public PucmanCommand(String mainAlias, String permission, String description, boolean playerOnlyCommand, boolean helpCmd, String... aliases)
    {
        super(mainAlias, permission, aliases);
        this.description = description;
        this.playerOnlyCommand = playerOnlyCommand;
        this.helpCmd = helpCmd;
        PLibrary.get().getMainConfig().populate(this);
    }

    /**
     * Adds the arguments to a command.
     * @param arguments - arguments.
     */
    public void addArguments(ArgumentField... arguments)
    {
        this.arguments.addAll(Arrays.asList(arguments));
        requiredArguments = Arrays.stream(arguments).filter(ArgumentField::isDef).collect(Collectors.toCollection(Lists::newLinkedList));
    }

    /**
     * TO check if a string is an alias of this command.
     * @param alias - alias.
     * @return yes if true, else false.
     */
    public boolean isAlias(String alias)
    {
        return Arrays.asList(getAliases()).contains(alias);
    }

    /**
     * Is child command.
     * @param child - child command to check.
     * @return true is yes, else false.
     */
    public boolean isChild(PucmanCommand child)
    {
        return childCommands.contains(child);
    }

    /**
     * Is parent command.
     * @param parent - parent command to check.
     * @return true if yes, else false.
     */
    public boolean isParent(PucmanCommand parent)
    {
        return parentCommands.contains(parent);
    }

    /**
     * Adds an array of commands as a child to this command.
     * @param commands
     */
    public void addChildren(PucmanCommand... commands)
    {
        for (PucmanCommand child : commands) {
            if (isChild(child)) {
                instance.getLogger().severe("Attempted to add command " + "{getcommandpath}" + " as a child when it already is a child of command " + "{getthiscommandpath}");
                continue;
            }

            if (isParent(child)) {
                instance.getLogger().severe("Attempted to add command " + "{getcommandpath}" + " as a child when it is already a parent of command " + "{getthiscommandpath}" + "!");
                continue;
            }

            childCommands.add(child);
            child.addParents(this);
        }
    }

    /**
     * Adds an array of commands as a parent to this command.
     * @param commands
     */
    public void addParents(PucmanCommand... commands)
    {
        for (PucmanCommand parent : commands) {
            if (isChild(parent)) {
                instance.getLogger().severe("Attempted to add command " + "{getcommandpath}" + " as a parent when it already is a child of command " + "{getthiscommandpath}");
                continue;
            }

            if (isParent(parent)) {
                instance.getLogger().severe("Attempted to add command " + "{getcommandpath}" + " as a parent when it is already a parent of command " + "{getthiscommandpath}" + "!");
                continue;
            }


            parentCommands.add(parent);
            parent.addChildren(this);
        }
    }

    /**
     * To check if an argument field is a required or optional field.
     * @param field - field to check.
     * @return true if required, else false.
     */
    public boolean isRequiredArgumentField(ArgumentField field)
    {
        return requiredArguments.contains(field);
    }

    /**
     * Gets the command path of this command.
     * @return command path.
     */
    public String getCommandPath()
    {
        StringBuilder sb = new StringBuilder("/");

        for (PucmanCommand parent : parentCommands) {
            sb.append(parent.getName());

            if (parent != parentCommands.getLast()) {
                sb.append(" ");
            }
        }

        return sb.toString();
    }

    /**
     * Gets the command usage of this command.
     * @return command usage.
     */
    public String getCommandUsage()
    {
        StringBuilder sb = new StringBuilder(getCommandPath()).append(" ");

        for (ArgumentField field : requiredArguments) {
            sb.append(isRequiredArgumentField(field) ? "[" : "<").append(field.getName()).append(isRequiredArgumentField(field) ? "]" : ">");

            if (field != requiredArguments.getLast()) {
                sb.append(" ");
            }
        }

        return sb.toString();
    }

    @Override
    public void execute(CommandSender sender, String[] arguments)
    {
        if (playerOnlyCommand && !(sender instanceof ProxiedPlayer)) {
            Sender.send(sender, PLAYER_ONLY_COMMAND);
            return;
        }

        if (arguments[0].equals("help")) {
            if (!helpCmd) {
                Sender.send(sender, HELP_COMMAND_NOT_ENABLED);
                return;
            }

            LinkedList<TextComponent> content = Lists.newLinkedList();

            for (PucmanCommand child : childCommands) {
                TextComponent entry = new TextComponent(child.getCommandUsage());
                entry.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(child.getDescription())));
                content.add(entry);
            }

            Multimap<Integer, TextComponent> pages = Format.paginate(content, new TextComponent(HELP_LIST_CURRENT_COMMAND_HEADER.replace("{currentcommangusage}", getCommandUsage())), new TextComponent(HELP_LIST_CHILD_COMMANDS_HEADER), 10);

            if (arguments.length == 2 && NumberUtil.parseable(arguments[1], Integer.class)) {
                int page = NumberUtil.parse(arguments[1], Integer.class);
                sender.sendMessage(pages.get(page).toArray(new TextComponent[pages.get(page).size()]));
            } else {
                sender.sendMessage(pages.get(1).toArray(new TextComponent[pages.get(1).size()]));
            }

            return;
        }

        if (childCommands.size() > 1) {
            for (PucmanCommand child : childCommands) {
                if (!child.isAlias(arguments[0])) {
                    continue;
                }

                LinkedList<String> args = Lists.newLinkedList(Arrays.asList(arguments));
                args.remove(args.getFirst());
                child.execute(sender, args.toArray(new String[args.size()]));
                return;
            }
        }

        if (arguments.length < requiredArguments.size()) {
            Sender.send(sender, NOT_ENOUGH_ARGUMENTS.replace("{commandusage}", getCommandUsage()));
            return;
        }

        LinkedList<String> args = Lists.newLinkedList(Arrays.asList(arguments));
        args.remove(args.getFirst());
        CompletableFuture.runAsync(() -> execute(GenericUtil.cast(sender),  args.toArray(new String[args.size()])), manager.getService()).exceptionally(throwable -> {
            exception(throwable);
            return null;
        });
    }

    /**
     * This method definition will contain the main body of this command.
     * @param sender - command sender.
     * @param arguments - arguments.
     */
    public abstract void execute(T sender, LinkedList<String> arguments) throws Exception;

    /**
     * Invoked when an exception occurs within the main body of this command.
     * @param t
     */
    public void exception(Throwable t)
    {

    }
}
