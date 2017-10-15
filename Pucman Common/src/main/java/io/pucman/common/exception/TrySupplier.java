package io.pucman.common.exception;

/**
 * @see TryUtil
 * @param <T> - genetic type.
 */
@FunctionalInterface
public interface TrySupplier<T>
{
    T get() throws Exception;
}
