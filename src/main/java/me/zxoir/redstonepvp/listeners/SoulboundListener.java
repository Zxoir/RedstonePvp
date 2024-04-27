package me.zxoir.redstonepvp.listeners;

import me.zxoir.redstonepvp.RedstonePvp;
import me.zxoir.redstonepvp.enchants.SoulboundEnchantment;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 4/25/2024
 */
public class SoulboundListener implements Listener {
    private static final HashMap<UUID, List<ItemStack>> itemsToReturn = new HashMap<>();

    @EventHandler
    public void onDeath(@NotNull PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (player.getWorld() != null && player.getWorld().getGameRuleValue("keepInventory").equalsIgnoreCase("true"))
            return;

        event.getDrops().removeIf(itemStack -> {
            if (!SoulboundEnchantment.isSoulbound(itemStack))
                return false;

            addItemToMap(player, itemStack);
            return true;
        });

        Bukkit.getScheduler().runTaskLater(RedstonePvp.getPlugin(RedstonePvp.class), () -> player.spigot().respawn(), 5);
    }

    @EventHandler
    public void onRespawn(@NotNull PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (!itemsToReturn.containsKey(player.getUniqueId()))
            return;

        Bukkit.getScheduler().runTaskLater(RedstonePvp.getPlugin(RedstonePvp.class), () -> {
            List<ItemStack> items = itemsToReturn.get(player.getUniqueId());
            items.forEach(item -> player.getInventory().addItem(item));
            itemsToReturn.remove(player.getUniqueId());
            player.updateInventory();
        }, 5);
    }

    @EventHandler
    public void onDropItem(@NotNull PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItemDrop().getItemStack();

        if (itemStack == null || !itemStack.containsEnchantment(RedstonePvp.getSoulboundEnchantment()))
            return;

        event.setCancelled(true);
        player.updateInventory();
    }

    private void addItemToMap(@NotNull Player player, @NotNull ItemStack itemStack) {
        if (!itemsToReturn.containsKey(player.getUniqueId())) {
            List<ItemStack> items = new ArrayList<>();
            items.add(itemStack);
            itemsToReturn.put(player.getUniqueId(), items);
            return;
        }

        List<ItemStack> items = itemsToReturn.get(player.getUniqueId());
        items.add(itemStack);
    }
}
