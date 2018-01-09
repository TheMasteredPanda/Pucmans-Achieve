package io.pucman.sql.operation.crud;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFuture;
import io.pucman.common.exception.DeveloperException;
import io.pucman.common.reflect.accessors.FieldAccessor;
import io.pucman.sql.database.Database;
import io.pucman.sql.operation.ConditionOperation;
import io.pucman.sql.util.OperationUtil;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Builder for update operations.
 * @param <T> - object that will be used to update it's deserialized data on the sql database.
 */
public class UpdateOperation<T> extends ConditionOperation<Void>
{
    private String tableName;
    private LinkedList<FieldAccessor> mappedFields = Lists.newLinkedList();
    private LinkedHashMap<String, Object> values = Maps.newLinkedHashMap();
    private T instance;

    public UpdateOperation(Database database, String tableName)
    {
        super(database);
        this.tableName = tableName;
    }

    /**
     * Set the object that will be used to update it's deserialized data on the sql database.
     * @param instance - object.
     * @return this.
     */
    public UpdateOperation set(T instance)
    {
        this.instance = instance;
        this.tableName = OperationUtil.getTableName(instance);
        this.mappedFields = OperationUtil.getMappedFields(instance);
        return this;
    }

    /**
     * If an object is not passed, then you can specify which rows to update.
     * @param column - column name.
     * @param value - value to set it to.
     * @return this.
     */
    public UpdateOperation set(@NonNull String column, @NonNull Object value)
    {
        this.values.put(column, value);
        return this;
    }

    /**
     * Constructs the statement.
     * @return PreparedStatement instance.
     */
    @Override @SneakyThrows
    protected PreparedStatement construct()
    {
        String query = "UPDATE %s SET %s";

        if (instance != null) {
            query = String.format(query, tableName, OperationUtil.getJoinedColumns(instance, mappedFields));
        } else {
            StringBuilder sb = new StringBuilder();
            int iteration = 0;

            for (Map.Entry<String, Object> value : values.entrySet()) {
                iteration++;
                sb.append(value.getKey()).append("=").append(value.getValue().toString());

                if (iteration != values.size()) {
                    sb.append(", ");
                }
            }

            query = String.format(query, tableName, sb.toString());
        }

        return getConnection().prepareStatement(query);
    }

    /**
     * Executes the operation asynchronously.
     */
    @Override
    public Void async()
    {
        getService().submit(() -> {
            PreparedStatement statement = construct();

            try {
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new DeveloperException(e);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                OperationUtil.close(getConditionString(), statement);
            }
        });

        return null;
    }

    /**
     * Executes the operation synchronously.
     */
    @Override
    public Void sync()
    {
        ReentrantLock lock = new ReentrantLock();

        ListenableFuture<Boolean> future = getService().submit(() -> {
            PreparedStatement statement = construct();

            try {
                lock.lock();
                statement.executeUpdate();
            } catch (SQLException e) {

                throw new DeveloperException(e);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                OperationUtil.close(getConnection(), statement);
            }

            return true;
        });

        future.addListener(lock::unlock, getService());
        return null;
    }
}
