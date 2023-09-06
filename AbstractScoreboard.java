package com.thgplugins.domination.core;

import lombok.var;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static com.thgplugins.domination.core.ItemCreate.colorize;

public abstract class AbstractScoreboard {

    protected @NotNull Player player;
    protected @NotNull Scoreboard scoreboard;
    protected @NotNull Objective objective;

    public AbstractScoreboard(@NotNull Player player, @NotNull String objective, @NotNull Component title) {
        this.player = player;
        this.scoreboard = player.getScoreboard();

        Optional.ofNullable(scoreboard.getObjective(objective))
                .ifPresent(Objective::unregister);

        this.objective = scoreboard.registerNewObjective(objective, "dummy", title);
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public abstract void create();

    public abstract void update();

    @Deprecated
    public void setDisplayName(@NotNull String displayName) {
        this.objective.displayName(Component.text(displayName));
    }

    public void setDisplayName(@NotNull Component displayName) {
        this.objective.displayName(displayName);
    }

    public void setRow(int index, @NotNull String text) {
        setRow(ScoreboardRow.byId(index), text);
    }

    public void setRow(@NotNull ScoreboardRow scoreboardRow, @NotNull String prefix) {
        setRow(scoreboardRow, prefix, "");
    }

    public void setRow(int index, @NotNull String prefix, @NotNull String suffix) {
        setRow(ScoreboardRow.byId(index), Component.text(prefix), Component.text(suffix));
    }

    public void setRow(@NotNull ScoreboardRow scoreboardRow, @NotNull Component prefix) {
        setRow(scoreboardRow, prefix, Component.text(""));
    }

    public void setRow(@NotNull ScoreboardRow scoreboardRow, @NotNull String prefix, @NotNull String suffix) {
        setRow(scoreboardRow, Component.text(colorize(prefix)), Component.text(colorize(suffix)));
    }

    public void setRow(int index, @NotNull String prefix, @NotNull Component suffix) {
        setRow(ScoreboardRow.byId(index), Component.text(colorize(prefix)), suffix);
    }

    public void setRow(int index, @NotNull Component prefix, @NotNull String suffix) {
        setRow(ScoreboardRow.byId(index), prefix, Component.text(colorize(suffix)));
    }

    public void setRow(@NotNull ScoreboardRow scoreboardRow, @NotNull Component prefix, @NotNull Component suffix) {
        String entry = scoreboardRow.getEntry();

        Team scoreboardTeam = scoreboard.getTeam(entry);
        if (Objects.isNull(scoreboardTeam)) {
            scoreboardTeam = scoreboard.registerNewTeam(entry);
        }
        if (!scoreboardTeam.prefix().equals(prefix))
            scoreboardTeam.prefix(prefix);

        if (!scoreboardTeam.suffix().equals(suffix))
            scoreboardTeam.suffix(suffix);

        if (!scoreboardTeam.hasEntry(entry))
            scoreboardTeam.addEntry(entry);

        Score score = objective.getScore(entry);

        if (score.getScore() != scoreboardRow.getScore())
            score.setScore(scoreboardRow.getScore());
    }

    public void clearLines() {
        for (ScoreboardRow row : ScoreboardRow.values()) {
            scoreboard.resetScores(row.getEntry());

            Team scoreboardTeam = scoreboard.getTeam(row.getEntry());
            if (Objects.nonNull(scoreboardTeam)) {
                scoreboardTeam.unregister();
            }
        }
    }

    public void clearLine(int line) {
        for (ScoreboardRow row : ScoreboardRow.values()) {
            if (row.getScore() == line) {
                scoreboard.resetScores(row.getEntry());

                Team scoreboardTeam = scoreboard.getTeam(row.getEntry());
                if (Objects.nonNull(scoreboardTeam)) {
                    scoreboardTeam.unregister();
                }
                break;
            }
        }
    }

    public void removeLine(int... lines) {
        Arrays.stream(lines).forEach(line -> {
            var row = ScoreboardRow.byId(line);
            this.scoreboard.resetScores(row.getEntry());
            Team scoreboardTeam = this.scoreboard.getTeam(row.getEntry());
            if (Objects.nonNull(scoreboardTeam)) {
                scoreboardTeam.unregister();
            }
        });
    }

    public boolean containsLine(int line) {
        var row = ScoreboardRow.byId(line);
        return this.scoreboard.getTeam(row.getEntry()) != null;
    }


    public enum ScoreboardRow {

        ROW_1,
        ROW_2,
        ROW_3,
        ROW_4,
        ROW_5,
        ROW_6,
        ROW_7,
        ROW_8,
        ROW_9,
        ROW_10,
        ROW_11,
        ROW_12,
        ROW_13,
        ROW_14,
        ROW_15,
        ROW_16;

        public int getScore() {
            return values().length - ordinal();
        }

        public @NotNull String getEntry() {
            return ChatColor.values()[ordinal()] + ChatColor.RESET.toString();
        }

        public static @NotNull ScoreboardRow byId(int index) {
            return values()[index];
        }
    }


}
