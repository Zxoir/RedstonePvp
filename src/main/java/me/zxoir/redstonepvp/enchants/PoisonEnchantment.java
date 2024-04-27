package me.zxoir.redstonepvp.enchants;

import me.zxoir.redstonepvp.RedstonePvp;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class PoisonEnchantment extends Enchantment {
    public PoisonEnchantment(int id) {
        super(id);
    }

    public static void applyDisplayName(@NotNull ItemStack itemStack, int level) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null)
            return;

        String name = ChatColor.DARK_PURPLE + "Poison " + (level == 1 ? "I" : "II");
        if (itemMeta.getLore() == null) {
            itemMeta.setLore(Collections.singletonList(name));
        } else {
            List<String> lore = itemMeta.getLore();
            lore.add(name);
            itemMeta.setLore(lore);
        }

        itemStack.setItemMeta(itemMeta);
    }

    public static double getPoisonChance(int level) {
        return level == 1 ? 25 : 45;
    }

    public static boolean isPoison(ItemStack itemStack) {
        return itemStack != null && itemStack.containsEnchantment(RedstonePvp.getPoisonEnchantment());
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
        return 2;
    }

    @Override
    public String getName() {
        return "Poison";
    }

    @Override
    public int getStartLevel() {
        return 1;
    }
}
