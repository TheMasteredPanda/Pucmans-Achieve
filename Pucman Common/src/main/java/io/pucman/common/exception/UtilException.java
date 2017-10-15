package io.pucman.common.exception;

/**
 * Used in constructors of utility classes to stop new instances of utility classes being made.
 */
public class UtilException extends RuntimeException
{
    public UtilException()
    {
        super("Cannot instantiate a utility class.");
    }

    public UtilException(String message)
    {
        super(message);
    }
}
