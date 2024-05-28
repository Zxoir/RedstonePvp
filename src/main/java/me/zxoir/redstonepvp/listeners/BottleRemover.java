package me.zxoir.redstonepvp.listeners;

import me.zxoir.redstonepvp.RedstonePvp;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.jetbrains.annotations.NotNull;

public class BottleRemover implements Listener {
    @EventHandler
    public void onPotionDrink(@NotNull PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(RedstonePvp.getPlugin(RedstonePvp.class), () -> {
            player.getInventory().remove(Material.GLASS_BOTTLE);
            player.updateInventory();
        }, 1L);
    }
}
