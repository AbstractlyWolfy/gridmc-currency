package io.gridmc.currency.currency.command;

import com.google.common.base.Preconditions;
import io.gridmc.currency.GridCurrencyPlugin;
import io.gridmc.currency.account.Account;
import io.gridmc.currency.account.dao.AccountDAO;
import io.gridmc.currency.account.manager.AccountManager;
import io.gridmc.currency.commands.Command;
import io.gridmc.currency.currency.CurrencyAccount;
import io.gridmc.currency.currency.dao.CurrencyDAO;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.SQLException;

public class CurrencyCommand extends Command {

    public CurrencyCommand() {
        super("currency");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        // /currency [add|remove|get] [player] <amount>
        if (args.length < 1) {
            // Help
            sender.sendMessage(ChatColor.YELLOW + "/currency [add | remove | get] [player] <amount>");
            return;
        }

        String action = args[0].toLowerCase();

        if (args.length == 1) {

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

            sender.sendMessage(ChatColor.GREEN + "Your credit balance is " + currencyAccount.getAmount());
            return;
        }

        if (!sender.hasPermission("gridmc.currency.admin")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use currency commands!");
            return;
        }

        if (args.length == 2) {

            // Balance
            String username = args[1];
            Player player = Bukkit.getPlayer(username);

            if (player == null) {

                // Offline
                try {

                    Connection connection = GridCurrencyPlugin.getDatabase().getConnection();
                    Preconditions.checkNotNull(connection);

                    AccountDAO.fetchAccountId(username, id -> {

                        if (id == -1) {
                            sender.sendMessage(ChatColor.RED + "We could not locate an account with this username!");
                            return;
                        }

                        double currentAmount = CurrencyDAO.getCurrency(id);
                        sender.sendMessage(ChatColor.GREEN + username + " has " + currentAmount + " credits in their account!");

                        try {
                            connection.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                return;
            }

            Account account = AccountManager.get(player.getUniqueId());

            if (account == null) {
                sender.sendMessage(ChatColor.RED + "Could not find a valid profile for " + player.getName());
                return;
            }

            CurrencyAccount currencyAccount = account.getSubAccount(CurrencyAccount.class);
            sender.sendMessage(ChatColor.GREEN + player.getName() + " has " + currencyAccount.getAmount() + " credits in their account!");
            return;
        }

       try {

           // Add|Remove
           String username = args[1];
           Player player = Bukkit.getPlayer(username);
           int amount = Integer.parseInt(args[2]);

           if (player == null) {

               // Offline
               try {

                   Connection connection = GridCurrencyPlugin.getDatabase().getConnection();
                   Preconditions.checkNotNull(connection);

                   AccountDAO.fetchAccountId(username, id -> {

                       if (id == -1) {
                           sender.sendMessage(ChatColor.RED + "We could not locate an account with this username!");
                           return;
                       }

                       double currentAmount = CurrencyDAO.getCurrency(id);

                       switch (action) {

                           case "add":
                               CurrencyDAO.setCurrency(id, Math.abs(currentAmount + amount));
                               sender.sendMessage(ChatColor.GREEN + "Added " + amount + " credits to " + username + "'s account.");
                               break;

                           case "remove":

                               if ((currentAmount - amount) < 0) {
                                   sender.sendMessage(ChatColor.RED + "You entered an amount that would take " + username + " below 0 credits, you cannot do this!");
                                   return;
                               }

                               CurrencyDAO.setCurrency(id, currentAmount - amount);
                               sender.sendMessage(ChatColor.GREEN + "Removed " + amount + " credits from " + username + "'s account.");
                               break;
                       }

                       try {
                           connection.close();
                       } catch (SQLException e) {
                           e.printStackTrace();
                       }
                   });
               } catch (SQLException e) {
                   e.printStackTrace();
               }

               return;
           }

           Account account = AccountManager.get(player.getUniqueId());

           if (account == null) {
               sender.sendMessage(ChatColor.RED + "Could not find a valid profile for " + player.getName());
               return;
           }

           CurrencyAccount currencyAccount = account.getSubAccount(CurrencyAccount.class);

           switch (action) {

               case "add":
                   currencyAccount.setAmount(Math.abs(currencyAccount.getAmount() + amount));
                   sender.sendMessage(ChatColor.GREEN + "Added " + amount + " credits to " + player.getName() + "'s account.");
                   player.sendMessage(ChatColor.GREEN + "You have had " + amount + " credits added to your account!");
                   break;

               case "remove":

                   if ((currencyAccount.getAmount() - amount) < 0) {
                       sender.sendMessage(ChatColor.RED + "You entered an amount that would take " + player.getName() + " below 0 credits, you cannot do this!");
                       return;
                   }

                   currencyAccount.setAmount(currencyAccount.getAmount() - amount);
                   sender.sendMessage(ChatColor.GREEN + "Removed " + amount + " credits from " + player.getName() + "'s account.");
                   player.sendMessage(ChatColor.GREEN + "You have had " + amount + " credits removed from your account!");
                   break;
           }
       } catch (NumberFormatException e) {
           sender.sendMessage(ChatColor.RED + "You entered an invalid number of credits!");
       }
    }
}
