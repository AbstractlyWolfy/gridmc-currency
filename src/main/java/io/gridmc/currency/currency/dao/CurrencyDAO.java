package io.gridmc.currency.currency.dao;

import io.gridmc.currency.GridCurrencyPlugin;
import io.gridmc.currency.account.Account;
import io.gridmc.currency.account.manager.AccountManager;
import org.bukkit.Bukkit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CurrencyDAO {

    /**
     * Get amount of currency a player has by uuid
     * @param connection - connection
     * @param  uuid = uuid
     * @return Currency
     */
    @Nullable
    public static double getCurrency(@Nonnull Connection connection, @Nonnull UUID uuid) {

        Account account = AccountManager.get(uuid);

        if (account == null) {
            return 0;
        }

        try (PreparedStatement statement = connection.prepareStatement("SELECT `amount` FROM `currency_account` WHERE `account_id`=?;")) {
            statement.setInt(1, account.getUniqueId());
            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return result.getDouble("amount");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Save current currency amount by players uuid
     * @param connection - connection
     * @param uuid - uuid
     */
    public static void saveCurrency(@Nonnull Connection connection, @Nonnull UUID uuid, double amount) {
        Account account = AccountManager.get(uuid);

        if (account == null) {
            return;
        }

        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO `currency_account` (`account_id`, `amount`) VALUES (?, ?) ON DUPLICATE KEY UPDATE `amount`=?;")) {
            statement.setInt(1, account.getUniqueId());
            statement.setDouble(2, amount);
            statement.setDouble(3, amount);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set currency amount by account id
     *
     * @param accountId - account id
     * @param amount - amount
     */
    public static void setCurrency(@Nonnull int accountId, double amount) {

        Bukkit.getScheduler().runTaskAsynchronously(GridCurrencyPlugin.getPlugin(), () -> {

            try (Connection connection = GridCurrencyPlugin.getDatabase().getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement("INSERT INTO `currency_account` (`account_id`, `amount`) VALUES (?, ?) ON DUPLICATE KEY UPDATE `amount`=?;")) {
                    statement.setInt(1, accountId);
                    statement.setDouble(2, amount);
                    statement.setDouble(3, amount);
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Get amount of currency based on account id.
     *
     * @param accountId - account id
     * @return currency
     */
    public static double getCurrency(@Nonnull int accountId) {

        try (Connection connection = GridCurrencyPlugin.getDatabase().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT `amount` FROM `currency_account` WHERE `account_id`=?;")) {
                statement.setInt(1, accountId);
                try (ResultSet result = statement.executeQuery()) {

                    if (result.next()) {
                        return result.getDouble("amount");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
