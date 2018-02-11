package io.pucman.bungee.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@AllArgsConstructor
@RequiredArgsConstructor
@Getter
/**
 * Template class. Used to specify one argument for a pucman command.
 */
public class ArgumentField
{
    private final String name;
    private boolean def;
}
