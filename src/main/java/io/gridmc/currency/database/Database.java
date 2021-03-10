package io.gridmc.currency.database;

public interface Database<T> {

    /**
     * Connect to the database
     */
    void connect();

    /**
     * Disconnect from the database
     */
    void disconnect();

    /**
     * @return Get the database instance
     */
    T getDatabase();
}
