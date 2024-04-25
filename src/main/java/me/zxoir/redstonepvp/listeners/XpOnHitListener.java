package me.zxoir.redstonepvp.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 4/24/2024
 */
public class XpOnHitListener implements Listener {

    @EventHandler
    public void onPlayerHit(@NotNull EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();

        if (!(damager instanceof Player && entity instanceof Player))
            return;

        Player attacker = (Player) damager;
        Player victim = (Player) entity;

        giveHalfLevelXP(attacker);
    }

    private int getXpNeededToLevelUp(int level) {
        return level > 30 ? 62 + (level - 30) * 7 : level >= 16 ? 17 + (level - 15) * 3 : 17;
    }

    private void giveHalfLevelXP(@NotNull Player player) {
        int level = player.getLevel();

        player.giveExp(getXpNeededToLevelUp(level) / 2);
    }
}
