package io.pucman.sql.operation;

import com.google.common.util.concurrent.ListeningExecutorService;
import io.pucman.sql.database.Database;
import lombok.Getter;

import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Interface that all operations will be implementing.
 * @param <T> - return value type.
 */
@Getter
@ParametersAreNonnullByDefault
public abstract class DatabaseStatement<T>
{
    private Connection connection;
    private ListeningExecutorService service;


    public DatabaseStatement(Database database)
    {
        this.connection = database.getOpenConnection();
        this.service = database.getService();
    }


    /**
     * Invoked to construct the sql statement.
     */
    protected abstract PreparedStatement construct();

    /**
     * Used to invoke the statement asynchronously. You can either execute it in a non-blocking or blocking
     * way.
     * @return return value.
     */
    public abstract T async();

    /**
     * Used to invoke the statement synchronously.
     * @return return value.
     */
    public abstract T sync();
}
