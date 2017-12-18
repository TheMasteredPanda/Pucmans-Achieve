package io.pucman.common.sql.util;

import io.pucman.common.exception.UtilException;

public final class ErrorConsumer
{
    private ErrorConsumer()
    {
        throw new UtilException();
    }

    public static void consume(int errorId)
    {
        //TODO
    }
}
