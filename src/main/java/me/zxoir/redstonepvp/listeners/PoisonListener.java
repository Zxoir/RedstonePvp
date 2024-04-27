package me.zxoir.redstonepvp.listeners;

import me.zxoir.redstonepvp.RedstonePvp;
import me.zxoir.redstonepvp.enchants.PoisonEnchantment;
import me.zxoir.redstonepvp.util.CommonUtils;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PoisonListener implements Listener {
    private static final double POISON_CHANCE = 45;
    private static final int POISON_DURATION = 20*3;
    private static final int POISON_AMPLIFIER = 2;

    @EventHandler(ignoreCancelled = true)
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (!event.getEntity().getType().equals(EntityType.PLAYER) || !event.getDamager().getType().equals(EntityType.PLAYER) || event.getEntity().equals(event.getDamager()))
            return;

        Player target = (Player) event.getEntity();
        Player attacker = (Player) event.getDamager();

        if (event.getFinalDamage() == 0)
            return;

        ItemStack itemStack = attacker.getItemInHand();
        if (!PoisonEnchantment.isPoison(itemStack))
            return;

        int enchantmentLevel = itemStack.getEnchantmentLevel(RedstonePvp.getPoisonEnchantment());
        if (!CommonUtils.rollPercentage(PoisonEnchantment.getPoisonChance(enchantmentLevel)))
            return;

        target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, POISON_DURATION, POISON_AMPLIFIER));
    }
}
