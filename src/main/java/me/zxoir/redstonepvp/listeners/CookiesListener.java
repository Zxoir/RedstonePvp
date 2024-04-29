package me.zxoir.redstonepvp.listeners;

import me.zxoir.redstonepvp.RedstonePvp;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 4/19/2024
 */
public class CookiesListener implements Listener {
    private final RedstonePvp plugin = RedstonePvp.getPlugin(RedstonePvp.class);
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    @EventHandler
    public void onCookieConsumption(@NotNull PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Material item = event.getItem().getType();

        if (!item.equals(Material.COOKIE))
            return;

        long currentTime = System.currentTimeMillis();
        long cooldownTime = cooldowns.getOrDefault(uuid, 0L);

        if (currentTime >= cooldownTime) {
            cooldowns.put(uuid, currentTime + plugin.getCookieCooldown());
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));
            return;
        }

        int remainingSeconds = (int) ((cooldownTime - currentTime) / 1000);
        player.sendMessage("You can eat another cookie in " + remainingSeconds + " seconds");
        event.setCancelled(true);
    }

    @EventHandler
    public void onItemHeldEvent(@NotNull PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItem(event.getNewSlot());
        ItemStack previousItemStack = player.getInventory().getItem(event.getPreviousSlot());

        if (itemInHand != null && itemInHand.getType().equals(Material.COOKIE)) {
            player.setFoodLevel(19);
        }

        if (previousItemStack != null && previousItemStack.getType().equals(Material.COOKIE)) {
            player.setFoodLevel(20);
        }
    }

    @EventHandler
    public void onPickUp(@NotNull PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        ItemStack pickedItem = event.getItem().getItemStack();

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            ItemStack itemOnHand = player.getItemInHand();
            if (!itemOnHand.getType().equals(pickedItem.getType()))
                return;

            player.setFoodLevel(19);
        }, 1);
    }

    @EventHandler
    public void onDrop(@NotNull PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack itemOnHand = player.getItemInHand();
        ItemStack droppedItem = event.getItemDrop().getItemStack();

        if (droppedItem.getType() != Material.COOKIE || itemOnHand.getType() != Material.AIR)
            return;

        player.setFoodLevel(20);
    }

    @EventHandler
    public void onHungerChange(@NotNull FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();

        if (!event.getEntityType().equals(EntityType.PLAYER))
            return;

        ItemStack itemInHand = player.getItemInHand();
        if (itemInHand != null && itemInHand.getType().equals(Material.COOKIE)) {
            player.setFoodLevel(19);
            event.setCancelled(true);
            return;
        }

        player.setFoodLevel(20);
        event.setCancelled(true);
    }
}
