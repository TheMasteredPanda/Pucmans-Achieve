package io.pucman.sql.operation.crud;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import io.pucman.common.exception.DeveloperException;
import io.pucman.common.generic.GenericUtil;
import io.pucman.common.reflect.ReflectUtil;
import io.pucman.common.reflect.accessors.ConstructorAccessor;
import io.pucman.common.reflect.accessors.FieldAccessor;
import io.pucman.sql.DataType;
import io.pucman.sql.annotation.Column;
import io.pucman.sql.database.Database;
import io.pucman.sql.operation.ConditionOperation;
import io.pucman.sql.util.OperationUtil;
import lombok.SneakyThrows;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

/**
 * Builder for select operations.
 * @param <T> - return type.
 * TODO: documentation.
 */
public class FetchOperation<T> extends ConditionOperation<T>
{
    private String tableName;
    private Class template;
    private boolean singleFetch = false;
    private boolean structureAsList = false;
    private boolean structureAsMultiMap = false;

    //For raw fetching without serializing.
    private String[] columnNames;

    public FetchOperation(Database database, Class template)
    {
        super(database);
        this.template = template;
    }

    public FetchOperation(Database database, String... columnNames)
    {
        super(database);
        this.columnNames = columnNames;
    }

    /**
     * Constructs the operation.
     * @return a PreparedStatement instance.
     */
    @Override @SneakyThrows
    protected PreparedStatement construct()
    {
        return getConnection().prepareStatement(String.format("SELECT %s FROM %s WHERE %s", null, tableName, null));
    }

    /**
     * Once the data fetched is either serialized, or the fetch is only one column in a table,
     * it will store and return it as a LinkedList.
     * @return LinkedList of the serialized or raw data.
     */
    public FetchOperation asList()
    {
        structureAsList = !structureAsList;
        return this;
    }

    /**
     * Onc the data fetched is either serialized, or the fetch is more than one column in the table,
     * it will store and return it as a Multimap.
     * @return Multimap of serialized or raw data.
     */
    public FetchOperation asMultimap()
    {
        structureAsMultiMap = !structureAsMultiMap;
        return this;
    }

    /**
     * Specifying the table the data will be fetched from.
     * @param tableName - table name.
     * @return this.
     */
    public FetchOperation from(String tableName)
    {
        this.tableName = tableName;
        return this;
    }

    /**
     * Executes the operation asynchronously.
     * @return data that has either been structured in a list or map, serialized objects that have been stored in a list or map, or a serialized object.
     */
    @Override
    public T async()
    {
        ListenableFuture<T> future = getService().submit(this::process);


        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Executes the operation synchronously.
     * @return data that has either been structured in a list or map, serialized objects that have been stored in a list or map, or a serialized object.
     */
    @Override
    public T sync()
    {
        ListenableFutureTask<T> future = ListenableFutureTask.create(this::process);

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * The actual operation.
     * @return data.
     */
    @SneakyThrows
    private T process()
    {
        PreparedStatement statement = construct();
        ResultSet set = null;

        try {
            set = statement.executeQuery();
        } catch (SQLException e) {
            throw new DeveloperException(e);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OperationUtil.close(getConnection(), statement);
        }

        if (set == null) {
            throw new DeveloperException("ResultSet is null.");
        }

        ConstructorAccessor constructor = ReflectUtil.getConstructor(template, ReflectUtil.Type.DECLARED);


        if (singleFetch) {
            T instance = GenericUtil.cast(constructor.call());
            LinkedList<FieldAccessor> mappedFields = OperationUtil.getMappedFields(instance);

            for (FieldAccessor accessor : mappedFields) {
                Class fieldDataType = accessor.getType();
                DataType type = OperationUtil.getCorrespondingDataType(fieldDataType);

                String columnName;
                Column column = (Column) accessor.getAnnotation(Column.class, ReflectUtil.Type.DECLARED);

                if (column.value().equalsIgnoreCase("null")) {
                    columnName = accessor.getName();
                } else {
                    columnName = column.value();
                }

                if (!type.getCorrespondingClasses().contains(fieldDataType)) {
                    throw new DeveloperException("Field " + accessor.getName() + " does not have a corresponding data type. Data Type is: " + fieldDataType.getName() + ".");
                }

                try {
                    accessor.set(instance, set.getObject(columnName));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                OperationUtil.close(set);
                return GenericUtil.cast(instance);
            }
        } else if (OperationUtil.getFetchSize(set) > 1) {
            if (structureAsList && (template != null || OperationUtil.getColumnCount(set) == 1)) {
                LinkedList<Object> result = Lists.newLinkedList();

                if (template == null) {
                    while (set.next()) {
                        result.add(GenericUtil.cast(set.getObject(1)));
                    }
                } else {
                    while (set.next()) {
                        Object instance = GenericUtil.cast(constructor.call());
                        LinkedList<FieldAccessor> mappedFields = OperationUtil.getMappedFields(instance);

                        for (FieldAccessor accessor : mappedFields) {
                            Class fieldDataType = accessor.getType();
                            DataType type = OperationUtil.getCorrespondingDataType(fieldDataType);

                            if (!type.getCorrespondingClasses().contains(fieldDataType)) {
                                throw new DeveloperException("Field " + accessor.getName() + " does not have a corresponding data type, Data Type is: " + fieldDataType.getName() + ".");
                            }

                            String columnName;

                            Column column = (Column) accessor.getAnnotation(Column.class, ReflectUtil.Type.DECLARED);

                            if (column.value().equals("null")) {
                                columnName = accessor.getName();
                            } else {
                                columnName = column.value();
                            }

                            try {
                                accessor.set(instance, set.getObject(columnName));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        result.add(instance);
                    }
                }

                OperationUtil.close(set);
                return GenericUtil.cast(result);
            }

            if (structureAsMultiMap && (template != null || OperationUtil.getColumnCount(set) > 1)) {

                if (template == null) {
                    Multimap<String, Object> result = LinkedListMultimap.create();

                    while (set.next()) {
                        for (String name : OperationUtil.getColumnNames(set)) {
                            result.put(name, set.getObject(name));
                        }
                    }

                    OperationUtil.close(set);
                    return GenericUtil.cast(result);
                } else {
                    Multimap<Integer, Object> result = LinkedListMultimap.create();

                    while (set.next()) {
                        T instance = GenericUtil.cast(constructor.call());
                        LinkedList<FieldAccessor> mappedFields = OperationUtil.getMappedFields(instance);

                        int iteration = 0;

                        for (FieldAccessor accessor : mappedFields) {
                            Class fieldDataType = accessor.getType();
                            DataType type = OperationUtil.getCorrespondingDataType(fieldDataType);

                            if (!type.getCorrespondingClasses().contains(fieldDataType)) {
                                throw new DeveloperException("Field " + accessor.getName() + " does not have a corresponding data type. Data Type is: " + fieldDataType.getName() + ".");
                            }

                            Column column = (Column) accessor.getAnnotation(Column.class, ReflectUtil.Type.DECLARED);
                            String columnName;

                            if (column.value().equals("null")) {
                                columnName = accessor.getName();
                            } else {
                                columnName = column.value();
                            }

                            try {
                                accessor.set(instance, set.getObject(columnName));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        iteration++;
                        result.put(iteration, instance);
                    }

                    OperationUtil.close(set);
                    return GenericUtil.cast(result);
                }
            }
        }

        return null;
    }
}