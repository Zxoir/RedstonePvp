package me.zxoir.redstonepvp.commands;

import me.zxoir.redstonepvp.data.PlayerProfile;
import me.zxoir.redstonepvp.managers.PlayerProfileDatabaseManager;
import me.zxoir.redstonepvp.managers.PlayerProfileManager;
import me.zxoir.redstonepvp.util.CommonUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.zxoir.redstonepvp.util.CommonUtils.colorize;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 5/28/2024
 */
public class PointsCommand implements CommandExecutor {
    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player && !sender.hasPermission("redstonepvp.points.command")) {
            // TODO: no permission message
            return true;
        }

        if (args.length == 4) {
            if (args[0].equalsIgnoreCase("transfer")) {
                OfflinePlayer senderPlayer = Bukkit.getOfflinePlayer(args[1]);
                if (senderPlayer == null || !PlayerProfileDatabaseManager.isPlayerInDatabase(senderPlayer.getUniqueId())) {
                    sender.sendMessage(colorize("&c" + args[1] + " player does not exist"));
                    return true;
                }

                OfflinePlayer receiverPlayer = Bukkit.getOfflinePlayer(args[2]);
                if (receiverPlayer == null || !PlayerProfileDatabaseManager.isPlayerInDatabase(receiverPlayer.getUniqueId())) {
                    sender.sendMessage(colorize("&c" + args[2] + " player does not exist"));
                    return true;
                }

                if (!CommonUtils.isInteger(args[3])) {
                    sender.sendMessage(colorize("&cThe amount must be a integer number! \n&7/points transfer <Sender> <Receiver> <Amount>"));
                    return true;
                }

                int amount = Integer.parseInt(args[3]);
                if (amount <= 0) {
                    sender.sendMessage(colorize("&cThe amount must be more than 0!"));
                    return true;
                }

                PlayerProfile senderProfile = PlayerProfileManager.getPlayerProfile(senderPlayer.getUniqueId());
                PlayerProfile receiverProfile = PlayerProfileManager.getPlayerProfile(receiverPlayer.getUniqueId());

                if (senderProfile.getStats().getPoints() < amount) {
                    sender.sendMessage(colorize("&c" + senderPlayer.getName() + " doesn't have that much points!"));
                    return true;
                }

                senderProfile.getStats().updatePoints(amount * -1);
                receiverProfile.getStats().updatePoints(amount);
                sender.sendMessage(colorize("&a" + senderPlayer.getName() + " has transferred " + amount + " points to " + receiverPlayer.getName()));
            }
        }

        if (args.length == 3 && (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("set"))) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
            if (offlinePlayer == null || !PlayerProfileDatabaseManager.isPlayerInDatabase(offlinePlayer.getUniqueId())) {
                sender.sendMessage(colorize("&cThat player does not exist"));
                return true;
            }

            if (!CommonUtils.isInteger(args[2])) {
                sender.sendMessage(colorize("&cThe amount must be a integer number! \n&7/points give <Player> <Amount>"));
                return true;
            }

            int amount = Integer.parseInt(args[2]);
            if (amount <= 0) {
                sender.sendMessage(colorize("&cThe amount must be more than 0!"));
                return true;
            }

            PlayerProfile playerProfile = PlayerProfileManager.getPlayerProfile(offlinePlayer.getUniqueId());

            if (args[0].equalsIgnoreCase("remove")) {
                if (playerProfile.getStats().getPoints() < amount) {
                    sender.sendMessage(colorize("&c" + offlinePlayer.getName() + " doesn't have that much points!"));
                    return true;
                }

                amount *= -1;
            }

            if (args[0].equalsIgnoreCase("set"))
                playerProfile.getStats().setPoints(amount);
            else
                playerProfile.getStats().updatePoints(amount);

            sender.sendMessage(colorize("&aYou have sent " + offlinePlayer.getName() + " " + amount + " points."));
            return true;
        }

        return true;
    }
}
