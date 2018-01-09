package io.pucman.sql.operation.crud;

import io.pucman.common.exception.DeveloperException;
import io.pucman.sql.database.Database;
import io.pucman.sql.operation.ConditionOperation;
import io.pucman.sql.util.OperationUtil;
import lombok.SneakyThrows;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Builder for delete operations.
 * @param <T>
 */
public class DeleteOperation<T> extends ConditionOperation<Void>
{
    private T instance;
    private String tableName;
    private ReentrantLock lock = new ReentrantLock();

    public DeleteOperation(Database database, T instance)
    {
        super(database);
        this.instance = instance;
    }

    public DeleteOperation(Database database, String tableName)
    {
        super(database);
        this.tableName = tableName;
    }

    /**
     * Constructs the operation.
     * @return a PreparedStatement instance.
     */
    @Override @SneakyThrows
    protected PreparedStatement construct()
    {
        if (instance != null) {
            return getConnection().prepareStatement(String.format("DELETE FROM %s WHERE %s=%s", OperationUtil.getTableName(instance), OperationUtil.getPrimaryName(instance), OperationUtil.getPrimaryValue(instance)));
        } else {
            return getConnection().prepareStatement(String.format("DELETE FROM %s WHERE %s", tableName, getConditionString()));
        }
    }


    /**
     * Executes the operation asynchronously.
     */
    @Override
    public Void async()
    {
        getService().submit(this::process);
        return null;
    }

    /**
     * Executes the operation synchronously.
     */
    @Override
    public Void sync()
    {
        try {
            lock.lock();
            process();
        } finally {
            lock.unlock();
        }

        return null;
    }

    @SneakyThrows
    private void process()
    {
        PreparedStatement statement = construct();

        try {
            statement.execute();
        } catch (SQLException e) {
            throw new DeveloperException(e);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OperationUtil.close(getConnection(), statement);
        }
    }
}
