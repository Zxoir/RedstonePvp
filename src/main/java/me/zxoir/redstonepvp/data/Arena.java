package me.zxoir.redstonepvp.data;

import lombok.Data;
import org.bukkit.Location;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 6/6/2024
 */
@Data
public class Arena {
    private String name;
    private Location pointA;
    private Location pointB;
    private Location spawn;
    private boolean isActive;

    public Arena(String name, Location pointA, Location pointB, Location spawn) {
        this.name = name;
        this.pointA = pointA;
        this.pointB = pointB;
        this.spawn = spawn;
        this.isActive = false;
    }
}
