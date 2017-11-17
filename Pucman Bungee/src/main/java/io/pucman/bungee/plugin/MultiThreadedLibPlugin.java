package io.pucman.bungee.plugin;

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

    /**
     * Submit a callable.
     * @param task - callable.
     * @param <V> - generic type.
     * @return listenable future.
     */
    public <V> ListenableFuture<V> submit(Callable<V> task)
    {
        return this.service.submit(task);
    }

    /**
     * Submitting a runnable.
     * @param task - the runnable.
     * @return listenable future.
     */
    public void submit(Runnable task)
    {
        this.service.submit(task);
    }

    public void close()
    {
        this.service.shutdown();
    }
}
