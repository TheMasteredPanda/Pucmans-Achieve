package io.pucman.common.sql.database;

import io.pucman.common.exception.DeveloperException;
import io.pucman.common.sql.client.Client;
import io.pucman.common.sql.operation.consumer.StatementConsumer;
import io.pucman.common.sql.util.ErrorConsumer;
import lombok.NonNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Object Mapped Database Template.
 */
public class Database
{
    private Client client;

    protected Database(Client client)
    {
        this.client = client;
    }

    /**
     * Builds a statement and executes it synchronously.
     * @param query - query.
     * @param statementConsumer - statement consumer.
     * @param postTransaction - post transaction task.
     */
    public void sync(@NonNull String query, StatementConsumer statementConsumer, Runnable postTransaction)
    {
        Connection connection = client.getOpenConnection();
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(query);

            if (statementConsumer != null) {
                statementConsumer.consume(statement);
            }
        } catch (SQLException e) {
            throw new DeveloperException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (statement == null) {
            throw new DeveloperException("Statement is null");
        }

        try {
            synchronized (statement) {
                statement.execute();
            }

            if (postTransaction != null) {
                postTransaction.run();
            }
        } catch (SQLException e) {
            ErrorConsumer.consume(e.getErrorCode());
        } finally {
            client.close(connection, statement);
        }
    }

    public void sync(@NonNull String query, StatementConsumer statementConsumer)
    {
        sync(query, statementConsumer, null);
    }

    public void sync(@NonNull String query)
    {
        sync(query, null);
    }
}
