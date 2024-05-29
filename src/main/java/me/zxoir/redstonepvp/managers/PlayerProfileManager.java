package me.zxoir.redstonepvp.managers;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Getter;
import me.zxoir.redstonepvp.RedstonePvp;
import me.zxoir.redstonepvp.data.PlayerProfile;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 4/17/2024
 */
public class PlayerProfileManager {
    @Getter
    private static final Cache<UUID, PlayerProfile> profileCache = Caffeine.newBuilder()
            .expireAfterAccess(3, TimeUnit.MINUTES)
            .build();

    public static void createPlayerProfile(@NotNull UUID uuid) {
        PlayerProfile profile = new PlayerProfile(uuid);
        PlayerProfileDatabaseManager.savePlayerProfile(profile);
        profileCache.put(uuid, profile);
    }

    public static @NotNull PlayerProfile getPlayerProfile(@NotNull UUID uuid) {
        PlayerProfile profile = profileCache.getIfPresent(uuid);

        if (profile == null) {
            profile = PlayerProfileDatabaseManager.getPlayerProfile(uuid);
            profileCache.put(uuid, profile);
        }

        return profile;
    }

    public static void cacheProfile(@NotNull PlayerProfile profile) {
        if (profileCache.getIfPresent(profile.getUuid()) == null)
            profileCache.put(profile.getUuid(), profile);
        RedstonePvp.getLOGGER().debug("Cached profile");
    }

    public static void removeProfile(UUID uuid) {
        PlayerProfile profile = profileCache.getIfPresent(uuid);

        if (profile != null)
            PlayerProfileDatabaseManager.updatePlayerProfile(profile);

        profileCache.invalidate(uuid);
        RedstonePvp.getLOGGER().debug("Removed profile from cache");
    }
}