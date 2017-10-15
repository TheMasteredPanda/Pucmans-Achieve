package io.pucman.common.exception;

/**
 * @see TryUtil
 *
 */
@FunctionalInterface
public interface TryRunnable
{
    void run() throws Exception;
}
