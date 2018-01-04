package io.pucman.sql.operation;

import com.google.common.collect.Lists;
import io.pucman.common.exception.DeveloperException;
import io.pucman.sql.database.Database;
import io.pucman.sql.util.OperatonUtil;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Builder for insert operations.
 */
public class InsertOperation<T> extends DatabaseStatement<Void>
{
    private String tableName;
    private LinkedList<Field> mappedFields = Lists.newLinkedList();
    private T instance;

    public InsertOperation(Database database)
    {
        super(database);
        this.tableName = tableName;
    }

    public InsertOperation insert(T instance)
    {
        this.tableName = OperatonUtil.getTableName(instance);
        this.mappedFields = OperatonUtil.getMappedFields(instance);
        return this;
    }

    @Override @SneakyThrows
    protected PreparedStatement construct()
    {
        return getConnection().prepareStatement(String.format("INSERT INTO %s VALUES(%s);", tableName, getJoinedColumns()));
    }

    protected String getJoinedColumns()
    {
        return mappedFields.stream().map(field -> {
            field.setAccessible(true);

            try {
                return field.getName() + "=" + field.get(instance);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            return null;
        }).collect(Collectors.joining(", "));
    }

    @Override
    public Void async(boolean blocking)
    {
        PreparedStatement statement = construct();
        if (blocking) {
            CountDownLatch latch = new CountDownLatch(1);

            getService().submit(() -> {
                try {
                    statement.execute();
                } catch (SQLException e) {
                    throw new DeveloperException(e);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    OperatonUtil.close(statement, getConnection());
                }
            });

            try {
                latch.await(15, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            CompletableFuture.runAsync(() -> {
                try {
                    statement.execute();
                } catch (SQLException e) {
                    throw new DeveloperException(e);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    OperatonUtil.close(statement, getConnection());
                }
            }, getService());
        }

        return null;
    }

    @Override
    public Void sync()
    {
        PreparedStatement statement = this.construct();

        try {
            statement.execute();
        } catch (SQLException e) {
            throw new DeveloperException(e);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OperatonUtil.close(statement, this.getConnection());
        }

        return null;
    }
}
