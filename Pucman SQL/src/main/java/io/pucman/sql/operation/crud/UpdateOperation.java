package io.pucman.sql.operation.crud;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.pucman.sql.database.Database;
import io.pucman.sql.operation.ConditionOperation;
import lombok.NonNull;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class UpdateOperation<T> extends ConditionOperation<Void>
{
    private String tableName;
    private LinkedList<Field> mappedFields = Lists.newLinkedList();
    private LinkedHashMap<String, Object> values = Maps.newLinkedHashMap();
    private T instance;

    public UpdateOperation(Database database)
    {
        super(database);
    }

    public UpdateOperation update(String tableName)
    {
        this.tableName = tableName;
        return this;
    }

    public UpdateOperation set(T instance)
    {
        this.instance = instance;
        return this;
    }

    public UpdateOperation set(@NonNull String column, @NonNull Object value)
    {
        this.values.put(column, value);
        return this;
    }

    @Override
    protected PreparedStatement construct()
    {
        //TODO
        return null;
    }

    @Override
    public Void async()
    {
        return null;
    }

    @Override
    public Void sync()
    {
        return null;
    }
}
