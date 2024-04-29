package me.zxoir.redstonepvp.listeners;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 4/29/2024
 */
public class EnchantLimiterListener implements Listener {
    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        Player player = event.getEnchanter();
        ItemStack item = event.getItem();
        HashSet<Enchantment> enchantmentsToModify = new HashSet<>();

        for (Enchantment enchantment : event.getEnchantsToAdd().keySet()) {
            if ((enchantment.getName().equalsIgnoreCase("ARROW_KNOCKBACK") || enchantment.getName().equalsIgnoreCase("KNOCKBACK")) && event.getEnchantsToAdd().get(enchantment) >= 2)
                enchantmentsToModify.add(enchantment);
        }

        enchantmentsToModify.forEach(enchantment -> {
            event.getEnchantsToAdd().remove(enchantment);
            event.getEnchantsToAdd().put(enchantment, 1);
        });
    }
}
