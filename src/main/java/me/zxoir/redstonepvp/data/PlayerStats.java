package me.zxoir.redstonepvp.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 4/17/2024
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerStats {
    private int points;
    private int kills;
    private int deaths;
    private int kisses;
    private int logins;

    public synchronized double getKDA() {
        if (deaths == 0) {
            return kills;
        } else {
            return (double) kills / deaths;
        }
    }

    public synchronized void updatePoints(int delta) {
        this.points += delta;
    }

    public synchronized void updateKills(int delta) {
        this.kills += delta;
    }

    public synchronized void updateDeaths(int delta) {
        this.deaths += delta;
    }

    public synchronized void updateKisses(int delta) {
        this.kisses += delta;
    }

    public synchronized void updateLogins(int delta) {
        this.logins += delta;
    }
}