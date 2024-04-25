package me.zxoir.redstonepvp.database;

import me.zxoir.redstonepvp.RedstonePvp;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 4/17/2024
 */
public class DataFile {
    private FileConfiguration playerscfg;
    private File playersfile;
    private static final RedstonePvp mainInstance = RedstonePvp.getPlugin(RedstonePvp.class);

    public void setup() {
        if (!mainInstance.getDataFolder().exists())
            mainInstance.getDataFolder().mkdir();

        File dataFile = new File(mainInstance.getDataFolder() + File.separator + "Data" + File.separator);
        this.playersfile = new File(dataFile.getPath(), "DataFile.yml");

        if (!dataFile.exists()) {
            try {
                dataFile.mkdirs();
                RedstonePvp.getLOGGER().info("the Data folder has been created!");
            } catch (SecurityException e) {
                RedstonePvp.getLOGGER().info("Could not create the Data folder");
            }
        }

        if (!this.playersfile.exists()) {
            try {
                this.playersfile.createNewFile();
                RedstonePvp.getLOGGER().info("the DataFile.yml file has been created!");
            } catch (IOException e) {
                RedstonePvp.getLOGGER().info("Could not create the DataFile.yml file");
            }
        }

        this.playerscfg = YamlConfiguration.loadConfiguration(this.playersfile);
    }

    public FileConfiguration getConfig() {
        return this.playerscfg;
    }

    public void saveConfig() {
        try {
            this.playerscfg.save(this.playersfile);
        } catch (IOException localIOException) {
            RedstonePvp.getLOGGER().info("Could not save the DataFile.yml file");
        }
    }

    public void reloadConfig() {
        this.playerscfg = YamlConfiguration.loadConfiguration(this.playersfile);
    }
}
