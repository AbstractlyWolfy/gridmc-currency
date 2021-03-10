package io.gridmc.currency.currency.command;

import io.gridmc.currency.account.Account;
import io.gridmc.currency.account.manager.AccountManager;
import io.gridmc.currency.commands.Command;
import io.gridmc.currency.currency.CurrencyAccount;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CurrencyBalanceCommand extends Command {

    public CurrencyBalanceCommand() {
        super("balance", new String[] { "bal" });
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.YELLOW + "Only players have credit balances silly..");
            return;
        }

        Player senderPlayer = (Player) sender;

        // Self balance
        Account account = AccountManager.get(senderPlayer.getUniqueId());

        if (account == null) {
            sender.sendMessage(ChatColor.RED + "There was an error finding your credit balance..");
            return;
        }

        CurrencyAccount currencyAccount = account.getSubAccount(CurrencyAccount.class);

        if (currencyAccount == null) {
            sender.sendMessage(ChatColor.RED + "Could not find your currency account!");
            return;
        }

        sender.sendMessage(ChatColor.GREEN + "Your balance is " + currencyAccount.getAmount() + " credits");
    }
}
