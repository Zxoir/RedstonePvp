package me.zxoir.redstonepvp.commands;

import me.zxoir.redstonepvp.RedstonePvp;
import me.zxoir.redstonepvp.data.PlayerProfile;
import me.zxoir.redstonepvp.managers.PlayerProfileManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.*;

import static me.zxoir.redstonepvp.util.CommonUtils.colorize;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 5/29/2024
 */
public class FriendCommand implements CommandExecutor {
    private final HashMap<UUID, Set<UUID>> friendRequests = new HashMap<>();

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof ConsoleCommandSender)
            return true;

        Player player = (Player) sender;

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                PlayerProfile profile = PlayerProfileManager.getPlayerProfile(player.getUniqueId());
                if (profile.getFriends().isEmpty()) {
                    player.sendMessage(colorize("&cYou don't have any friends :c"));
                    return true;
                }

                List<String> friends = new ArrayList<>();
                profile.getFriends().forEach(uuid -> friends.add(Bukkit.getOfflinePlayer(uuid).isOnline() ? colorize("&a" + Bukkit.getOfflinePlayer(uuid).getName()) : colorize("&c" + Bukkit.getOfflinePlayer(uuid).getName())));
                player.sendMessage(colorize("&eYour friends are: &9" + StringUtils.join(friends, ", ") + "."));
                return true;
            }
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                Player friend = Bukkit.getPlayer(args[1]);

                if (friend == null) {
                    player.sendMessage(colorize("&cThat player is not online"));
                    return true;
                }

                PlayerProfile profile = PlayerProfileManager.getPlayerProfile(player.getUniqueId());
                if (profile.getFriends().contains(friend.getUniqueId())) {
                    player.sendMessage(colorize("&cYou're already friends with that player"));
                    return true;
                }

                if (containsFriendRequest(friend.getUniqueId(), player.getUniqueId())) {
                    player.sendMessage(colorize("&cYou have already sent a friend request to that player"));
                    return true;
                }

                addFriendRequest(player.getUniqueId(), friend.getUniqueId());
                player.sendMessage(colorize("&eYou have sent a friend request to " + friend.getName() + " that will expire in 5 minutes"));

                TextComponent message = new TextComponent(colorize("&a\n&e" + player.getName() + " has sent you a friend request "));
                TextComponent acceptButton = new TextComponent("&8[&a&lACCEPT&8] ");
                TextComponent denyButton = new TextComponent("&8[&c&lDENY&8]");
                acceptButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(colorize("&7Click to accept")).create()));
                acceptButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend accept " + player.getName()));
                acceptButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(colorize("&7Click to deny")).create()));
                acceptButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend deny " + player.getName()));
                player.spigot().sendMessage(message, acceptButton, denyButton);

                Bukkit.getScheduler().runTaskLater(RedstonePvp.getPlugin(RedstonePvp.class), () -> removeFriendRequest(player.getUniqueId(), friend.getUniqueId()), 20*60*5);
                return true;
            }

            if (args[0].equalsIgnoreCase("remove")) {
                OfflinePlayer friend = Bukkit.getPlayer(args[1]);
                PlayerProfile profile = PlayerProfileManager.getPlayerProfile(player.getUniqueId());

                if (!profile.getFriends().contains(friend.getUniqueId())) {
                    player.sendMessage(colorize("&cYou're not friends with that player"));
                    return true;
                }

                profile.removeFriend(friend.getUniqueId());
                PlayerProfile friendProfile = PlayerProfileManager.getPlayerProfile(friend.getUniqueId());
                friendProfile.removeFriend(player.getUniqueId());
                player.sendMessage(colorize("&e" + friend.getName() + " is no longer your friend"));
                return true;
            }

            if (args[0].equalsIgnoreCase("accept")) {
                OfflinePlayer friend = Bukkit.getOfflinePlayer(args[1]);

                if (!containsFriendRequest(player.getUniqueId(), friend.getUniqueId())) {
                    player.sendMessage(colorize("&cYou don't have a friend request from that player"));
                    return true;
                }

                removeFriendRequest(friend.getUniqueId(), player.getUniqueId());
                PlayerProfile profile = PlayerProfileManager.getPlayerProfile(player.getUniqueId());
                PlayerProfile friendProfile = PlayerProfileManager.getPlayerProfile(friend.getUniqueId());

                profile.addFriend(friend.getUniqueId());
                friendProfile.addFriend(player.getUniqueId());

                player.sendMessage(colorize("&eYou have accepted " + friend.getName() + "'s friend request"));

                if (friend.isOnline())
                    friend.getPlayer().sendMessage(colorize("&e" + player.getName() + " has accepted your friend request"));

                return true;
            }

            if (args[0].equalsIgnoreCase("deny")) {
                OfflinePlayer friend = Bukkit.getOfflinePlayer(args[1]);

                if (!containsFriendRequest(player.getUniqueId(), friend.getUniqueId())) {
                    player.sendMessage(colorize("&cYou don't have a friend request from that player"));
                    return true;
                }

                removeFriendRequest(friend.getUniqueId(), player.getUniqueId());
                player.sendMessage(colorize("&eYou have denied " + friend.getName() + "'s friend request"));
                return true;
            }
        }

        player.sendMessage(colorize("&a\n&e&lFriend Help\n&7- /friend add <Player>\n&7/friend remove <Player>\n&7/friend accept <Player>\n&7/friend deny <Player>\n&7/friend list\n"));
        return true;
    }

    private boolean containsFriendRequest(UUID player, UUID friend) {
        return friendRequests.containsKey(player) && friendRequests.get(player).contains(friend);
    }

    private void addFriendRequest(UUID player, UUID friend) {
        if (!friendRequests.containsKey(friend))
            friendRequests.put(friend, new HashSet<>());

        Set<UUID> requestList = friendRequests.get(friend);
        requestList.add(player);
        friendRequests.put(friend, requestList);
    }

    private void removeFriendRequest(UUID player, UUID friend) {
        if (!friendRequests.containsKey(friend))
            return;

        Set<UUID> requestList = friendRequests.get(friend);

        if (requestList.size() <= 1) {
            friendRequests.remove(friend);
            return;
        }

        requestList.remove(player);
        friendRequests.put(friend, requestList);
    }
}
