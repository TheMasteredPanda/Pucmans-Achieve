package io.pucman.sql.util;

import com.google.common.collect.Lists;
import io.pucman.common.exception.DeveloperException;
import io.pucman.common.exception.UtilException;
import io.pucman.sql.annotation.Column;
import io.pucman.sql.annotation.Table;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;

public final class OperatonUtil
{
    private OperatonUtil()
    {
        throw new UtilException();
    }

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

    public static <T> LinkedList<Field> getMappedFields(T object)
    {
        Class clazz = object.getClass();
        LinkedList<Field> fields = Lists.newLinkedList();

        for (Field f : clazz.getDeclaredFields()) {
            if (!f.isAnnotationPresent(Column.class)) {
                continue;
            }

            fields.add(f);
        }

        if (fields.isEmpty()) {
            throw new DeveloperException("No fields annotated with 'Column' were found in class " + clazz.getName() + ".");
        }

        return fields;
    }
}
