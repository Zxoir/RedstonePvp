package me.zxoir.redstonepvp.enchants;

import me.zxoir.redstonepvp.RedstonePvp;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 4/29/2024
 */
public class WitherEnchantment extends Enchantment {
    private static final int WITHER_DURATION_LEVEL_ONE = 20*5;
    private static final int WITHER_AMPLIFIER_LEVEL_ONE = 0;
    private static final int WITHER_DURATION_LEVEL_TWO = 20*5;
    private static final int WITHER_AMPLIFIER_LEVEL_TWO = 1;

    public WitherEnchantment(int id) {
        super(id);
    }

    public static void applyDisplayName(@NotNull ItemStack itemStack, int level) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null)
            return;

        String name = ChatColor.RESET + "Wither " + (level == 1 ? "I" : "II");
        if (itemMeta.getLore() == null) {
            itemMeta.setLore(Collections.singletonList(name));
        } else {
            List<String> lore = itemMeta.getLore();
            lore.add(name);
            itemMeta.setLore(lore);
        }

        itemStack.setItemMeta(itemMeta);
    }

    public static @NotNull PotionEffect getWitherEffect(int level) {
        return new PotionEffect(PotionEffectType.WITHER, level == 1 ? WITHER_DURATION_LEVEL_ONE : WITHER_DURATION_LEVEL_TWO, level == 1 ? WITHER_AMPLIFIER_LEVEL_ONE : WITHER_AMPLIFIER_LEVEL_TWO);
    }

    public static double getWitherChance(int level) {
        return level == 1 ? 25 : 45;
    }

    public static boolean isWither(ItemStack itemStack) {
        return itemStack != null && itemStack.containsEnchantment(RedstonePvp.getWitherEnchantment());
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
        return "Wither";
    }

    @Override
    public int getStartLevel() {
        return 1;
    }
}
