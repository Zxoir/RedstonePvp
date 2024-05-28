package me.zxoir.redstonepvp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 5/28/2024
 */
public class PointsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player && !sender.hasPermission("redstonepvp.points.command")) {
            // TODO: no permission message
            return true;
        }

        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("give")) {
                
            }

            if (args[0].equalsIgnoreCase("add")) {

            }

            if (args[0].equalsIgnoreCase("remove")) {

            }

            if (args[0].equalsIgnoreCase("set")) {

            }
        }

        return true;
    }
}
