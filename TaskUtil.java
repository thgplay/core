package com.thgplugins.guild.core;

import com.thgplugins.guild.GuildPlugin;
import lombok.var;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public final class TaskUtil {

    private TaskUtil() {
    }

    public static void runTask(Runnable runnable) {
        Bukkit.getScheduler().runTask(GuildPlugin.getInstance(), runnable);
    }

    public static void runTask(long delay, Runnable runnable) {
        Bukkit.getScheduler().runTaskLater(GuildPlugin.getInstance(), runnable, delay);
    }

    public static BukkitTask runTaskTimer(long delay, long interval, Runnable runnable) {
        return Bukkit.getScheduler().runTaskTimer(GuildPlugin.getInstance(), runnable, delay, interval);
    }

    public static void runAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(GuildPlugin.getInstance(), runnable);
    }

    public static void runAsync(long delay, Runnable runnable) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(GuildPlugin.getInstance(), runnable, delay);
    }

    public static BukkitTask runAsyncTimer(long delay, long interval, Runnable runnable) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(GuildPlugin.getInstance(), runnable, delay, interval);
    }

    public static void runSyncSafe(Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            runnable.run();
        } else {
            TaskUtil.runTask(runnable);
        }
    }

    public static <T> CompletableFuture<T> runSyncSafe(Supplier<T> supplier) {
        if (Bukkit.isPrimaryThread()) {
            try {
                var value = supplier.get();
                return CompletableFuture.completedFuture(value);
            } catch (Exception e) {
                return CompletableFuture.completedFuture(null);
            }
        } else {
            var future = new CompletableFuture<T>();
            TaskUtil.runTask(() -> {
                try {
                    var value = supplier.get();
                    future.complete(value);
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            });

            return future;
        }
    }

}
