package io.pucman.sql.operation.crud;

import com.google.common.util.concurrent.ListenableFuture;
import io.pucman.common.exception.DeveloperException;
import io.pucman.sql.database.Database;
import io.pucman.sql.operation.DatabaseStatement;
import io.pucman.sql.util.OperatonUtil;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

/**
 * Builder for insert operations.
 */
public class InsertOperation<T> extends DatabaseStatement<Void>
{
    private String tableName;
    private LinkedList<Field> mappedFields;
    private T instance;

    /**
     * @param database - database instance.
     * @param instance - object that will be inserted into the specified table.
     */
    public InsertOperation(Database database, T instance)
    {
        super(database);
        this.tableName = OperatonUtil.getTableName(instance);
        this.mappedFields = OperatonUtil.getMappedFields(instance);
        this.instance = instance;
    }

    /**
     * Constructs the statement.
     * @return PreparedStatement instance.
     */
    @Override @SneakyThrows
    protected PreparedStatement construct()
    {
        return getConnection().prepareStatement(String.format("INSERT INTO %s VALUES(%s);", tableName, OperatonUtil.getJoinedColumns(mappedFields, instance)));
    }

    /**
     * Executes the query asynchronously.
     */
    @Override
    public Void async()
    {
        ListenableFuture<Void> future = getService().submit(() -> {
            PreparedStatement statement = construct();

            try {
                statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                OperatonUtil.close(getConnection(), statement);
            }

            return null;
        });

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new DeveloperException(e);
        }
    }

    /**
     * Executes the query synchronously.
     */
    @Override
    public synchronized Void sync()
    {
        PreparedStatement statement = construct();

        try {
            statement.execute();
        } catch (SQLException e) {
            throw new DeveloperException(e);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OperatonUtil.close(getConnection(), statement);
        }

        return null;
    }
}
