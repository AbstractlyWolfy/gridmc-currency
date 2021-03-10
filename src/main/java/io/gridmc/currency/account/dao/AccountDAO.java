package io.gridmc.currency.account.dao;

import io.gridmc.currency.GridCurrencyPlugin;
import io.gridmc.currency.account.Account;
import io.gridmc.currency.utils.Callback;
import io.gridmc.currency.utils.UUIDUtil;
import org.bukkit.Bukkit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AccountDAO {

    /**
     * Get an account by a players uuid
     * @param connection - connection
     * @param uuid - uuid
     * @return Account
     */
    @Nullable
    public static Account get(@Nonnull Connection connection, @Nonnull UUID uuid) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT `id` FROM `account` WHERE `uuid`=UNHEX(?);")) {
            statement.setString(1, UUIDUtil.strip(uuid));
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    save(uuid);
                    return get(connection, uuid);
                }

                return new Account(result.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Save an account by players uuid
     * @param uuid - uuid
     */
    public static void save(@Nonnull UUID uuid) {

        try (Connection connection = GridCurrencyPlugin.getDatabase().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT IGNORE INTO `account` (`uuid`) VALUES (UNHEX(?))")) {
                statement.setString(1, UUIDUtil.strip(uuid));
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save an account by players uuid and username
     * @param uuid - uuid
     * @param username - username.
     */
    public static void save(@Nonnull UUID uuid, String username) {

        try (Connection connection = GridCurrencyPlugin.getDatabase().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO `account` (`uuid`, `name`) VALUES (UNHEX(?), ?) ON DUPLICATE KEY UPDATE `name`=?;")) {
                statement.setString(1, UUIDUtil.strip(uuid));
                statement.setString(2, username);
                statement.setString(3, username);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get an account id by a username
     *
     * @param username - username
     * @param callback - callback
     */
    public static void fetchAccountId(@Nonnull String username, Callback<Integer> callback) {

        Bukkit.getScheduler().runTaskAsynchronously(GridCurrencyPlugin.getPlugin(), () -> {

            try (Connection connection = GridCurrencyPlugin.getDatabase().getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement("SELECT `id` FROM `account` WHERE `name`=LOWER(?);")) {
                    statement.setString(1, username.toLowerCase());
                    try (ResultSet result = statement.executeQuery()) {

                        if (result.next()) {
                            callback.call(result.getInt("id"));
                            return;
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            callback.call(-1);
        });
    }
}
