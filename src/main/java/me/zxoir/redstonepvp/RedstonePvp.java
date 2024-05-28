package me.zxoir.redstonepvp;

import lombok.Getter;
import me.zxoir.redstonepvp.commands.ItemFrameClickerCommand;
import me.zxoir.redstonepvp.commands.TrashCommand;
import me.zxoir.redstonepvp.database.RedstoneDatabase;
import me.zxoir.redstonepvp.enchants.PoisonEnchantment;
import me.zxoir.redstonepvp.enchants.SoulboundEnchantment;
import me.zxoir.redstonepvp.listeners.*;
import me.zxoir.redstonepvp.util.EnchantmentUtil;
import me.zxoir.redstonepvp.util.ProfileBatchSaveTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 4/17/2024
 */
public final class RedstonePvp extends JavaPlugin {
    @Getter
    private static final Logger LOGGER = LogManager.getLogger("RedstonePvp");
    @Getter
    private long cookieCooldown;
    @Getter
    private static Enchantment soulboundEnchantment;
    @Getter
    private static Enchantment poisonEnchantment;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        RedstoneDatabase.createTable("CREATE TABLE IF NOT EXISTS redstoneprofiles(" +
                "uuid VARCHAR(36) PRIMARY KEY NOT NULL," +
                "friends TEXT," +
                "firstJoinDate TEXT," +
                "enderchest TEXT," +
                "alts TEXT," +
                "points INT," +
                "kills INT," +
                "deaths INT," +
                "kisses INT," +
                "logins INT," +
                "playtime TEXT" +
                ");");

        soulboundEnchantment = new SoulboundEnchantment(100);
        poisonEnchantment = new PoisonEnchantment(100);
        EnchantmentUtil.registerCustomEnchantment(soulboundEnchantment);
        EnchantmentUtil.registerCustomEnchantment(poisonEnchantment);

        registerListeners();
        registerCommand();
        loadConfigValues();

        int saveIntervalMinutes = 5;
        new ProfileBatchSaveTask().runTaskTimerAsynchronously(this, 0, 20 * 60 * saveIntervalMinutes);
    }

    private void loadConfigValues() {
        this.cookieCooldown = this.getConfig().getLong("cookie-cooldown", 5000L);
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new AFKListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerProfileListener(), this);
        getServer().getPluginManager().registerEvents(new WorldListener(), this);
        getServer().getPluginManager().registerEvents(new CookiesListener(), this);
        getServer().getPluginManager().registerEvents(new ItemFrameClickerListener(), this);
        getServer().getPluginManager().registerEvents(new XpOnHitListener(), this);
        getServer().getPluginManager().registerEvents(new SoulboundListener(), this);
        getServer().getPluginManager().registerEvents(new CombatLogListener(), this);
        getServer().getPluginManager().registerEvents(new PoisonListener(), this);
        getServer().getPluginManager().registerEvents(new CommonItemRemoverListener(), this);
        getServer().getPluginManager().registerEvents(new EnchantLimiterListener(), this);
        getServer().getPluginManager().registerEvents(new AbsorptionRemover(), this);
        getServer().getPluginManager().registerEvents(new BottleRemover(), this);
        getServer().getPluginManager().registerEvents(new AutoEnchantLapizPlacer(), this);
    }

    private void registerCommand() {
        getServer().getPluginCommand("itemframeclicker").setExecutor(new ItemFrameClickerCommand());
        getServer().getPluginCommand("trash").setExecutor(new TrashCommand());
    }

    @Override
    public void onDisable() {
        RedstoneDatabase.getDataSource().close();
    }
}