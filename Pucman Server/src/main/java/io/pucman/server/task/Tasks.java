package io.pucman.server.task;

import io.pucman.common.exception.UtilException;
import io.pucman.server.PLibrary;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Utility class for easily scheduling and execution of tasks.
 */
public final class Tasks
{
    private static PLibrary lib = PLibrary.get();
    private static final BukkitScheduler scheduler = Bukkit.getScheduler();

    private Tasks()
    {
        throw new UtilException();
    }

    public static int runSync(Runnable runnable)
    {
        return Tasks.scheduler.runTask(lib, runnable).getTaskId();
    }

    public static int runSyncLater(Runnable runnable, long ticks)
    {
        return Tasks.scheduler.runTaskLater(lib, runnable, ticks).getTaskId();
    }

    public static int runSyncRepeating(Runnable runnable, long tickRepeat)
    {
        return Tasks.runSyncLaterRepeating(runnable, 0, tickRepeat);
    }

    public static int runSyncLaterRepeating(Runnable runnable, long later, long tickRepeat)
    {
        return Tasks.scheduler.scheduleSyncRepeatingTask(lib, runnable, later, tickRepeat);
    }

    public static int runAsync(Runnable runnable)
    {
        return Tasks.scheduler.runTaskAsynchronously(lib, runnable).getTaskId();
    }

    public static int runAsyncLater(Runnable runnable, long ticks)
    {
        return Tasks.runSyncLater(() -> Tasks.runAsync(runnable), ticks);
    }

    public static int runAsyncRepeating(Runnable runnable, long tickRepeat)
    {
        return Tasks.runAsyncLaterRepeating(runnable, 0, tickRepeat);
    }

    public static int runAsyncLaterRepeating(Runnable runnable, long later, long tickRepeat)
    {
        return Tasks.runSyncLaterRepeating(() -> Tasks.runAsync(runnable), later, tickRepeat);
    }

    public static int runLaterRepeating(Runnable runnable, boolean sync, long later, long tickRepeat)
    {
        return sync ? Tasks.runSyncLaterRepeating(runnable, later, tickRepeat) : Tasks.runAsyncLaterRepeating(runnable, later, tickRepeat);
    }

    public static void cancel(int id) {
        Tasks.scheduler.cancelTask(id);
    }
}
