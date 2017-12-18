package io.pucman.common.sql.client;

import com.google.common.util.concurrent.ListenableScheduledFuture;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.pucman.common.exception.DeveloperException;
import lombok.Getter;
import lombok.SneakyThrows;

import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.Connection;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Getter
@ParametersAreNonnullByDefault
public class Client
{
    private ListeningScheduledExecutorService service;
    private HikariDataSource source;
    

    public Client(String username, String password, String database, String address, int port, int threadPoolSize)
    {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mariadb://" + address + ":" + port + "/" + database);
        config.setUsername(username);
        config.setPassword(password);

        this.service = threadPoolSize != 0 ? MoreExecutors.listeningDecorator(Executors.newScheduledThreadPool(threadPoolSize)) : MoreExecutors.listeningDecorator(Executors.newSingleThreadScheduledExecutor());
        this.source = new HikariDataSource(config);
    }

    public Client(String username, String password, String database, String address, int port)
    {
        this(username, password, database, address, port, 0);
    }

    public Client(HikariConfig config)
    {
        this.source = new HikariDataSource(config);
    }

    public <V> Future<V> post(Callable<V> task) throws Exception
    {
        return service.submit(task);
    }

    public void post(Runnable task) throws Exception
    {
        service.submit(task);
    }

    public <T> ListenableScheduledFuture<T> schedule(Callable<T> task, long time, TimeUnit unit) throws Exception
    {
        return service.schedule(task, time, unit);
    }

    public ListenableScheduledFuture<?> scheduledAtFixedRate(Runnable task, long time, long interval, TimeUnit unit) throws Exception
    {
        return service.scheduleAtFixedRate(task, time, interval, unit);
    }

    public ListenableScheduledFuture<?> scheduledWithFixedDelay(Runnable task, long time, long interval, TimeUnit unit) throws Exception
    {
        return service.scheduleAtFixedRate(task, time, interval, unit);
    }

    @SneakyThrows
    public Connection getOpenConnection()
    {
        if (source == null) {
            throw new DeveloperException("source is null.");
        }

        return source.getConnection();
    }
}
