package me.zxoir.redstonepvp.managers;

import lombok.Getter;
import me.zxoir.redstonepvp.RedstonePvp;

import static me.zxoir.redstonepvp.util.CommonUtils.colorize;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 5/29/2024
 */
public class ConfigManager {
    private static final RedstonePvp main = RedstonePvp.getPlugin(RedstonePvp.class);

    @Getter
    private static String invalidPermissionMessage;

    private static void getConfigData() {
        invalidPermissionMessage = colorize(main.getConfig().getString("InvalidPermissionMessage"));
    }

    public static void setup() {
        main.saveDefaultConfig();
        getConfigData();
    }

    public static void reloadConfig() {
        main.reloadConfig();
        RedstonePvp.getDataFile().reloadConfig();
        getConfigData();
    }
}