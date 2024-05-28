package me.zxoir.redstonepvp.listeners;

import me.zxoir.redstonepvp.util.CommonUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 4/29/2024
 */
public class CommonItemRemoverListener implements Listener {
    @EventHandler
    public void onThrow(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItemDrop().getItemStack();

        if (!isCommonItem(itemStack))
            return;

        event.getItemDrop().remove();
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        event.getDrops().removeIf(this::isCommonItem);
    }

    private boolean isCommonItem(ItemStack itemStack) {
        if (CommonUtils.isAirOrNull(itemStack))
            return false;

        if (!itemStack.getEnchantments().isEmpty())
            return false;

        return itemStack.getType() == Material.ARROW || itemStack.getType() == Material.DIAMOND_BOOTS ||
                itemStack.getType() == Material.DIAMOND_LEGGINGS || itemStack.getType() == Material.DIAMOND_CHESTPLATE ||
                itemStack.getType() == Material.DIAMOND_HELMET || itemStack.getType() == Material.DIAMOND_SWORD ||
                itemStack.getType() == Material.BOW || itemStack.getType() == Material.COOKIE;
    }
}
