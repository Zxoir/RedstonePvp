package me.zxoir.redstonepvp.listeners;

import me.zxoir.redstonepvp.RedstonePvp;
import me.zxoir.redstonepvp.util.CommonUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatLogListener implements Listener {
    private static final RedstonePvp mainInstance = RedstonePvp.getPlugin(RedstonePvp.class);
    private final Map<UUID, Integer> combatTimers = new HashMap<>();
    private final int COMBAT_DURATION_SECONDS = 10;

    @EventHandler(ignoreCancelled = true)
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (!event.getEntity().getType().equals(EntityType.PLAYER) || !event.getDamager().getType().equals(EntityType.PLAYER) || event.getEntity().equals(event.getDamager()))
            return;

        Player target = (Player) event.getEntity();
        Player attacker = (Player) event.getDamager();

        if (event.getFinalDamage() == 0)
            return;

        if (target.getGameMode() == GameMode.CREATIVE || attacker.getGameMode() == GameMode.CREATIVE)
            return;

        startCombatTimer(attacker, target);
        startCombatTimer(target, attacker);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (!combatTimers.containsKey(player.getUniqueId()))
            return;

        combatTimers.remove(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (!combatTimers.containsKey(player.getUniqueId()))
            return;

        player.setHealth(0);
        combatTimers.remove(player.getUniqueId());
    }

    private void sendCombatLogActionBar(Player player, int time) {
        CommonUtils.sendActionText(player, "&c&lCOMBATLOG > &7You're in combat for " + time + " seconds!");
    }

    private void startCombatTimer(@NotNull Player player, @NotNull Player attacker) {
        sendCombatLogActionBar(player, COMBAT_DURATION_SECONDS);

        if (combatTimers.containsKey(player.getUniqueId())) {
            combatTimers.put(player.getUniqueId(), COMBAT_DURATION_SECONDS);
            return;
        }

        BukkitTask taskTimer = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !combatTimers.containsKey(player.getUniqueId())) {
                    endCombat(player);
                    this.cancel();
                    return;
                }

                int remainingTime = combatTimers.get(player.getUniqueId());
                if (remainingTime > 0) {
                    sendCombatLogActionBar(player, remainingTime);
                    combatTimers.put(player.getUniqueId(), remainingTime - 1);
                    return;
                }

                endCombat(player);
                this.cancel();
            }
        }.runTaskTimer(mainInstance, 0, 20L);

        combatTimers.put(player.getUniqueId(), COMBAT_DURATION_SECONDS);
    }

    private void endCombat(@NotNull Player player) {
        combatTimers.remove(player.getUniqueId());
        CommonUtils.sendActionText(player, "&c&lCOMBATLOG > &aYou're no longer in combat!");
    }
}
