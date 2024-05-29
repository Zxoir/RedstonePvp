package me.zxoir.redstonepvp.util;

import io.netty.util.internal.ThreadLocalRandom;
import me.zxoir.redstonepvp.RedstonePvp;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
    private static final ThreadLocalRandom random = ThreadLocalRandom.current();

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

    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void sendActionText(Player player, String message) {
        runTaskAsync(() -> {
            /*PlayerProfile user = PlayerProfileManager.getPlayerProfile(player.getUniqueId());
            user.setActionbar(true);
            Bukkit.getScheduler().runTaskLaterAsynchronously(mainInstance, () -> user.setActionbar(false), 60);*/

            PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(colorize(message)), (byte) 2);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        });
    }

    public static boolean isAirOrNull(ItemStack item) {
        return item == null || item.getType().equals(Material.AIR);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean rollPercentage(double percentage) {
        if (percentage < 0.0 || percentage > 100.0) {
            throw new IllegalArgumentException("Percentage must be between 0.0 and 100.0");
        }

        // Generate a random number between 0.0 (inclusive) and 100.0 (exclusive)
        double randomNumber = random.nextDouble() * 100.0;

        // Check if the generated random number is less than the specified percentage
        return randomNumber < percentage;
    }

    @NotNull
    public static String colorize(String arg) {
        return ChatColor.translateAlternateColorCodes('&', arg);
    }

    public interface Task {
        void execute();
    }
}
