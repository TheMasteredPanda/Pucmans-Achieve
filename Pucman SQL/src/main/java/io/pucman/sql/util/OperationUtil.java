package io.pucman.sql.util;

import com.google.common.collect.Lists;
import io.pucman.common.exception.DeveloperException;
import io.pucman.common.exception.UtilException;
import io.pucman.common.generic.GenericUtil;
import io.pucman.common.math.NumberUtil;
import io.pucman.common.reflect.ReflectUtil;
import io.pucman.common.reflect.accessors.FieldAccessor;
import io.pucman.sql.DataType;
import io.pucman.sql.annotation.Column;
import io.pucman.sql.annotation.Table;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public final class OperationUtil
{
    private OperationUtil()
    {
        throw new UtilException();
    }

    /**
     * Closes Connections, Statements, and ResultSets.
     * @param objects
     */
    @SneakyThrows
    public static void close(Object... objects)
    {
        for (Object o : objects) {
            if (o instanceof Connection) {
                Connection c = (Connection)o;
                if (!c.isClosed()) c.close();
            }

            if (o instanceof Statement) {
                Statement s = (Statement) o;
                if (!s.isClosed()) s.close();
            }

            if (o instanceof ResultSet) {
                ResultSet rS = (ResultSet) o;
                if (!rS.isClosed()) rS.close();
            }
        }
    }

    @SneakyThrows
    public static <T> String getTableName(T object)
    {
        Class clazz = object.getClass();

        if (!clazz.isAnnotationPresent(Table.class)) {
            throw new DeveloperException("Annotation 'Table' was not found for object " + object + ".");
        }

        Table table = (Table) clazz.getAnnotation(Table.class);
        return table.value();
    }

    public static <T> LinkedList<FieldAccessor> getMappedFields(T object)
    {
        Class clazz = object.getClass();
        LinkedList<FieldAccessor> fields = Lists.newLinkedList();

        for (Field f : clazz.getDeclaredFields()) {
            if (!f.isAnnotationPresent(Column.class)) {
                continue;
            }

            fields.add(ReflectUtil.wrapField(f));
        }

        if (fields.isEmpty()) {
            throw new DeveloperException("No fields annotated with 'Column' were found in class " + clazz.getName() + ".");
        }

        return fields;
    }


    /**
     * Formats all mapped fields into a string that'll be used in the construction of the query.
     * @return the string.
     */
    public static <T> String getJoinedColumns(LinkedList<Field> mappedFields, T instance)
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

    /**
     * Gets the raw fields from the FieldAccessor wrappers and invokes this#getJoinedColumns(..);
     *
     * @see FieldAccessor
     * @see this#getJoinedColumns(LinkedList, Object)
     *
     * @param instance - object instance.
     * @param mappedFields - list of field accessors.
     * @param <T> - object generic type.
     * @return the string.
     */
    public static <T> String getJoinedColumns(T instance, LinkedList<FieldAccessor> mappedFields)
    {
        return getJoinedColumns(mappedFields.stream().map(FieldAccessor::get).collect(Collectors.toCollection(Lists::newLinkedList)), instance);
    }

    /**
     * Gets the sql data type that corresponds to the class.
     * @param clazz - class.
     * @return if there is a corresponding data type, it will return that. Else null.
     */
    public static DataType getCorrespondingDataType(Class clazz)
    {
        for (DataType type : DataType.values()) {
            if (!type.getCorrespondingClasses().contains(clazz)) {
                continue;
            }

            return type;
        }

        throw new DeveloperException("Couldn't find data type for " + clazz.getName() + ".");
    }

    /**
     * Checks if the value held by the field is within the minimum and maximum
     * boundaries.
     * @param instance - instance of the class the field is located in.
     * @param field - field to check.
     * @return true if the value is acceptable, else false.
     */
    @SneakyThrows
    public static <T> boolean isAcceptableValue(T instance, Field field)
    {
        field.setAccessible(true);

        if (!(field.get(instance) instanceof Number)) {
            throw new DeveloperException("Field is not an instance of number.");
        }

        double value = NumberUtil.parse((String) field.get(instance), double.class);
        DataType type = getCorrespondingDataType(field.getType());
        return type.getMinimumAcceptedValue() <= value && type.getMaximumAcceptValue() >= value;
    }

    /**
     * Checks if the data type has a digit range.
     * @param dataType - data type to check.
     * @return true if it does, else false.
     */
    public static boolean hasDigitRange(DataType dataType)
    {
        return dataType.getMaximumDigitSize() == -1;
    }

    /**
     * Checks if the data type has a decimal range.
     * @param dataType - data type to check.
     * @return true if it does, else false.
     */
    public static boolean hasDecimalRange(DataType dataType)
    {
        return dataType.getSupportedDecimals() == -1;
    }

    /**
     * Gets the value of the primary key.
     * @param instance - instance to get the primary value from.
     * @return field value.
     */
    public static <T> Object getPrimaryValue(T instance)
    {
        FieldAccessor accessor = getPrimaryField(instance);
        return GenericUtil.cast(accessor.get(instance));
    }

    /**
     * Gets the field that is also the primary key.
     * @param instance - instance the field is in.
     * @param <T> = generic type.
     * @return field.
     */
    public static <T> FieldAccessor getPrimaryField(T instance)
    {
        Class clazz = instance.getClass();

        if (!clazz.isAnnotationPresent(Table.class)) {
            throw new DeveloperException("Instance of " + clazz.getName() + " doesn't have the annotation Table. Can't get the primary field.");
        }

        Table t = (Table) clazz.getAnnotation(Table.class);
        return ReflectUtil.getField(clazz, t.primaryKey(), ReflectUtil.Type.DECLARED);
    }

    /**
     * Gets the name of the field that is also the primary key.
     * @param instance - instance to get the primary field from.
     * @param <T> - generic type.
     * @return field name.
     */
    public static <T> String getPrimaryName(T instance)
    {
        FieldAccessor accessor = getPrimaryField(instance);
        return accessor.get().getName();
    }

    /**
     * Gets the amount of rows fetched in the ResultSet.
     * @param set - the set of results.
     * @return the amount of rows fetched.
     */
    @SneakyThrows
    public static int getFetchSize(ResultSet set)
    {
        int row = 0;

        if (set.last()) {
            row = set.getRow();
            set.beforeFirst();
        }

        return row;
    }

    /**
     * Gets the amount of columns fetched in the ResultSet.
     * @param set - the set of results.
     * @return the amount of columns fetched.
     */
    @SneakyThrows
    public static int getColumnCount(ResultSet set)
    {
        ResultSetMetaData meta = set.getMetaData();
        return meta.getColumnCount();
    }

    /**
     * Gets all the names of the columns fetched.
     * @param set - the set of results.
     * @return a list of unordered column names.
     */
    @SneakyThrows
    public static List<String> getColumnNames(ResultSet set)
    {
        ResultSetMetaData meta = set.getMetaData();
        ArrayList<String> result = Lists.newArrayList();

        for (int i = 0; i < meta.getColumnCount(); i++) {
            result.add(meta.getColumnName(i));
        }

        return result;
    }
}