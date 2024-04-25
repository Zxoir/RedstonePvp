package me.zxoir.redstonepvp.listeners;

import me.zxoir.redstonepvp.RedstonePvp;
import me.zxoir.redstonepvp.commands.ItemFrameClickerCommand;
import me.zxoir.redstonepvp.util.ItemStackBuilder;
import me.zxoir.redstonepvp.util.TimeManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ItemFrameClickerListener implements Listener {
    private static final HashMap<UUID, HashMap<ItemFrame, Long>> playerItemFrameCooldowns = new HashMap<>(); // TODO: Save on disable

    @EventHandler
    public void test(EntityInteractEvent event) {
        Bukkit.broadcastMessage("test");
    }

    @EventHandler()
    public void onItemFrameAdd(@NotNull PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof ItemFrame))
            return;

        ItemFrame itemFrame = (ItemFrame) event.getRightClicked();
        Player player = event.getPlayer();
        ItemStack itemStack = player.getItemInHand();
        ItemStack itemStackOnFrame = itemFrame.getItem();

        if (itemStack == null || itemStack.getType() == Material.AIR || itemStackOnFrame == null || itemStackOnFrame.getType() != Material.AIR)
            return;

        event.setCancelled(true);
        itemFrame.setItem(new ItemStackBuilder(itemStack.clone()).withLore("Amount: " + itemStack.getAmount()).build());
        player.sendMessage("Itemframe item set!");
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onItemFrameModify(@NotNull PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof ItemFrame))
            return;

        ItemFrame itemFrame = (ItemFrame) event.getRightClicked();
        ItemStack itemStack = itemFrame.getItem();
        Player player = event.getPlayer();

        if (itemStack == null || itemStack.getType() == Material.AIR)
            return;

        if (!ItemFrameClickerCommand.getPendingItemFrameModification().containsKey(player.getUniqueId()))
            return;

        Object[] objects = ItemFrameClickerCommand.getPendingItemFrameModification().get(player.getUniqueId());
        String input = (String) objects[0];
        BukkitTask bukkitTask = (BukkitTask) objects[1];
        ItemFrameClickerCommand.getPendingItemFrameModification().remove(player.getUniqueId());
        event.setCancelled(true);
        bukkitTask.cancel();

        if (input.equalsIgnoreCase("enable") || input.equalsIgnoreCase("0")) {
            ItemFrameClickerCommand.getItemFrameCooldowns().remove(itemFrame);
            player.sendMessage("That Itemframe has been enabled.");
            return;
        }

        if (input.equalsIgnoreCase("disable")) {
            ItemFrameClickerCommand.getItemFrameCooldowns().put(itemFrame, -1L);
            player.sendMessage("That Itemframe has been disabled.");
            return;
        }

        long cooldown = TimeManager.toMillisecond(input);
        if (cooldown == -1 || cooldown == 0) {
            player.sendMessage("Invalid cooldown input.");
            return;
        }

        ItemFrameClickerCommand.getItemFrameCooldowns().put(itemFrame, cooldown);
        player.sendMessage("A cooldown has been set for that ItemFrame.");
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractEntity(@NotNull PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof ItemFrame))
            return;

        ItemFrame itemFrame = (ItemFrame) event.getRightClicked();
        ItemStack itemStack = itemFrame.getItem();

        if (itemStack == null || itemStack.getType() == Material.AIR)
            return;

        Player player = event.getPlayer();
        boolean isCooldown = ItemFrameClickerCommand.getItemFrameCooldowns().containsKey(itemFrame);

        if (isCooldown && ItemFrameClickerCommand.getItemFrameCooldowns().get(itemFrame) == -1L)
            return;

        event.setCancelled(true);

        if (isOnCooldown(player, itemFrame))
            return;

        if (isCooldown) {
            long cooldown = ItemFrameClickerCommand.getItemFrameCooldowns().getOrDefault(itemFrame, 0L);
            playerItemFrameCooldowns.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>()).put(itemFrame, System.currentTimeMillis() + cooldown);
            BukkitTask cooldownRemovalTask = Bukkit.getScheduler().runTaskLater(
                    RedstonePvp.getPlugin(RedstonePvp.class),
                    () -> {
                        HashMap<ItemFrame, Long> cooldowns = playerItemFrameCooldowns.get(player.getUniqueId());
                        cooldowns.remove(itemFrame);
                        if (cooldowns.isEmpty()) {
                            playerItemFrameCooldowns.remove(player.getUniqueId());
                        }
                    },
                    cooldown / 50
            );
        }

        ItemStack clonedItemStack = itemStack.clone();
        setItemstackAmount(clonedItemStack);
        player.getInventory().addItem(clonedItemStack);
        player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 10, 1);
    }

    private void setItemstackAmount(@NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = itemMeta.getLore();
        List<String> loreToRemove = new ArrayList<>();

        for (String line : lore) {
            String strippedLine = ChatColor.stripColor(line);
            if (!strippedLine.startsWith("Amount: "))
                continue;

            loreToRemove.add(line);
            int amount = Integer.parseInt(strippedLine.replace("Amount: ", ""));
            itemStack.setAmount(amount);
            break;
        }

        loreToRemove.forEach(lore::remove);
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
    }

    private boolean containsFrameLore(ItemStack itemStack) {
        if (itemStack == null || itemStack.getItemMeta() == null || itemStack.getItemMeta().getLore() == null || itemStack.getItemMeta().getLore().isEmpty())
            return false;
        return true;
    }

    private boolean isOnCooldown(@NotNull Player player, ItemFrame itemFrame) {
        if (playerItemFrameCooldowns.containsKey(player.getUniqueId())) {
            HashMap<ItemFrame, Long> cooldowns = playerItemFrameCooldowns.get(player.getUniqueId());
            Long cooldownEnd = cooldowns.get(itemFrame);
            if (cooldownEnd != null && System.currentTimeMillis() < cooldownEnd) {
                long timeLeft = (cooldownEnd - System.currentTimeMillis());
                player.sendMessage("You can use this item frame again in " + TimeManager.formatTime(timeLeft, false, true));
                return true;
            }
        }
        return false;
    }
}