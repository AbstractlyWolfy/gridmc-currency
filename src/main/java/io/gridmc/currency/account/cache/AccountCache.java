package io.gridmc.currency.account.cache;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheLoader;
import io.gridmc.currency.GridCurrencyPlugin;
import io.gridmc.currency.account.Account;
import io.gridmc.currency.account.dao.AccountDAO;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public final class AccountCache extends CacheLoader<UUID, Account> {

    @Override
    public Account load(@Nonnull UUID uuid) {
        try (Connection connection = GridCurrencyPlugin.getDatabase().getConnection()) {
            Preconditions.checkNotNull(connection);

            Account account = AccountDAO.get(connection, uuid);

            Preconditions.checkNotNull(account);

            return account;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
