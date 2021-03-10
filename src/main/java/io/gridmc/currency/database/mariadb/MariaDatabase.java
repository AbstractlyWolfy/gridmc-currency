package io.gridmc.currency.database.mariadb;

import com.google.common.base.Preconditions;
import com.zaxxer.hikari.HikariDataSource;
import io.gridmc.currency.config.ConfigManager;
import io.gridmc.currency.config.impl.DatabaseConfig;
import io.gridmc.currency.database.Database;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.logging.Level;

public final class MariaDatabase implements Database<HikariDataSource> {

    // Credentials config.
    private final DatabaseConfig config;

    // Connection pool
    private HikariDataSource hikariConnectionPool;

    /**
     * Create a new instance of {@link MariaDatabase}
     */
    public MariaDatabase() {

        // Register the initial database config.
        config = ConfigManager.loadConfigFile(DatabaseConfig.class, defaults -> new DatabaseConfig(
                "127.0.0.1",
                3306,
                "gridmc",
                "root",
                ""
        ));
    }

    @Override
    public void connect() {

        hikariConnectionPool = new HikariDataSource();

        hikariConnectionPool.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");

        hikariConnectionPool.addDataSourceProperty("serverName", config.getHost());
        hikariConnectionPool.addDataSourceProperty("port", config.getPort());
        hikariConnectionPool.addDataSourceProperty("databaseName", config.getDatabase());
        hikariConnectionPool.addDataSourceProperty("user", config.getUsername());
        hikariConnectionPool.addDataSourceProperty("password", config.getPassword());

        hikariConnectionPool.setMaximumPoolSize(25);

        hikariConnectionPool.setConnectionTimeout(3000);
        hikariConnectionPool.setValidationTimeout(1000);
    }

    @Override
    public void disconnect() {

        if (hikariConnectionPool != null && hikariConnectionPool.isClosed()) {
            hikariConnectionPool.close();
        }
    }

    /**
     * Create tables from a schema file.
     *
     * @param clazz - class
     * @param fileName - file name
     */
    public void createTables(Class<?> clazz, String fileName) {
        try (Scanner scanner = new Scanner(clazz.getResourceAsStream("/" + fileName + ".sql"))) {
            scanner.useDelimiter("(;(\r)?\n)|(--\n)");

            try (Connection connection = getDatabase().getConnection()) {
                Preconditions.checkNotNull(connection);

                Statement statement = connection.createStatement();

                while (scanner.hasNext()) {
                    String line = scanner.next();
                    if (line.startsWith("/*!") && line.endsWith("*/")) {
                        int index = line.indexOf(' ');
                        line = line.substring(index + 1, line.length() - " */".length());
                    }

                    if (line.trim().length() > 0) {
                        statement.execute(line);
                    }
                }

                Bukkit.getLogger().log(Level.INFO, "MariaDB Table creation was successful.");
            } catch (SQLException e) {
                Bukkit.getLogger().log(Level.SEVERE, "MariaDB Table creation has failed.");
                e.printStackTrace();
            }
        }
    }

    /**
     * @return Get the {@link Connection} instance
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException { return hikariConnectionPool.getConnection(); }

    @Override
    public HikariDataSource getDatabase() {
        return hikariConnectionPool;
    }
}
