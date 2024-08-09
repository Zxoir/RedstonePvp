package me.zxoir.redstonepvp.managers;

import lombok.Getter;
import me.zxoir.redstonepvp.RedstonePvp;
import me.zxoir.redstonepvp.data.Arena;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 6/6/2024
 */
public class ArenaManager {
    @Getter
    private static final HashMap<String, Arena> arenas = new HashMap<>();
    @Getter
    private static final HashMap<UUID, Arena> duelsInProgress = new HashMap<>();
    @Getter
    private static final HashMap<UUID, Set<UUID>> duelRequests = new HashMap<>();
    @Getter
    private static final List<UUID> duelCountdown = new ArrayList<>();

    public static void loadArenas() {
        FileConfiguration config = RedstonePvp.getDataFile().getConfig();
        if (config.isConfigurationSection("arenas")) {
            for (String key : config.getConfigurationSection("arenas").getKeys(false)) {
                String path = "arenas." + key;
                String name = config.getString(path + ".name");
                Location pointA = (Location) config.get(path + ".pointA");
                Location pointB = (Location) config.get(path + ".pointB");
                Location spawn = (Location) config.get(path + ".spawn");
                arenas.put(name.toLowerCase(), new Arena(name, pointA, pointB, spawn));
            }
        }
    }

    /*@Nullable
    public static Arena getRandomArena() {
        if (arenas.isEmpty()) {
            return null;
        }

        List<Arena> arenaList = new ArrayList<>(arenas.values());
        return arenaList.get(ThreadLocalRandom.current().nextInt(arenaList.size()));
    }*/

    @Nullable
    public static Arena getRandomAvailableArena() {
        if (arenas.isEmpty()) {
            return null;
        }

        for (Arena randomArena : arenas.values()) {
            if (!randomArena.isActive() && randomArena.getSpawn() != null && randomArena.getPointA() != null && randomArena.getPointB() != null)
                return randomArena;
        }

        return null;
    }

    public static void saveArena(@NotNull Arena arena) {
        String path = "arenas." + arena.getName().toLowerCase();
        RedstonePvp.getDataFile().getConfig().set(path + ".name", arena.getName());
        RedstonePvp.getDataFile().getConfig().set(path + ".pointA", arena.getPointA());
        RedstonePvp.getDataFile().getConfig().set(path + ".pointB", arena.getPointB());
        RedstonePvp.getDataFile().getConfig().set(path + ".spawn", arena.getSpawn());

        RedstonePvp.getDataFile().saveConfig();
        arenas.put(arena.getName().toLowerCase(), arena);
    }

    public static void deleteArena(@NotNull Arena arena) {
        RedstonePvp.getDataFile().getConfig().set("arenas." + arena.getName(), null);
        RedstonePvp.getDataFile().saveConfig();

        arenas.remove(arena.getName().toLowerCase());
    }

    public static void saveArenas() {
        FileConfiguration config = RedstonePvp.getDataFile().getConfig();
        config.set("arenas", null);
        for (Arena arena : arenas.values()) {
            String path = "arenas." + arena.getName().toLowerCase();
            config.set(path + ".name", arena.getName());
            config.set(path + ".pointA", arena.getPointA());
            config.set(path + ".pointB", arena.getPointB());
            config.set(path + ".spawn", arena.getSpawn());
        }
        RedstonePvp.getDataFile().saveConfig();
    }
}
