package io.gridmc.currency.account;

import com.google.common.base.Preconditions;
import io.gridmc.currency.GridCurrencyPlugin;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

public final class Account {

    // Database id
    private final int uniqueId;

    // Username
    private String username;

    // All sub accounts
    private final HashMap<Class<? extends SubAccount>, SubAccount> subAccountMap;

    public Account(int uniqueId) {
        this.uniqueId = uniqueId;
        this.subAccountMap = new HashMap<>();
    }

    public Account(int uniqueId, String username) {
        this.uniqueId = uniqueId;
        this.username = username;
        this.subAccountMap = new HashMap<>();
    }

    /**
     * @return Get players unique id in the database
     */
    public int getUniqueId() {
        return uniqueId;
    }

    /**
     * @return Get username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Load a {@link SubAccount} into memory
     * @param account - account
     */
    public <T extends SubAccount> void loadSubAccount(T account) {
        if (subAccountMap.containsKey(account.getClass()))
            return;

        try (Connection connection = GridCurrencyPlugin.getDatabase().getConnection()) {
            Preconditions.checkNotNull(connection);
            account.initialise(connection);
            subAccountMap.put(account.getClass(), account);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a {@link SubAccount} for this user
     * @param clazz - clazz
     * @return SubAccount
     */
    public <T extends SubAccount> T getSubAccount(Class<?> clazz) {
        return (T) subAccountMap.get(clazz);
    }

    /**
     * Invalidate all sub accounts
     */
    public void invalidateSubAccountsAccounts(@Nonnull Connection connection) {
        subAccountMap.values().forEach(subAccount -> subAccount.invalidate(connection));
        subAccountMap.clear();
    }

    public HashMap<Class<? extends SubAccount>, SubAccount> getSubAccountMap() {
        return subAccountMap;
    }
}
