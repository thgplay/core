package com.thgplugins.domination.core;

import com.google.common.collect.Maps;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

public final class ScoreboardMap {
    private static final @NotNull ConcurrentMap<UUID, AbstractScoreboard> scoreboardsMap = Maps.newConcurrentMap();
    private static Class<? extends AbstractScoreboard> factoryClazz;

    public ScoreboardMap() {
    }

    public static @NotNull List<AbstractScoreboard> getScoreboards() {
        return new ArrayList<>(scoreboardsMap.values());
    }

    public static void addScoreboard(@NotNull Player player, @NotNull AbstractScoreboard scoreboard) {
        scoreboardsMap.put(player.getUniqueId(), scoreboard);
    }

    public static void removeScoreboard(@NotNull Player player) {
        scoreboardsMap.remove(player.getUniqueId());
    }

    public static @NotNull ConcurrentMap<UUID, AbstractScoreboard> getScoreboardsMap() {
        return scoreboardsMap;
    }

    public static @NotNull Class<? extends AbstractScoreboard> getFactoryClazz() {
        return factoryClazz;
    }

    public static void setFactoryClazz(@NotNull Class<? extends AbstractScoreboard> factoryClazz) {
        ScoreboardMap.factoryClazz = factoryClazz;
    }
}