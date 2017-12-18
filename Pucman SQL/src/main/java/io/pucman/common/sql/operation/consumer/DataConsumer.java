package io.pucman.common.sql.operation.consumer;

import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.ResultSet;

@FunctionalInterface
@ParametersAreNonnullByDefault
public interface DataConsumer
{
    void consume(ResultSet result) throws Exception;
}
