package io.pucman.server.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @see PucmanCommand
 */
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
public class ArgumentField
{
    private final String name;
    private String def;
}
