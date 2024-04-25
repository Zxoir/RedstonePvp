package me.zxoir.redstonepvp.listeners;

import me.zxoir.redstonepvp.RedstonePvp;
import me.zxoir.redstonepvp.data.PlayerProfile;
import me.zxoir.redstonepvp.events.PlayerAFKEvent;
import me.zxoir.redstonepvp.managers.PlayerProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 4/18/2024
 */
public class AFKListener implements Listener {
    private static final RedstonePvp mainInstance = RedstonePvp.getPlugin(RedstonePvp.class);
    private static final Set<UUID> playersNotMoved = new HashSet<>();
    private static final Map<UUID, Boolean> afkChecks = new HashMap<>();

    @EventHandler(ignoreCancelled = true)
    public void onMove(@NotNull PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();

        cancelAfkCheck(player);
        scheduleAfkCheck(player);
    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = PlayerProfileManager.getPlayerProfile(player.getUniqueId());
        cancelAfkCheck(player);
    }

    @EventHandler
    public void onKick(@NotNull PlayerKickEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = PlayerProfileManager.getPlayerProfile(player.getUniqueId());
        cancelAfkCheck(player);
    }

    @EventHandler
    public void onCommandUsage(@NotNull PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = PlayerProfileManager.getPlayerProfile(player.getUniqueId());

        if (!profile.isAfk())
            return;

        cancelAfkCheck(player);
        scheduleAfkCheck(player);
    }

    @EventHandler
    public void onChat(@NotNull AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = PlayerProfileManager.getPlayerProfile(player.getUniqueId());

        if (!profile.isAfk())
            return;

        cancelAfkCheck(player);
        scheduleAfkCheck(player);
    }

    @EventHandler
    public void onInteract(@NotNull PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = PlayerProfileManager.getPlayerProfile(player.getUniqueId());

        if (!profile.isAfk())
            return;

        cancelAfkCheck(player);
        scheduleAfkCheck(player);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        playersNotMoved.add(player.getUniqueId());
    }

    @EventHandler
    public void onAFK(@NotNull PlayerAFKEvent event) {
        Player player = event.getPlayer();

        if (event.isAFK()) {
            player.sendMessage("You are now afk.");
            return;
        }

        if (!playersNotMoved.contains(player.getUniqueId()))
            player.sendMessage("You are no longer afk.");

        playersNotMoved.remove(player.getUniqueId());
    }

    private void scheduleAfkCheck(@NotNull Player player) {
        if (afkChecks.containsKey(player.getUniqueId()))
            return;

        long AFK_TIMER = 160;
        afkChecks.put(player.getUniqueId(), false);
        Bukkit.getScheduler().runTaskLater(mainInstance, () -> {

            if (afkChecks.get(player.getUniqueId())) {
                cancelAfkCheck(player);
                afkChecks.remove(player.getUniqueId());
                scheduleAfkCheck(player);
                return;
            }

            afkChecks.remove(player.getUniqueId());
            cancelAfkCheck(player);
            setPlayerAFK(player, true);
        }, 20L * AFK_TIMER);
    }

    private void cancelAfkCheck(@NotNull Player player) {
        if (afkChecks.containsKey(player.getUniqueId()))
            afkChecks.put(player.getUniqueId(), true);

        PlayerProfile profile = PlayerProfileManager.getPlayerProfile(player.getUniqueId());
        if (profile.isAfk())
            setPlayerAFK(player, false);
    }

    private void setPlayerAFK(@NotNull Player player, boolean afk) {
        PlayerProfile profile = PlayerProfileManager.getPlayerProfile(player.getUniqueId());
        profile.setAfk(afk);
        PlayerAFKEvent afkEvent = new PlayerAFKEvent(player, afk, profile);
        Bukkit.getServer().getPluginManager().callEvent(afkEvent);
    }

}