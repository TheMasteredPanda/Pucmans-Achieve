package io.pucman.server.command;

import com.google.common.collect.Lists;
import io.pucman.server.file.ConfigPopulate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.LinkedList;

//TODO documentation.
public class PucmanCommand extends Command
{
    private LinkedList<PucmanCommand> parentCommands = Lists.newLinkedList();
    private LinkedList<PucmanCommand> childCommands = Lists.newLinkedList();
    private LinkedList<ArgumentField> argumentFields = Lists.newLinkedList();
    private LinkedList<ArgumentField> requireArgumentFields = Lists.newLinkedList();
    private boolean playerOnlyCommand = false;

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

    public PucmanCommand()
    {
        super(null, null, null, null);


    }



    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings)
    {
        return false;
    }
}
