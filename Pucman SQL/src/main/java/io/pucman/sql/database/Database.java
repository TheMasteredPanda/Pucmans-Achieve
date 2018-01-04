package io.pucman.sql.database;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.pucman.common.exception.DeveloperException;
import lombok.Getter;
import lombok.SneakyThrows;

import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.Connection;
import java.util.concurrent.Executors;

@ParametersAreNonnullByDefault
public class Database
{
    private HikariDataSource source;

    @Getter
    private ListeningExecutorService service;

    public Database(HikariConfig config, int threadPoolSize)
    {
        service = threadPoolSize >= 1 ? MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(threadPoolSize)) : MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
        source = new HikariDataSource(config);
    }


    public void update(String tableName)
    {

    }

    public void delete(Object instance)
    {

    }

    public void deleteFrom(String tableName)
    {
    }

    public void insert(Object instance)
    {

    }

    public void select(Class templateClazz)
    {

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
    database.insert(instance).into("tableName").async();

    Delete:
    database.delete(instance).from("tableName").sync();
    database.deleteFrom("tableName").where("uuid", uuid).async();

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
