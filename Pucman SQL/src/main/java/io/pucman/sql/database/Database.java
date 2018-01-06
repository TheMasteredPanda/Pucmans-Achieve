package io.pucman.sql.database;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.pucman.common.exception.DeveloperException;
import io.pucman.sql.DataType;
import io.pucman.sql.annotation.Column;
import io.pucman.sql.annotation.Table;
import io.pucman.sql.operation.crud.InsertOperation;
import io.pucman.sql.operation.crud.UpdateOperation;
import io.pucman.sql.operation.raw.VoidOperation;
import io.pucman.sql.util.OperatonUtil;
import lombok.Getter;
import lombok.SneakyThrows;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.Executors;

@ParametersAreNonnullByDefault
public class Database
{
    private HikariDataSource source;

    @Getter
    private ListeningExecutorService service;

    @Getter
    private HashMap<String, Class> templateClasses = Maps.newHashMap();

    public Database(HikariConfig config, int threadPoolSize)
    {
        service = threadPoolSize >= 1 ? MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(threadPoolSize)) : MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
        source = new HikariDataSource(config);
    }

    /**
     * Create a table from a template class.
     * @param template - template class.
     * @param async = if true it will run asynchronously, else synchronously.
     * @return true if successful, else false.
     */
    public boolean create(Class<?> template, boolean async)
    {
        if (!template.isAnnotationPresent(Table.class)) {
            throw new DeveloperException("Can't create a table without the @Table annotation.");
        }

        LinkedList<Field> columns = OperatonUtil.getMappedFields(template);
        LinkedHashMap<Field, DataType> types = Maps.newLinkedHashMap();

        for (Field column : columns) {
            DataType type = OperatonUtil.getCorrespondingDataType(column.getType());
            types.put(column, type);
        }

        StringBuilder columnQuerySection = new StringBuilder();

        Table table = template.getAnnotation(Table.class);
        ArrayList<String> uniqueKeys = Lists.newArrayList(table.uniqueKeys());

        int iteration = 0;

        for (Map.Entry<Field, DataType> type : types.entrySet()) {
            iteration++;
            StringBuilder sb = new StringBuilder();
            boolean digitRange = OperatonUtil.hasDigitRange(type.getValue());
            boolean decimalRange = OperatonUtil.hasDecimalRange(type.getValue());

            Column annotation = type.getKey().getAnnotation(Column.class);

            sb.append(annotation.value()).append(" ").append(type.getValue().name());

            if (digitRange || decimalRange) {
                sb.append("(");

                if (digitRange) {
                    sb.append(String.valueOf(annotation.digitRange()));

                    if (decimalRange) {
                        sb.append(", ");
                    } else {
                        sb.append(")");
                    }
                }

                if (decimalRange) {
                    sb.append(String.valueOf(annotation.decimalRange())).append(")");
                }
            }

            if (annotation.value().equalsIgnoreCase(table.primaryKey())) {
                sb.append(" PRIMARY KEY");
            }

            if (uniqueKeys.contains(annotation.value())) {
                sb.append(" UNIQUE KEY");
            }

            if (iteration != types.size()) {
                sb.append(", ");
            }

            columnQuerySection.append(sb.toString());
        }

        VoidOperation operation = VoidOperation.create(this);
        operation.query(String.format("CREATE TABLE IF NOT EXISTS %s(%s)", table.value(), columnQuerySection.toString()));

        try {
            if (async) {
                operation.async();
            } else {
                operation.sync();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Drop a table.
     * @param tableName - table name.
     * @param async = if true it will run asynchronously, else synchronously.
     * @return true if successful, else false.
     */
    public boolean drop(String tableName, boolean async)
    {
        try {
            VoidOperation operation = VoidOperation.create(this).query(String.format("DROP TABLE %s", tableName));

            if (async) {
                operation.async();
            } else {
                operation.sync();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Creates update operation builder.
     * @param tableName - name of the t able you will be updating an entry of.
     * @return update operation builder.
     */
    public UpdateOperation<?> update(String tableName)
    {
        return new UpdateOperation<>(this, tableName);
    }

    /**
     * Creates insert operation builder.
     * @param instance - object to insert.
     * @return insert operation builder.
     */
    public <T> InsertOperation<T> insert(T instance)
    {
        return new InsertOperation<>(this, instance);
    }

    /**
     * To get a connection from the connection pool to use.
     * @return connection is source is not null, else null.
     */
    @SneakyThrows
    public synchronized Connection getOpenConnection()
    {
        if (source != null && !source.isClosed()) {
            return source.getConnection();
        } else {
            throw new DeveloperException("source is null.");
        }
    }

    /*
    Operation examples:
    Database database;
    Object instance;

    Update:
    database.update("tableName").set("value1", value).where("uuid", uuid).sync();
    database.update("tableName").set(instance);

    Insert:
    database.insert(instance).async();

    Delete:
    database.delete(instance).sync();
    database.delete().from("tableName").where("uuid", uuid).async();

    Select:
    database.select(TemplateClass.class).from("tableName").async();
    database.select(TemplateClass.class).from("tableName").where("uuid", uuid).and("block-breaks", 165)

    StatementFunction, ManipulationFunction, PostTransactionFunction
    database.query("SELECT * FROM table", (statement) -> {}, (result) -> {}, () -> {})
    /*
    TODO:
    - Update Operation
    - Insert Operation
    - Fetch Operation
    - Delete Operation
    - Raw Void Query Operation (takes the raw query and executes it)
    - Raw Data Query Operation (expect a result set)
     */
}
