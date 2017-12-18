package io.pucman.common.sql.util;

import io.pucman.common.exception.UtilException;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Utilities for handling operations.
 */
public final class OperationUtil
{
    private OperationUtil()
    {
        throw new UtilException();
    }

    @SneakyThrows
    public static void close(Object... objects)
    {
        for (Object o : objects) {
            if (o instanceof Connection) {
                Connection c = (Connection) o;
                c.close();
            }

            if (o instanceof Statement) {
                Statement s = (Statement) o;
                s.close();
            }

            if (o instanceof ResultSet) {
                ResultSet set = (ResultSet) o;
                set.close();
            }
        }
    }
}
