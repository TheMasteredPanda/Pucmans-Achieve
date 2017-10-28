package io.pucman.common.exception;

import com.google.common.base.Supplier;
import lombok.SneakyThrows;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Utility used for methods that need to be wrapped in a try/catch statement.
 * This is meant to provide a cleaner way of doing so.
 */
@ParametersAreNonnullByDefault
public final class TryUtil
{
    private TryUtil()
    {
        throw new UtilException();
    }

    @SneakyThrows
    public static <T> T sneaky(TrySupplier<T> supplier, Class<T> type)
    {
        return supplier.get();
    }

    @SneakyThrows
    public static void sneaky(TryRunnable task)
    {
        task.run();
    }
}
