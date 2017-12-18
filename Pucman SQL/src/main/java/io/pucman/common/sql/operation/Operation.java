package io.pucman.common.sql.operation;

import com.google.inject.Singleton;
import io.pucman.common.exception.DeveloperException;
import io.pucman.common.exception.TryUtil;
import io.pucman.common.sql.operation.exception.OperationBuilderException;
import io.pucman.common.sql.util.OperationUtil;

import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * Operation class. Meant for building an operation that returns no results.
 */
@Singleton
@ParametersAreNonnullByDefault
public class Operation
{
    public Builder with(Connection connection)
    {
        return new Builder(connection);
    }

    /**
     * Operation builder.
     */
    @ParametersAreNonnullByDefault
    public class Builder
    {
        private Connection connection;
        private PreparedStatement statement;
        private Consumer<PreparedStatement> statementConsumer;
        private Runnable postTransactionTask;

        Builder(Connection connection)
        {
            this.connection = connection;
        }

        public Builder query(String query)
        {
            try {
                statement = connection.prepareStatement(query);
            } catch (SQLException e) {
                throw new DeveloperException(e);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return this;
        }

        public Builder consume(Consumer<PreparedStatement> consumer)
        {
            this.statementConsumer = consumer;
            return this;
        }

        public Builder withPostTransactionTask(Runnable postTransactionTask)
        {
            this.postTransactionTask = postTransactionTask;
            return this;
        }

        public void sync()
        {
            if (statement == null) {
                throw new OperationBuilderException("Cannot execute an operation that is null.");
            }


            try {
                if (statementConsumer != null) {
                    statementConsumer.accept(statement);
                }

                statement.execute();

                if (postTransactionTask != null) {
                    postTransactionTask.run();
                }
            } catch (SQLException e) {
                throw new DeveloperException(e);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                OperationUtil.close(connection, statement);
            }
        }

        public void async(ExecutorService service, boolean blocking)
        {
            if (statement == null) {
                throw new OperationBuilderException("Cannot execute an operation that is null.");
            }

            if (statementConsumer != null) {
                statementConsumer.accept(statement);
            }

            if (blocking) {
                Future<Boolean> future = service.submit((Callable<Boolean>) statement::execute);

                try {
                    future.get();
                    postTransactionTask.run();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            } else {
                CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> TryUtil.sneaky(statement::execute, Boolean.class), service);
                future.thenRun(postTransactionTask);
            }
        }
    }
}
