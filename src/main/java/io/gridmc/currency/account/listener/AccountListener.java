package io.gridmc.currency.account.listener;

import com.google.common.base.Preconditions;
import io.gridmc.currency.GridCurrencyPlugin;
import io.gridmc.currency.account.Account;
import io.gridmc.currency.account.manager.AccountManager;
import io.gridmc.currency.account.events.AccountLoadEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class AccountListener implements Listener {

    @EventHandler
    public void onPreLoginEvent(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED)
            return;

        UUID uuid = event.getUniqueId();
        Account account = AccountManager.get(event.getUniqueId());

        if (account == null) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.DARK_RED + "Failed to load profile, please try again..");
            throw new NullPointerException("Account (" + event.getName() + ") is null");
        }

        try (Connection connection = GridCurrencyPlugin.getDatabase().getConnection()) {
            Preconditions.checkNotNull(connection);

            // Call AccountLoadEvent.
            AccountLoadEvent accountLoadEvent = new AccountLoadEvent(uuid, account);
            Bukkit.getPluginManager().callEvent(accountLoadEvent);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Removing will automatically save all sub accounts and the main account
        AccountManager.remove(event.getPlayer().getUniqueId());
    }
}
