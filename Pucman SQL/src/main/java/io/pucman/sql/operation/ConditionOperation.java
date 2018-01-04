package io.pucman.sql.operation;

import com.google.common.collect.Maps;
import io.pucman.sql.database.Database;

import java.util.HashMap;
import java.util.stream.Collectors;

public abstract class ConditionOperation<T> extends DatabaseStatement<T>
{
    private final HashMap<String, Object> conditions = Maps.newHashMap();

    public ConditionOperation(Database database)
    {
        super(database);
    }

    public ConditionOperation where(String key, Object value)
    {
        this.conditions.put(key, value);
        return this;
    }

    public String getConditionString()
    {
        return conditions.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue().toString()).collect(Collectors.joining(" AND "));
    }
}
