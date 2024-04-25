package me.zxoir.redstonepvp.listeners;

import me.zxoir.redstonepvp.data.PlayerProfile;
import me.zxoir.redstonepvp.events.PlayerAFKEvent;
import me.zxoir.redstonepvp.managers.PlayerProfileDatabaseManager;
import me.zxoir.redstonepvp.managers.PlayerProfileManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 4/19/2024
 */
public class PlayerProfileListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();

        PlayerProfileDatabaseManager.isPlayerInDatabase(player.getUniqueId()).thenAccept(isInDatabase -> {
            if (isInDatabase)
                return;

            PlayerProfileManager.createPlayerProfile(player.getUniqueId());
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAFK(@NotNull PlayerAFKEvent event) {
        Player player = event.getPlayer();

        if (event.isAFK()) {
            PlayerProfileManager.removeProfile(player.getUniqueId());
            return;
        }

        PlayerProfileManager.cacheProfile(event.getProfile());
    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = PlayerProfileManager.getProfileCache().getIfPresent(player.getUniqueId());

        if (profile == null)
            return;

        PlayerProfileManager.removeProfile(player.getUniqueId());
    }

    @EventHandler
    public void onKick(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = PlayerProfileManager.getProfileCache().getIfPresent(player.getUniqueId());

        if (profile == null)
            return;

        PlayerProfileManager.removeProfile(player.getUniqueId());
    }

}
