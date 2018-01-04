package io.pucman.sql.operation.raw;

import javax.annotation.ParametersAreNonnullByDefault;

@FunctionalInterface
@ParametersAreNonnullByDefault
public interface OperationProcessor<T, T1>
{
     T1 accept(T instance) throws Exception;
}
