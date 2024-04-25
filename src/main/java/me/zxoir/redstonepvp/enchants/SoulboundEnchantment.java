package me.zxoir.redstonepvp.enchants;

import me.zxoir.redstonepvp.RedstonePvp;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 4/25/2024
 */
public class SoulboundEnchantment extends Enchantment {
    public SoulboundEnchantment(int id) {
        super(id);
    }

    public static void applyDisplayName(@NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null)
            return;

        String name = ChatColor.DARK_PURPLE + "Soulbound";
        if (itemMeta.getLore() == null) {
            itemMeta.setLore(Collections.singletonList(name));
        } else {
            List<String> lore = itemMeta.getLore();
            lore.add(name);
            itemMeta.setLore(lore);
        }

        itemStack.setItemMeta(itemMeta);
    }

    public static boolean isSoulbound(ItemStack itemStack) {
        return itemStack != null && itemStack.containsEnchantment(RedstonePvp.getSoulboundEnchantment());
    }

    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        return true;
    }

    @Override
    public boolean conflictsWith(Enchantment enchantment) {
        return false;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.ALL;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public String getName() {
        return ChatColor.DARK_PURPLE + "Soulbound";
    }

    @Override
    public int getStartLevel() {
        return 1;
    }
}
