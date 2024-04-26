package me.zxoir.redstonepvp.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
@Data
public class CombatLog {
    @Nullable
    Player lastHit;
    @Nullable
    BukkitTask bukkitTask;
    Player profile;
}
