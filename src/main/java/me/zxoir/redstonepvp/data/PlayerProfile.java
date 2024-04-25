package me.zxoir.redstonepvp.data;

import lombok.Getter;
import me.zxoir.redstonepvp.managers.PlayerProfileDatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 4/17/2024
 */
@Getter
public class PlayerProfile {
    private final UUID uuid;
    private final PlayerStats stats;
    private final Set<UUID> friends;
    private final String dateJoined;
    private boolean afk;

    public PlayerProfile(UUID uuid) {
        this.uuid = uuid;
        this.stats = new PlayerStats();
        this.friends = new HashSet<>();
        this.dateJoined = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa").format(new Date());
        this.afk = true;
    }

    public PlayerProfile(UUID uuid, PlayerStats stats, Set<UUID> friends, String dateJoined) {
        this.uuid = uuid;
        this.stats = stats;
        this.friends = friends;
        this.dateJoined = dateJoined;
        this.afk = true;
    }

    @Nullable
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    @NotNull
    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    public synchronized void addFriend(@NotNull UUID uuid) {
        friends.add(uuid);
        PlayerProfileDatabaseManager.updateFriends(this);
    }

    public synchronized void removeFriend(@NotNull UUID uuid) {
        friends.remove(uuid);
        PlayerProfileDatabaseManager.updateFriends(this);
    }

    public synchronized void setAfk(boolean afk) {
        this.afk = afk;
    }
}