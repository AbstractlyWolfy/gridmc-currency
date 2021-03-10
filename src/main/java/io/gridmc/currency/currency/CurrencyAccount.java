package io.gridmc.currency.currency;

import io.gridmc.currency.account.SubAccount;
import io.gridmc.currency.currency.dao.CurrencyDAO;

import java.sql.Connection;
import java.util.UUID;

public class CurrencyAccount implements SubAccount {

    // uuid
    private final UUID uuid;

    // Current amount of currency
    private double amount;

    public CurrencyAccount(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void initialise(Connection connection) {
        // Called when a profile is loaded
        amount = CurrencyDAO.getCurrency(connection, uuid);
    }

    @Override
    public void invalidate(Connection connection) {
        // Called when a profile is saved
        CurrencyDAO.saveCurrency(connection, uuid, amount);
    }

    /**
     * @return Currency amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Set the currency amount
     *
     * @param amount - amount
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }
}
