package io.pucman.common.sql.operation.consumer;

import javax.annotation.ParametersAreNonnullByDefault;

@FunctionalInterface
@ParametersAreNonnullByDefault
public interface PostTransactionConsumer<T>
{
    void consume(T t) throws Exception;
}
