package me.zxoir.redstonepvp.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.zxoir.redstonepvp.RedstonePvp;
import me.zxoir.redstonepvp.data.PlayerProfile;
import me.zxoir.redstonepvp.data.PlayerStats;
import me.zxoir.redstonepvp.database.RedstoneDatabase;
import me.zxoir.redstonepvp.util.FriendSetSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 4/18/2024
 */
@SuppressWarnings("UnusedReturnValue")
public class PlayerProfileDatabaseManager {
    private static final Gson adapter = new GsonBuilder().serializeNulls().create();

    public static @NotNull ConcurrentLinkedQueue<PlayerProfile> getAllPlayerProfiles() {
        ConcurrentLinkedQueue<PlayerProfile> users = new ConcurrentLinkedQueue<>();
        long start = System.currentTimeMillis();

        try {
            RedstoneDatabase.execute(conn -> {
                PreparedStatement statement = conn.prepareStatement("SELECT * FROM users");
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    users.add(resultSetToPlayerProfile(resultSet));
                }

            }).get();
        } catch (InterruptedException | ExecutionException e) {
            if (e instanceof InterruptedException) {
                e.printStackTrace();
                throw new IllegalThreadStateException("ERROR: Thread was interrupted during execution! Code: PPDM_GPP'S.01");
            } else {
                e.printStackTrace();
                throw new IllegalThreadStateException("ERROR: Execution was aborted during execution! Code: PPDM_GPP'S.02");
            }
        }

        double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
        RedstonePvp.getLOGGER().debug("Fetched Player Profiles from DB in " + finish + " seconds.");

