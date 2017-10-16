package io.pucman.server.plugin;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

/**
 * Just for plugins that require general multithreading capabilities.
 */
public class MultiThreadedLibPlugin extends LibPlugin
{
    private ListeningExecutorService service;

    public MultiThreadedLibPlugin(int max)
    {
        this.service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(max));
    }

    public <V> ListenableFuture<V> execute(Callable<V> task)
    {
        return this.service.submit(task);
    }

    public <V> ListenableFuture<V> execute(Runnable task, V type)
    {
        return this.service.submit(task, type);
    }

    public ListenableFuture<?> execute(Runnable task)
    {
        return this.service.submit(task);
    }

    public void close()
    {
        this.service.shutdown();
    }
}
