package io.pucman.common.data.service;

import java.util.concurrent.Callable;

/**
 * Template class for integrating data services into plugins. Although this seems useless, this allows the
 * developer to use this abstracted client as a 'plug'n'use' data service client. For example, instead of
 * using HikariCP (SQL Client) and integrating that into the project, you integrate this abstracted data
 * service client and then inherit these data service classes to make a sql service using HikariCP.
 *
 * Not only does this have the added functionality of easily executing statements asynchronously, it will
 * also allow the developer to swap the plugins data service onto a different data service without having
 * to rewrite anything in the plugin.
 * @param <C> - specifying the class configuration this service will use that inherits service configuration.
 */
public interface Service<C extends ServiceConfig>
{
    int connect(C serviceConfig);

    int release();

    <T> T execute(Callable<T> task);

    void execute(Runnable task);
}
