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
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import static me.zxoir.redstonepvp.util.CommonUtils.colorize;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 5/28/2024
 */
@SuppressWarnings("deprecation")
public class TransferCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof ConsoleCommandSender)
            return true;

        Player player = (Player) sender;

        if (args.length == 3) {
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

            PlayerProfile senderProfile = PlayerProfileManager.getPlayerProfile(player.getUniqueId());
            PlayerProfile receiverProfile = PlayerProfileManager.getPlayerProfile(offlinePlayer.getUniqueId());

            if (senderProfile.getStats().getPoints() < amount) {
                player.sendMessage(colorize("&cYou don't have that much points!"));
                return true;
            }

            senderProfile.getStats().updatePoints(amount * -1);
            receiverProfile.getStats().updatePoints(amount);
            player.sendMessage(colorize("&aYou have sent " + offlinePlayer.getName() + " " + amount + " points."));
            if (offlinePlayer.isOnline())
                offlinePlayer.getPlayer().sendMessage(colorize("&aYou have received " + amount + " points from " + player.getName()));
            return true;
        }

        player.sendMessage(colorize("&7Correct usage: /transfer <Player> <Amount>"));
        return true;
    }
}
