package me.zxoir.redstonepvp.listeners;

import me.zxoir.redstonepvp.RedstonePvp;
import me.zxoir.redstonepvp.enchants.PoisonEnchantment;
import me.zxoir.redstonepvp.util.CommonUtils;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

public class PoisonListener implements Listener {
    private static final RedstonePvp mainInstance = RedstonePvp.getPlugin(RedstonePvp.class);

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

        applyPoisonEffect(target, enchantmentLevel);
    }

    @EventHandler(ignoreCancelled = true)
    public void onShootBow(@NotNull EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        Player shooter = (Player) event.getEntity();
        if (!(event.getProjectile() instanceof Arrow))
            return;

        Arrow arrow = (Arrow) event.getProjectile();
        if (event.getBow().getEnchantments().isEmpty())
            return;

        arrow.setMetadata("enchant", new FixedMetadataValue(mainInstance, event.getBow().getEnchantments()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onBowDamage(@NotNull EntityDamageByEntityEvent event) {
        if (event.getFinalDamage() == 0)
            return;

        if (event.getCause() != EntityDamageEvent.DamageCause.PROJECTILE)
            return;

        if (event.getDamager().getType() != EntityType.ARROW)
            return;

        Arrow arrow = (Arrow) event.getDamager();
        if (!(arrow.getShooter() instanceof Player))
            return;

        if (!event.getEntityType().equals(EntityType.PLAYER))
            return;

        if (arrow.getMetadata("enchant").isEmpty())
            return;

        if (!arrow.getMetadata("enchant").get(0).asString().toLowerCase().contains("poison"))
            return;

        Player attacker = (Player) arrow.getShooter();
        Player target = (Player) event.getEntity();

        if (target.getGameMode() == GameMode.CREATIVE || attacker.getGameMode() == GameMode.CREATIVE)
            return;

        ItemStack itemStack = attacker.getItemInHand();
        int enchantmentLevel = itemStack.getEnchantmentLevel(RedstonePvp.getPoisonEnchantment());
        if (!CommonUtils.rollPercentage(PoisonEnchantment.getPoisonChance(enchantmentLevel)))
            return;

        applyPoisonEffect(target, enchantmentLevel);
    }

    private void applyPoisonEffect(Player player, int level) {
        player.addPotionEffect(PoisonEnchantment.getPoisonEffect(level));
        player.spigot().playEffect(player.getLocation().add(0.5, 0, 0.5), Effect.POTION_BREAK, 11, 1, 1, 1, 1, 1, 1, 1);
    }
}