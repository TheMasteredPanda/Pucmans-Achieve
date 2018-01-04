package io.pucman.sql.operation.raw;

import com.google.common.util.concurrent.ListenableFuture;
import io.pucman.common.exception.DeveloperException;
import io.pucman.common.generic.GenericUtil;
import io.pucman.sql.database.Database;
import io.pucman.sql.operation.ConditionOperation;
import io.pucman.sql.util.OperatonUtil;
import lombok.Getter;
import lombok.SneakyThrows;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

/**
 * Used in other predefined operations. This will build and execute a statement that is expecting a resultSet.
 * @param <T>
 */
@Getter
public class ObjectOperation<T> extends ConditionOperation<T>
{
    private String query;
    private Object[] values;
    private OperationProcessor<PreparedStatement, Void> statementProcessor = null;
    private OperationProcessor<ResultSet, T> dataProcessor = null;
    private OperationProcessor<T, Void> postTransactionProcessor = null;

    public ObjectOperation(Database database)
    {
        super(database);
    }

    public ObjectOperation query(String query)
    {
        this.query = query;
        return this;
    }

    public ObjectOperation values(Object... values)
    {
        this.values = values;
        return this;
    }

    public ObjectOperation processStatemeent(OperationProcessor<PreparedStatement, Void> processor)
    {
        this.statementProcessor = processor;
        return this;
    }

    public ObjectOperation processData(OperationProcessor<ResultSet, T> processor)
    {
        this.dataProcessor = processor;
        return this;
    }

    public ObjectOperation postTransaction(OperationProcessor<T, Void> processor)
    {
        this.postTransactionProcessor = processor;
        return this;
    }

    @Override @SneakyThrows
    protected PreparedStatement construct()
    {
        return getConnection().prepareStatement(query + " " + getConditionString() + ";");
    }

    @Override
    public T sync()
    {
        PreparedStatement statement = construct();
        ResultSet resultSet = null;
        T instance = null;


        try {
            if (statementProcessor != null) {
                statementProcessor.accept(statement);
            }

            resultSet = statement.executeQuery();

            if (dataProcessor != null) {
                instance = dataProcessor.accept(resultSet);
            } else {
                instance = GenericUtil.cast(resultSet);
            }

            if (postTransactionProcessor != null && instance != null) {
                postTransactionProcessor.accept(instance);
            }
        } catch (SQLException e) {
            throw new DeveloperException(e);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OperatonUtil.close(resultSet, statement, getConnection());
        }

        return instance;
    }

    @Override
    public T async(boolean blocking)
    {
        if (blocking) {
            ListenableFuture<T> future = getService().submit(() -> {
                PreparedStatement statement = construct();
                ResultSet set;
                T instance;

                if (statementProcessor != null) {
                    statementProcessor.accept(statement);
                }

                try {
                    set = statement.executeQuery();

                    if (dataProcessor != null) {
                        instance = dataProcessor.accept(set);
                    } else {
                        instance = GenericUtil.cast(set);
                    }

                    if (postTransactionProcessor != null) {
                        postTransactionProcessor.accept(instance);
                    }

                    return instance;
                } catch (SQLException e) {
                    throw new DeveloperException(e);
                } finally {
                    OperatonUtil.close(getConnection(), statement);
                }
            });


            try {
                return future.get(20, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }
        } else {
            CompletableFuture.supplyAsync((Supplier<ResultSet>) () -> {
                PreparedStatement statement = construct();
                ResultSet set = null;

                try {
                    if (statementProcessor != null) {
                        statementProcessor.accept(statement);
                    }

                    set = statement.executeQuery();
                } catch (SQLException e) {
                    throw new DeveloperException(e);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    OperatonUtil.close(getConnection(), statement);
                }

                return set;
            });

            //TODO data processor and post transaction processor.
        }

        return null;
    }
}
