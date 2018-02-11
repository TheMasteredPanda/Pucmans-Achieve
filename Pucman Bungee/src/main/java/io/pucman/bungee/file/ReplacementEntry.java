package io.pucman.bungee.file;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Class template for a replacement entry.
 *
 * This is for the format functionality of the file system.
 * When you opt for a message or value to be formatted, it will
 * iterate through the list of replacement entries.
 *
 * It will then replace the placeholder, if found, with the value
 * specified.
 *
 * @see BaseFile
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReplacementEntry
{
    private String placeholder;
    private Object value;

    public static ReplacementEntry create(String placeholder, Object value)
    {
        return new ReplacementEntry(placeholder, value);
    }
}