        return users;
    }

    public static @NotNull CompletableFuture<Void> batchUpdatePlayerProfiles(List<PlayerProfile> profiles) {
        return CompletableFuture.runAsync(() -> {
            long start = System.currentTimeMillis();

            try (Connection conn = RedstoneDatabase.getDataSource().getConnection()) {
                PreparedStatement statement = conn.prepareStatement("REPLACE INTO users VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

                for (PlayerProfile profile : profiles) {
                    statement.setString(1, profile.getUuid().toString());
                    statement.setString(2, FriendSetSerializer.serialize(profile.getFriends()));
                    statement.setString(3, profile.getDateJoined());
                    statement.setString(4, "");
                    statement.setString(5, "");
                    statement.setInt(6, profile.getStats().getPoints());
                    statement.setInt(7, profile.getStats().getKills());
                    statement.setInt(8, profile.getStats().getDeaths());
                    statement.setInt(9, profile.getStats().getKisses());
                    statement.setInt(10, profile.getStats().getLogins());
                    statement.setString(11, "");

                    statement.addBatch();
                }

                statement.executeBatch();

                double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
                //RedstonePvp.getPluginLogger().debug("Saved Player Profile ('" + user.getUuid() + "') to DB in " + finish + " seconds.");
                RedstonePvp.getLOGGER().debug("Batch updated player profiles to DB in {} seconds.", finish);
            } catch (SQLException e) {
                throw new IllegalStateException("Error while batch updating player profiles in the database", e);
            }
        });
    }

    public static @NotNull PlayerProfile getPlayerProfile(@NotNull UUID uuid) {

        AtomicReference<PlayerProfile> user = new AtomicReference<>(null);
        long start = System.currentTimeMillis();

        try {
            RedstoneDatabase.execute(conn -> {
                PreparedStatement statement = conn.prepareStatement("SELECT * FROM redstoneprofiles WHERE uuid = ? LIMIT 1");
                statement.setString(1, uuid.toString());
                ResultSet resultSet = statement.executeQuery();

                if (!resultSet.next())
                    return;

                user.set(resultSetToPlayerProfile(resultSet));

            }).get();
        } catch (InterruptedException | ExecutionException e) {
            if (e instanceof InterruptedException) {
                e.printStackTrace();
                throw new IllegalThreadStateException("ERROR: Thread was interrupted during execution! Code: PPDM_GPP.01");
            } else {
                e.printStackTrace();
                throw new IllegalThreadStateException("ERROR: Execution was aborted during execution! Code: PPDM_GPP.02");
            }
        }

        double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
        PlayerProfile playerProfile = user.get();
        RedstonePvp.getLOGGER().debug("Fetched Player Profile ('" + playerProfile.getUuid() + "') from DB in " + finish + " seconds.");

        return playerProfile;
    }

    public static @NotNull CompletableFuture<Void> savePlayerProfile(@NotNull PlayerProfile user) {
        return RedstoneDatabase.execute(conn -> {
            long start = System.currentTimeMillis();

            PreparedStatement statement = conn.prepareStatement("INSERT INTO redstoneprofiles VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            statement.setString(1, user.getUuid().toString());
            statement.setString(2, FriendSetSerializer.serialize(user.getFriends()));
            statement.setString(3, user.getDateJoined());
            statement.setString(4, "");
            statement.setString(5, "");
            statement.setInt(6, user.getStats().getPoints());
            statement.setInt(7, user.getStats().getKills());
            statement.setInt(8, user.getStats().getDeaths());
            statement.setInt(9, user.getStats().getKisses());
            statement.setInt(10, user.getStats().getLogins());
            statement.setString(11, "");
            statement.execute();

            double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
            //RedstonePvp.getPluginLogger().debug("Saved Player Profile ('" + user.getUuid() + "') to DB in " + finish + " seconds.");
            RedstonePvp.getLOGGER().debug("Saved Player Profile ('" + user.getUuid() + "') to DB in " + finish + " seconds.");
        });
    }

    public static @NotNull CompletableFuture<Void> updatePlayerProfile(@NotNull PlayerProfile user) {
        return RedstoneDatabase.execute(conn -> {
            long start = System.currentTimeMillis();

            PreparedStatement statement = conn.prepareStatement(
                    "UPDATE redstoneprofiles SET friends = ?, enderchest = ?, alts = ?, points = ?, kills = ?, deaths = ?, kisses = ?, logins = ?, playtime = ? WHERE uuid = ?");

            statement.setString(1, FriendSetSerializer.serialize(user.getFriends()));

            statement.setString(2, "");

            statement.setString(3, "");

            statement.setInt(4, user.getStats().getPoints());

            statement.setInt(5, user.getStats().getKills());

            statement.setInt(6, user.getStats().getDeaths());

            statement.setInt(7, user.getStats().getKisses());

            statement.setInt(8, user.getStats().getLogins());

            statement.setString(9, "");

            statement.setString(10, user.getUuid().toString());

            statement.execute();

            double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
            //RedstonePvp.getPluginLogger().debug("Updated Player Profile ('" + user.getUuid() + "') from DB in " + finish + " seconds."); //todo: remove
            RedstonePvp.getLOGGER().debug("Updated Player Profile ('" + user.getUuid() + "') from DB in " + finish + " seconds.");
        });
    }

    public static @NotNull CompletableFuture<Void> updateFriends(@NotNull PlayerProfile user) {
        return RedstoneDatabase.execute(conn -> {
            try {
                PreparedStatement statement = conn.prepareStatement("UPDATE redstoneprofiles SET friends = ? WHERE uuid = ?");
                statement.setString(1, FriendSetSerializer.serialize(user.getFriends()));
                statement.setString(2, user.getUuid().toString());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new IllegalStateException("ERROR: Failed to update friends for user '" + user.getUuid().toString() + "' in the database.");
            }
        });
    }

    public static @NotNull CompletableFuture<Boolean> isPlayerInDatabase(UUID uuid) {
        return RedstoneDatabase.execute(conn -> {
            PreparedStatement statement = conn.prepareStatement("SELECT COUNT(*) FROM redstoneprofiles WHERE uuid = ?");
            statement.setString(1, uuid.toString());
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            return count > 0;
        });
    }

    private static @NotNull PlayerProfile resultSetToPlayerProfile(@NotNull ResultSet resultSet) {
        try {
            UUID uuid = UUID.fromString(resultSet.getString("uuid"));
            Set<UUID> friends = FriendSetSerializer.deserialize(resultSet.getString("friends"));
            String firstJoinDate = resultSet.getString("firstJoinDate");
            String enderchest = resultSet.getString("enderchest");
            String alts = resultSet.getString("alts");
            int points = resultSet.getInt("points");
            int kills = resultSet.getInt("kills");
            int deaths = resultSet.getInt("deaths");
            int kisses = resultSet.getInt("kisses");
            int logins = resultSet.getInt("logins");
            String playtime = resultSet.getString("playtime");

            PlayerStats playerStats = new PlayerStats(points, kills, deaths, kisses, logins);
            return new PlayerProfile(uuid, playerStats, friends, firstJoinDate);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new IllegalThreadStateException("ERROR: Execution was aborted during execution! Code: PPDM_DTU");
        }
    }
}
