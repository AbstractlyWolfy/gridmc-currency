package io.gridmc.currency.currency.listener;

import io.gridmc.currency.account.Account;
import io.gridmc.currency.account.events.AccountLoadEvent;
import io.gridmc.currency.currency.CurrencyAccount;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CurrencyListener implements Listener {

    @EventHandler
    public void onAccountLoad(AccountLoadEvent event) {
        Account account = event.getAccount();
        account.loadSubAccount(new CurrencyAccount(event.getUuid()));
    }
}
