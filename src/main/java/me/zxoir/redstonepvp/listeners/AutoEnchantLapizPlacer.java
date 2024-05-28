package me.zxoir.redstonepvp.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;

public class AutoEnchantLapizPlacer implements Listener {
    private final ItemStack lapiz = new ItemStack(Material.INK_SACK, 64, (short) 4);

    @EventHandler
    public void onEnchant(InventoryOpenEvent event) {
        if (event.getInventory() == null)
            return;

        if (!event.getInventory().getType().equals(InventoryType.ENCHANTING))
            return;

        EnchantingInventory enchantingInventory = (EnchantingInventory) event.getInventory();
        enchantingInventory.setSecondary(lapiz);
    }

    @EventHandler
    public void onInteract(InventoryClickEvent event) {
        if (event.getInventory() == null)
            return;

        if (!event.getInventory().getType().equals(InventoryType.ENCHANTING))
            return;

        if (event.getCurrentItem() == null)
            return;

        if (!event.getCurrentItem().getType().equals(lapiz.getType()))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onEnchant(InventoryCloseEvent event) {
        if (event.getInventory() == null)
            return;

        if (!event.getInventory().getType().equals(InventoryType.ENCHANTING))
            return;

        EnchantingInventory enchantingInventory = (EnchantingInventory) event.getInventory();
        enchantingInventory.setSecondary(null);
    }
}
