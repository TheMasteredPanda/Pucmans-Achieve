package io.pucman.common.exception;

/**
 * Used for members of classes when any given member of that class
 * shouldn't reach this exception.
 */
public class DeveloperException extends RuntimeException
{
    public DeveloperException(String message)
    {
        super(message);
    }

    public DeveloperException(Throwable t)
    {
        super(t);
    }
}
