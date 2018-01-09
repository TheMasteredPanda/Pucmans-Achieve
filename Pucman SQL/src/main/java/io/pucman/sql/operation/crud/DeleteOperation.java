package io.pucman.sql.operation.crud;

import io.pucman.sql.database.Database;
import io.pucman.sql.operation.ConditionOperation;
import io.pucman.sql.util.OperationUtil;
import lombok.SneakyThrows;

import java.sql.PreparedStatement;

/**
 * Builder for delete operations.
 * @param <T>
 */
public class DeleteOperation<T> extends ConditionOperation<Void>
{
    private T instance;
    private String tableName;

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

    @Override @SneakyThrows
    protected PreparedStatement construct()
    {
        if (instance != null) {
            return getConnection().prepareStatement(String.format("DELETE FROM %s WHERE %s=%s", OperationUtil.getTableName(instance), OperationUtil.getPrimaryName(instance), OperationUtil.getPrimaryValue(instance)));
        } else {
            return getConnection().prepareStatement(String.format("DELETE FROM %s WHERE %s", tableName, getConditionString()));
        }
    }


    @Override
    public Void async()
    {
        //TODO
        return null;
    }

    @Override
    public Void sync()
    {
       //TODO
        return null;
    }
}
