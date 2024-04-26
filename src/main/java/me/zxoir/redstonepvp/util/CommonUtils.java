package me.zxoir.redstonepvp.util;

import me.zxoir.redstonepvp.RedstonePvp;
import me.zxoir.redstonepvp.data.PlayerProfile;
import me.zxoir.redstonepvp.managers.PlayerProfileManager;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 4/18/2024
 */
public class CommonUtils {
    private static final RedstonePvp mainInstance = RedstonePvp.getPlugin(RedstonePvp.class);

    public static void runTaskSync(Task task) {
        new BukkitRunnable() {
            @Override
            public void run() {
                task.execute();
            }
        }.runTask(mainInstance);
    }

    public static void runTaskAsync(Task task) {
        new BukkitRunnable() {
            @Override
            public void run() {
                task.execute();
            }
        }.runTaskAsynchronously(mainInstance);
    }

    public static boolean isLong(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void sendActionText(Player player, String message) {
        runTaskAsync(() -> {
            //PlayerProfile user = PlayerProfileManager.getPlayerProfile(player.getUniqueId());

            //user.setActionbar(true);
            //Bukkit.getScheduler().runTaskLaterAsynchronously(mainInstance, () -> user.setActionbar(false), 60);

            PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(colorize(message)), (byte) 2);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        });
    }

    @NotNull
    public static String colorize(String arg) {
        return ChatColor.translateAlternateColorCodes('&', arg);
    }

    public interface Task {
        void execute();
    }
}
