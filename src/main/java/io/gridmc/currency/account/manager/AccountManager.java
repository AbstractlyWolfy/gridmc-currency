package io.gridmc.currency.account.manager;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import io.gridmc.currency.GridCurrencyPlugin;
import io.gridmc.currency.account.Account;
import io.gridmc.currency.account.cache.AccountCache;
import io.gridmc.currency.account.cache.AccountRemovalListener;
import io.gridmc.currency.account.dao.AccountDAO;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class AccountManager {

    // Cache all accounts for an 2 hours if not accessed it will remove
    private static final LoadingCache<UUID, Account> ACCOUNTS_CACHE = CacheBuilder.newBuilder()
            .expireAfterAccess(2, TimeUnit.HOURS)
            .removalListener(new AccountRemovalListener())
            .build(new AccountCache());

    /**
     * Get account by UUID
     *
     * @param uuid - uuid
     * @return Account
     */
    public static Account get(@Nonnull UUID uuid) {

        try {
            return ACCOUNTS_CACHE.get(uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Invalidate an account
     * @param uuid - uuid
     */
    public static void remove(@Nonnull UUID uuid) {
        ACCOUNTS_CACHE.invalidate(uuid);
    }

    /**
     * Save account
     * @param uuid - uuid
     */
    public static void save(@Nonnull UUID uuid, @Nonnull Account account) {
        try (Connection connection = GridCurrencyPlugin.getDatabase().getConnection()) {
            Preconditions.checkNotNull(connection);

            Player online = Bukkit.getPlayer(uuid);

            // Save main account
            if (online != null)
                AccountDAO.save(uuid, online.getName());
            else
                AccountDAO.save(uuid);

            // Save all SubAccounts
            account.invalidateSubAccountsAccounts(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
