package me.zxoir.redstonepvp.commands;

import lombok.Getter;
import me.zxoir.redstonepvp.RedstonePvp;
import me.zxoir.redstonepvp.enchants.SoulboundEnchantment;
import me.zxoir.redstonepvp.util.TimeManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 4/22/2024
 */
public class ItemFrameClickerCommand implements CommandExecutor {
    @Getter
    private static final HashMap<UUID, Object[]> pendingItemFrameModification = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender)
            return true;

        Player player = (Player) sender;
        if (!player.hasPermission("redstonepvp.itemframeclicker.command")) {
            // TODO: no permission message
            return true;
        }

        if (args.length != 1) {
            player.sendMessage("Correct usage: /itemframeclicker <Cooldown/Disable/Enable>");
            return true;
        }

        if (args[0].equalsIgnoreCase("test")) {
            ItemStack itemInHand = player.getItemInHand();
            ItemMeta itemMeta = itemInHand.getItemMeta();
            if (itemMeta != null) {
                if (itemMeta.getLore() == null) {
                    itemMeta.setLore(Collections.singletonList("test"));
                } else
                    itemMeta.getLore().add("Test");
                itemInHand.setItemMeta(itemMeta);
            }
            player.updateInventory();
        }

        if (args[0].equalsIgnoreCase("enchant")) {
            ItemStack itemInHand = player.getItemInHand();
            itemInHand.addUnsafeEnchantment(RedstonePvp.getSoulboundEnchantment(), 1);
            SoulboundEnchantment.applyDisplayName(itemInHand);
            player.updateInventory();
            return true;
        }

        BukkitTask bukkitTask = Bukkit.getServer().getScheduler().runTaskLater(RedstonePvp.getPlugin(RedstonePvp.class), () -> pendingItemFrameModification.remove(player.getUniqueId()), 120);
        Object[] objects = {args[0], bukkitTask};

        pendingItemFrameModification.put(player.getUniqueId(), objects);
        player.sendMessage("Right click on the itemframe you would like to modify");
        return true;
    }

}
