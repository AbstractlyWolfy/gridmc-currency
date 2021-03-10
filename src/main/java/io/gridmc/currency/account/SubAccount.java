package io.gridmc.currency.account;

import io.gridmc.currency.utils.Callback;

import java.sql.Connection;

public interface SubAccount {

    /**
     * Initialise the {@link SubAccount} for the user
     * @param connection - connection
     */
    void initialise(Connection connection);

    /**
     * Called when you remove an account from a user, such as on leave
     */
    void invalidate(Connection connection);
}
