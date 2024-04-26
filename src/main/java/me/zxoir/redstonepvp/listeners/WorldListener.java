package me.zxoir.redstonepvp.listeners;

import me.zxoir.redstonepvp.RedstonePvp;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 4/19/2024
 */
public class WorldListener implements Listener {
    private static final RedstonePvp mainInstance = RedstonePvp.getPlugin(RedstonePvp.class);

    public WorldListener() {
        World world = Bukkit.getWorld("world");
        world.setDifficulty(Difficulty.NORMAL);
        world.setTime(1000);
        world.setThundering(false);
        world.setStorm(false);
        world.setThunderDuration(1);

        setWorldTimer(world);
    }

    private void setWorldTimer(World world) {
        Bukkit.getScheduler().runTaskTimer(mainInstance, () -> world.setTime(1000), 0, 6000L);
    }

    @EventHandler
    public void onWeatherChange(@NotNull WeatherChangeEvent event) {
        event.setCancelled(event.toWeatherState());
        event.getWorld().setTime(1000);
        event.getWorld().setThundering(false);
        event.getWorld().setThunderDuration(1);
    }

    @EventHandler(ignoreCancelled = true)
    public void onArrowSelf(@NotNull EntityDamageByEntityEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.PROJECTILE)
            return;

        if (event.getDamager().getType() != EntityType.ARROW)
            return;

        Arrow arrow = (Arrow) event.getDamager();
        if (!(arrow.getShooter() instanceof Player))
            return;

        if (!event.getEntityType().equals(EntityType.PLAYER))
            return;

        Player shooter = (Player) arrow.getShooter();
        Player damaged = (Player) event.getEntity();

        if (!shooter.equals(damaged))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void arrowEvent(@NotNull ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getEntity();
            arrow.remove();
        }
    }

    @EventHandler
    public void onCraft(@NotNull PrepareItemCraftEvent event) {
        event.getInventory().setResult(new ItemStack(Material.AIR));
    }
}
