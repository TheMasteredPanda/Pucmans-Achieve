package io.pucman.sql.operation.crud;

import io.pucman.common.exception.DeveloperException;
import io.pucman.common.reflect.accessors.FieldAccessor;
import io.pucman.sql.database.Database;
import io.pucman.sql.operation.DatabaseStatement;
import io.pucman.sql.util.OperationUtil;
import lombok.SneakyThrows;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Builder for insert operations.
 */
public class InsertOperation<T> extends DatabaseStatement<Void>
{
    private String tableName;
    private LinkedList<FieldAccessor> mappedFields;
    private T instance;
    private ReentrantLock lock = new ReentrantLock();

    /**
     * @param database - database instance.
     * @param instance - object that will be inserted into the specified table.
     */
    public InsertOperation(Database database, T instance)
    {
        super(database);
        this.tableName = OperationUtil.getTableName(instance);
        this.mappedFields = OperationUtil.getMappedFields(instance);
        this.instance = instance;
    }

    /**
     * Constructs the statement.
     * @return PreparedStatement instance.
     */
    @Override @SneakyThrows
    protected PreparedStatement construct()
    {
        return getConnection().prepareStatement(String.format("INSERT INTO %s VALUES(%s);", tableName, OperationUtil.getJoinedColumns(instance, mappedFields)));
    }

    /**
     * Executes the query asynchronously.
     */
    @Override
    public Void async()
    {
        getService().submit(this::process);
        return null;
    }

    /**
     * Executes the query synchronously.
     */
    @Override
    public synchronized Void sync()
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
