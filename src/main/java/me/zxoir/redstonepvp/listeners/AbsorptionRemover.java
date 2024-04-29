package me.zxoir.redstonepvp.listeners;

import me.zxoir.redstonepvp.RedstonePvp;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffectType;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 4/30/2024
 */
public class AbsorptionRemover implements Listener {
    private static final RedstonePvp mainInstance = RedstonePvp.getPlugin(RedstonePvp.class);

    @EventHandler
    public void onGapple(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (event.getItem().getType() != Material.GOLDEN_APPLE)
            return;

        Bukkit.getScheduler().runTaskLater(mainInstance, () -> {
            if (!player.hasPotionEffect(PotionEffectType.ABSORPTION))
                return;

            player.removePotionEffect(PotionEffectType.ABSORPTION);
        }, 1);
    }
}
