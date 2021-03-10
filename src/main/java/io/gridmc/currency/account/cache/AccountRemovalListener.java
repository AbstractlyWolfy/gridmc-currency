package io.gridmc.currency.account.cache;

import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import io.gridmc.currency.account.Account;
import io.gridmc.currency.account.manager.AccountManager;
import org.bukkit.Bukkit;

import java.util.UUID;
import java.util.logging.Level;

public class AccountRemovalListener implements RemovalListener<UUID, Account> {

    @Override
    public void onRemoval(RemovalNotification<UUID, Account> notification) {
        Account account = notification.getValue();

        if (account == null) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not find an account to save!");
            return;
        }

        // Runs the invalidate code (saving the account)
        AccountManager.save(notification.getKey(), notification.getValue());
    }
}
