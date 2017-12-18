package io.pucman.common.sql.operation.consumer;

import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
@ParametersAreNonnullByDefault
public interface StatementConsumer
{
    void consume(PreparedStatement statement) throws SQLException;
}
