package io.gridmc.currency.config.impl;

import io.gridmc.currency.config.Config;

@Config(name = "database")
public final class DatabaseConfig {

    // Host
    private final String host;
    // Port
    private final int port;

    // Database name.
    private final String database;

    // Username.
    private final String username;
    // Password.
    private final String password;

    /**
     * Create a new {@link DatabaseConfig}.
     * @param host - host.
     * @param port - port.
     * @param database - database.
     * @param username - username.
     * @param password - password.
     */
    public DatabaseConfig(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    /**
     * @return Get host.
     */
    public String getHost() {
        return host;
    }

    /**
     * @return Get port.
     */
    public int getPort() {
        return port;
    }

    /**
     * @return Get database.
     */
    public String getDatabase() {
        return database;
    }

    /**
     * @return Get username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return Get password.
     */
    public String getPassword() {
        return password;
    }
}
