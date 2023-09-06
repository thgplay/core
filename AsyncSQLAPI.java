package com.thgplugins.guild.core;

import com.sun.istack.internal.NotNull;
import com.thgplugins.guild.GuildPlugin;
import com.thgplugins.guild.util.GuildConstants;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.var;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class AsyncSQLAPI {

    private GuildPlugin plugin;

    private final HikariDataSource dataSource;

    public AsyncSQLAPI(GuildPlugin plugin) throws SQLException {
        this.plugin = plugin;
        dataSource = createSQLPool(GuildConstants.DATABASE, 10);
        if (dataSource.isRunning()) {
            Stream.of("Successfully established database connection with the following details:",
                    "JDBC URL: " + dataSource.getJdbcUrl(),
                    "Maximum Pool Size: " + dataSource.getMaximumPoolSize(),
                    "Username: " + dataSource.getUsername()).forEach(plugin.getLogger()::info);
        }
    }


    public CompletableFuture<Void> executeUpdate(String sql, Object... parameters) {
        sendLog("QUERY: " + sql);
        return CompletableFuture.runAsync(() -> {
            try (var connection = dataSource.getConnection()) {
                if (connection.isValid(2)) {
                    try (var statement = connection.prepareStatement(sql)) {
                        for (int i = 0; i < parameters.length; i++) {
                            statement.setObject(i + 1, parameters[i]);
                        }
                        statement.executeUpdate();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public CompletableFuture<Void> executeQuery(String sql, Consumer<ResultSet> resultSetProcessor, Object... parameters) {
        sendLog("QUERY: " + sql);
        return CompletableFuture.runAsync(() -> {
            try (var connection = dataSource.getConnection()) {
                if (connection.isValid(2)) {
                    try (var statement = connection.prepareStatement(sql)) {
                        for (int i = 0; i < parameters.length; i++) {
                            statement.setObject(i + 1, parameters[i]);
                        }
                        try (var resultSet = statement.executeQuery()) {
                            resultSetProcessor.accept(resultSet);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void sendLog(String s) {
        plugin.getLogger().info(s);
    }

    private HikariDataSource createSQLPool(@NotNull String database, int maximumPoolSize) {
        var config = plugin.getConfig();

        var mysqlHost = config.getString("database.host", "localhost");
        var mysqlPort = config.getInt("database.port", 3306);
        var mysqlUser = config.getString("database.user", "root");
        var mysqlPassword = config.getString("database.password", "");

        long leakDetectionThreshold = config.getLong("database.leak-detection-threshold-ms", 0L);

        var hikariConfig = this.createDefaultConfig(mysqlHost, mysqlPort, mysqlUser, mysqlPassword,
                database, leakDetectionThreshold);
        hikariConfig.setMaximumPoolSize(maximumPoolSize);

        return new HikariDataSource(hikariConfig);
    }

    @NotNull
    private HikariConfig createDefaultConfig(@NotNull String host, int port, @NotNull String user, @NotNull String password, @NotNull String database, long leakDetectionThreshold) {
        var config = new HikariConfig();

        config.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s", host, port, database));
        config.setUsername(user);
        config.setPassword(password);

        config.setConnectionTimeout(10000);

        if (leakDetectionThreshold > 0) {
            config.setLeakDetectionThreshold(leakDetectionThreshold);
        }

        return config;
    }

    public CompletableFuture<Long> insertDatabase(String sql, Object... parameters) {
        sendLog("QUERY: " + sql);
        return CompletableFuture.supplyAsync(() -> {
            try (var connection = dataSource.getConnection();
                 var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < parameters.length; i++) {
                    statement.setObject(i + 1, parameters[i]);
                }
                statement.executeUpdate();

                try (var generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getLong(1);
                    } else {
                        throw new SQLException("Creating item failed, no ID obtained.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        });
    }




    public void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
