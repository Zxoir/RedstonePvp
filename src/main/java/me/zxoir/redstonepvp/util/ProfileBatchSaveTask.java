package me.zxoir.redstonepvp.util;

import me.zxoir.redstonepvp.RedstonePvp;
import me.zxoir.redstonepvp.data.PlayerProfile;
import me.zxoir.redstonepvp.managers.PlayerProfileDatabaseManager;
import me.zxoir.redstonepvp.managers.PlayerProfileManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 4/19/2024
 */
public class ProfileBatchSaveTask extends BukkitRunnable {
    private static final int BATCH_SIZE = 30;
    private static final RedstonePvp pluginInstance = RedstonePvp.getPlugin(RedstonePvp.class);

    @Override
    public void run() {
        List<PlayerProfile> profilesToSave = new ArrayList<>();

        for (PlayerProfile profile : PlayerProfileManager.getProfileCache().asMap().values()) {
            profilesToSave.add(profile);

            if (profilesToSave.size() >= BATCH_SIZE) {
                PlayerProfileDatabaseManager.batchUpdatePlayerProfiles(profilesToSave);
                profilesToSave.clear();
            }
        }

        if (!profilesToSave.isEmpty())
            PlayerProfileDatabaseManager.batchUpdatePlayerProfiles(profilesToSave);
    }
}
