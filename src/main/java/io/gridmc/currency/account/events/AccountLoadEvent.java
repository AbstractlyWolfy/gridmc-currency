package io.gridmc.currency.account.events;

import io.gridmc.currency.account.Account;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import java.util.UUID;

public class AccountLoadEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    // UUID
    private final UUID uuid;

    // Account
    private final Account account;

    public AccountLoadEvent(@Nonnull UUID uuid, @Nonnull Account account) {
        super(true);
        this.uuid = uuid;
        this.account = account;
    }

    /**
     * @return Unique id
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Get the account
     *
     * @return Account
     */
    public Account getAccount() {
        return account;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
