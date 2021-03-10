package io.gridmc.currency;

import io.gridmc.currency.account.Account;
import io.gridmc.currency.account.events.AccountLoadEvent;
import io.gridmc.currency.account.manager.AccountManager;
import io.gridmc.currency.account.listener.AccountListener;
import io.gridmc.currency.currency.command.CurrencyBalanceCommand;
import io.gridmc.currency.currency.command.CurrencyCommand;
import io.gridmc.currency.currency.listener.CurrencyListener;
import io.gridmc.currency.database.mariadb.MariaDatabase;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class GridCurrencyPlugin extends JavaPlugin {

    private static JavaPlugin plugin;

    private static MariaDatabase database;

    @Override
    public void onEnable() {
        super.onEnable();

        plugin = this;

        // Donnect to the database
        database = new MariaDatabase();
        database.connect();

        // Create appropriate tables
        database.createTables(GridCurrencyPlugin.class, "schema");

        Bukkit.getOnlinePlayers().forEach(online -> {
            Account account = AccountManager.get(online.getUniqueId());

            if (account == null)
                return;

            // Call AccountLoadEvent.
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                AccountLoadEvent accountLoadEvent = new AccountLoadEvent(online.getUniqueId(), account);
                Bukkit.getPluginManager().callEvent(accountLoadEvent);
            });
        });

        // Register listeners
        Bukkit.getPluginManager().registerEvents(new AccountListener(), this);
        Bukkit.getPluginManager().registerEvents(new CurrencyListener(), this);

        // Register commands
        new CurrencyCommand();
        new CurrencyBalanceCommand();
    }

    @Override
    public void onDisable() {

        Bukkit.getOnlinePlayers().forEach(online -> AccountManager.remove(online.getUniqueId()));

        if (database != null)
            database.disconnect();

        super.onDisable();
    }

    /**
     * @return Get the {@link MariaDatabase} instance
     */
    public static MariaDatabase getDatabase() {
        return database;
    }

    /**
     * @return JavaPlugin
     */
    public static JavaPlugin getPlugin() {
        return plugin;
    }
}
