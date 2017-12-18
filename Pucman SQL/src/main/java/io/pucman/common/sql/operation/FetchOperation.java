package io.pucman.common.sql.operation;

import com.google.inject.Singleton;
import io.pucman.common.exception.DeveloperException;
import io.pucman.common.exception.TryUtil;
import io.pucman.common.sql.operation.exception.OperationBuilderException;
import io.pucman.common.sql.util.OperationUtil;

import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * Operation class. Meant for building an operation that returns a result.
 */
@Singleton
@ParametersAreNonnullByDefault
public class FetchOperation
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
        private Consumer<ResultSet> postTransactionTask;

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

        public Builder withPostTransactionTask(Consumer<ResultSet> postTransactionTask)
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


                if (postTransactionTask != null) {
                    postTransactionTask.accept(statement.executeQuery());
                } else {
                    throw new OperationBuilderException("Cannot execute an operation that returns a ResultSet without a post transaction task.");
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
                Future<ResultSet> future = service.submit(() -> statement.executeQuery());

                try {
                    if (postTransactionTask != null) {
                        postTransactionTask.accept(future.get());
                    } else {
                        throw new OperationBuilderException("Cannot execute an operation that returns a ResultSet without a post transaction task.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                CompletableFuture<ResultSet> future = CompletableFuture.supplyAsync(() -> TryUtil.sneaky(statement::executeQuery, ResultSet.class), service);
                future.thenAccept(postTransactionTask);
            }
        }
    }
}
