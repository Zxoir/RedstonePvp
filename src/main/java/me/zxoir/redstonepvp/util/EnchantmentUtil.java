package me.zxoir.redstonepvp.util;

import org.bukkit.enchantments.Enchantment;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 4/25/2024
 */
public class EnchantmentUtil {
    @SuppressWarnings({"unchecked", "deprecation"})
    public static void registerCustomEnchantment(Enchantment enchantment) {
        try {
            Field byIdField = Enchantment.class.getDeclaredField("byId");
            byIdField.setAccessible(true);
            Map<Integer, Enchantment> byId = (Map<Integer, Enchantment>) byIdField.get(null);
            byId.put(enchantment.getId(), enchantment);

            Field byNameField = Enchantment.class.getDeclaredField("byName");
            byNameField.setAccessible(true);
            Map<String, Enchantment> byName = (Map<String, Enchantment>) byNameField.get(null);
            byName.put(enchantment.getName(), enchantment);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
