package me.zxoir.redstonepvp.events;

import me.zxoir.redstonepvp.data.PlayerProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 4/19/2024
 */
public class PlayerAFKEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final boolean afk;
    private final PlayerProfile profile;

    public PlayerAFKEvent(@NotNull Player player, boolean afk, PlayerProfile profile) {
        this.player = player;
        this.afk = afk;
        this.profile = profile;
    }

    public @NotNull Player getPlayer() {
        return player;
    }

    public boolean isAFK() {
        return afk;
    }

    public @NotNull PlayerProfile getProfile() {
        return profile;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
