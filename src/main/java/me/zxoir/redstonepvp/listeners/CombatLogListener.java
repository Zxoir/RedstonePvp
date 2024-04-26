package me.zxoir.redstonepvp.listeners;

import lombok.Getter;
import me.zxoir.redstonepvp.RedstonePvp;
import me.zxoir.redstonepvp.data.PlayerProfile;
import me.zxoir.redstonepvp.data.PlayerStats;
import me.zxoir.redstonepvp.managers.PlayerProfileManager;
import me.zxoir.redstonepvp.util.CombatLog;
import me.zxoir.redstonepvp.util.CommonUtils;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CombatLogListener implements Listener {
    @Getter
    private static final ConcurrentHashMap<UUID, CombatLog> combatLogs = new ConcurrentHashMap<>();
    private static final RedstonePvp mainInstance = RedstonePvp.getPlugin(RedstonePvp.class);

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerProfile user = PlayerProfileManager.getPlayerProfile(player.getUniqueId());

        if (!combatLogs.containsKey(player.getUniqueId()))
            return;

        CombatLog combatLog = combatLogs.get(player.getUniqueId());
        if (combatLog.getBukkitTask() != null)
            combatLog.getBukkitTask().cancel();

        player.setHealth(0);

        if (combatLog.getLastHit() == null)
            return;

        Player killerUser = combatLog.getLastHit();

        if (killerUser != null) {
            combatLogs.remove(user.getUuid());
            return;
        }

        // Checking if anyone has hit the player
        CommonUtils.runTaskAsync(() -> {

            for (CombatLog log : combatLogs.values()) {
                Player lastHit = log.getProfile();

                if (log.getLastHit() != null && log.getLastHit().getUniqueId().equals(user.getUuid())) {
                    PlayerProfile profile = PlayerProfileManager.getPlayerProfile(user.getUuid());
                    PlayerStats stats = profile.getStats();
                    stats.updateDeaths(1);

                    profile = PlayerProfileManager.getPlayerProfile(lastHit.getUniqueId());
                    stats = profile.getStats();
                    stats.updateKills(1);

                    if (lastHit.getPlayer() == null || !profile.getOfflinePlayer().isOnline()) {
                        combatLogs.remove(user.getUuid());
                        return;
                    }

                    //CommonUtils.runTaskSync(() -> lastHit.getPlayer().sendMessage(ConfigManager.getKillMessage(lastHit.getPlayer().getName(), player.getName(), gainedCoins, xpGained)));
                    //CommonUtils.runTaskSync(() -> CommonUtils.sendActionText(lastHit.getPlayer(), ConfigManager.getKillActionbar(lastHit.getPlayer().getName(), player.getName(), gainedCoins, xpGained)));
                    return;
                }

            }
            combatLogs.remove(user.getUuid());

        });
    }

    /*@EventHandler
    public void onKick(@NotNull PlayerKickEvent event) {
        Player player = event.getPlayer();
        User user = PickMcFFA.getCachedUsers().getIfPresent(player.getUniqueId());

        if (user == null)
            return;

        if (!combatLogs.containsKey(user))
            return;

        CombatLog combatLog = combatLogs.get(user);
        if (combatLog.getBukkitTask() != null)
            combatLog.getBukkitTask().cancel();

        player.setHealth(0);

        if (combatLog.getLastHit() == null)
            return;

        User killerUser = combatLog.getLastHit();

        if (killerUser != null) {
            combatLogs.remove(user);
            return;
        }

        // Checking if anyone has hit the player
        runAsync(() -> {

            for (CombatLog log : combatLogs.values()) {
                User lastHit = log.getUser();

                if (log.getLastHit() != null && log.getLastHit().equals(user)) {
                    Stats stats = user.getStats();
                    stats.setDeaths(user.getStats().getDeaths() + 1);
                    stats.deductCoins(5, 10);
                    user.save();
                    StatsCommand.refreshPlayerHologram(user.getPlayer());
                    ScoreboardListener.updateScoreBoard(user.getPlayer());

                    Kill kill = new Kill(stats.getUuid(), new Date());
                    stats = lastHit.getStats();
                    stats.getKills().add(kill);
                    int gainedCoins = stats.addCoins(15, 20);
                    int xpGained = stats.addXp(50, 150);
                    lastHit.save();
                    StatsCommand.refreshPlayerHologram(lastHit.getPlayer());
                    ScoreboardListener.updateScoreBoard(lastHit.getPlayer());

                    if (lastHit.getPlayer() == null || !lastHit.getOfflinePlayer().isOnline()) {
                        combatLogs.remove(user);
                        return;
                    }

                    Utils.runTaskSync(() -> lastHit.getPlayer().sendMessage(ConfigManager.getKillMessage(lastHit.getPlayer().getName(), player.getName(), gainedCoins, xpGained)));
                    Utils.runTaskSync(() -> Utils.sendActionText(lastHit.getPlayer(), ConfigManager.getKillActionbar(lastHit.getPlayer().getName(), player.getName(), gainedCoins, xpGained)));
                    return;
                }

            }
            combatLogs.remove(user);

        });
    }

    @EventHandler
    public void onShoot(@NotNull EntityDamageByEntityEvent event) {
        // Checking if a player got shot by a bow
        if (!event.getDamager().getType().equals(EntityType.ARROW) || !event.getEntity().getType().equals(EntityType.PLAYER) || !(((Arrow) event.getDamager()).getShooter() instanceof Player))
            return;

        Player player = (Player) event.getEntity();
        Player damager = (Player) ((Arrow) event.getDamager()).getShooter();

        // If the player hits himself, return
        if (player.equals(damager))
            return;

        // If the damage is cancelled, return
        if (event.isCancelled() || event.getFinalDamage() == 0)
            return;

        startCombatLog(player, damager);
    }

    @EventHandler
    public void onDeath(@NotNull PlayerDeathEvent event) {
        Player player = event.getEntity();
        User user = PickMcFFA.getCachedUsers().getIfPresent(player.getUniqueId());

        if (user == null)
            return;

        combatLogs.remove(user);
    }

    @EventHandler
    public void onSnowball(@NotNull EntityDamageByEntityEvent event) {
        // Checking if a player got shot by a bow
        if (!event.getDamager().getType().equals(EntityType.SNOWBALL) || !event.getEntity().getType().equals(EntityType.PLAYER) || !(((Snowball) event.getDamager()).getShooter() instanceof Player))
            return;

        Player player = (Player) event.getEntity();
        Player damager = (Player) ((Snowball) event.getDamager()).getShooter();

        // If the player hits himself, return
        if (player.equals(damager))
            return;

        // If the damage is cancelled, return
        if (event.isCancelled())
            return;

        startCombatLog(player, damager);
    }*/

    @EventHandler(ignoreCancelled = true)
    public void onDamage(@NotNull EntityDamageByEntityEvent event) {
        if (!event.getEntity().getType().equals(EntityType.PLAYER) || !event.getDamager().getType().equals(EntityType.PLAYER) || event.getEntity().equals(event.getDamager()))
            return;

        Player player = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();

        if (event.getFinalDamage() == 0)
            return;

        startCombatLog(player, damager);
    }

    private void startCombatLog(@NotNull Player player, @NotNull Player damager) {
        CombatLog playerCombatlog = combatLogs.get(player.getUniqueId());
        if (combatLogs.containsKey(player.getUniqueId()) && playerCombatlog.getBukkitTask() != null)
            playerCombatlog.getBukkitTask().cancel();

        CombatLog damagerCombatlog = combatLogs.get(damager.getUniqueId());
        if (combatLogs.containsKey(damager.getUniqueId()) && damagerCombatlog.getBukkitTask() != null)
            damagerCombatlog.getBukkitTask().cancel();

        combatLogs.put(player.getUniqueId(), new CombatLog(damager, timerTask(player), player));

        if (combatLogs.containsKey(damager.getUniqueId()))
            combatLogs.put(damager.getUniqueId(), new CombatLog(damagerCombatlog.getLastHit(), timerTask(damager), damager));
        else
            combatLogs.put(damager.getUniqueId(), new CombatLog(player, timerTask(damager), damager));

        /*if (!ConfigManager.getCombatMessage(damager.getName()).isEmpty())
            player.sendMessage(ConfigManager.getCombatMessage(damager.getName()));

        if (!ConfigManager.getCombatMessage(damager.getName()).isEmpty())
            damager.sendMessage(ConfigManager.getCombatMessage(player.getName()));

        if (ConfigManager.isCombatCountdown())
            return;

        if (!ConfigManager.getCombatActionbar(damager.getName()).isEmpty())*/
        //CommonUtils.sendActionText(player, ConfigManager.getCombatActionbar(damager.getName()));
        CommonUtils.sendActionText(player, "&c&lCOMBATLOG > &8You're in combat for 10 seconds!");

        //if (!ConfigManager.getCombatActionbar(damager.getName()).isEmpty())
        //CommonUtils.sendActionText(damager, ConfigManager.getCombatActionbar(player.getName()));
        CommonUtils.sendActionText(damager, "&c&lCOMBATLOG > &8You're in combat for 10 seconds!");
    }

    @Nullable
    private BukkitTask timerTask(Player player) {
        return /*ConfigManager.isCombatCountdown() ? */new BukkitRunnable() {
            //long time = ConfigManager.getCombatTime();
            long time = 10;

            @Override
            public void run() {

                if (!player.isOnline() || !combatLogs.containsKey(player.getUniqueId())) {
                    this.cancel();
                    return;
                }

                if (time <= 0) {
                    CommonUtils.runTaskSync(() -> CommonUtils.sendActionText(player, "No longer in Combat!"));
                    combatLogs.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                //if (!user.isActionbar())
                    CommonUtils.runTaskSync(() -> CommonUtils.sendActionText(player, "&c&lCOMBATLOG > &8You're in combat for " + (time + 1) + " seconds!"));
                time--;
            }

        }.runTaskTimerAsynchronously(mainInstance, 0, 20L) /*: null*/;
    }
}
