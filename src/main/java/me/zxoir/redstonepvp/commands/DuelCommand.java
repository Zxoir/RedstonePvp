package me.zxoir.redstonepvp.commands;

import me.zxoir.redstonepvp.RedstonePvp;
import me.zxoir.redstonepvp.data.Arena;
import me.zxoir.redstonepvp.managers.ArenaManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static me.zxoir.redstonepvp.util.CommonUtils.colorize;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 6/6/2024
 */
public class DuelCommand implements CommandExecutor, Listener {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof ConsoleCommandSender)
            return true;

        Player player = (Player) sender;
        boolean isAdmin = player.hasPermission("redstonepvp.duel.admin");

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("create")) {
                if (!isAdmin) {
                    // TODO: No permission message
                    return true;
                }

                String arenaName = args[1];
                if (ArenaManager.getArenas().containsKey(arenaName.toLowerCase())) {
                    player.sendMessage(colorize("&cThere's already an arena with that name."));
                    return true;
                }

                Arena arena = new Arena(arenaName, null, null, null);
                ArenaManager.saveArena(arena);
                player.sendMessage(colorize("&eYou have created a new arena, set up the spawn, point A, and point B locations to enable the arena"));
                return true;
            }

            if (args[0].equalsIgnoreCase("delete")) {
                if (!isAdmin) {
                    // TODO: No permission message
                    return true;
                }

                String arenaName = args[1];
                if (!ArenaManager.getArenas().containsKey(arenaName.toLowerCase())) {
                    player.sendMessage(colorize("&cThere's no arena with that name."));
                    return true;
                }

                Arena arena = ArenaManager.getArenas().get(arenaName.toLowerCase());
                ArenaManager.deleteArena(arena);
                player.sendMessage(colorize("&eYou have deleted the " + arena.getName() + " arena."));
                return true;
            }

            if (args[0].equalsIgnoreCase("accept")) {
                Player target = Bukkit.getPlayer(args[1]);

                if (target == null) {
                    player.sendMessage(colorize("&cThat player is not online."));
                    return true;
                }

                if (!containsDuelRequest(target.getUniqueId(), player.getUniqueId())) {
                    player.sendMessage(colorize("&cYou don't have a duel request from that player"));
                    return true;
                }

                Arena arena = ArenaManager.getRandomAvailableArena();
                if (arena == null) {
                    player.sendMessage(colorize("&cThere are no available duel arenas."));
                    return true;
                }

                startDuel(player, target, arena);
                return true;
            }

            if (args[0].equalsIgnoreCase("deny")) {
                Player target = Bukkit.getPlayer(args[1]);

                if (target == null) {
                    player.sendMessage(colorize("&cThat player is not online."));
                    return true;
                }

                if (!containsDuelRequest(target.getUniqueId(), player.getUniqueId())) {
                    player.sendMessage(colorize("&cYou don't have a duel request from that player"));
                    return true;
                }

                player.sendMessage(colorize("&eYou have denied " + target.getName() + "'s duel request."));
                target.sendMessage(colorize("&e" + player.getName() + " has denied your duel request"));
                return true;
            }

            if (args[0].equalsIgnoreCase("invite")) {
                Player target = Bukkit.getPlayer(args[1]);

                if (target == null) {
                    player.sendMessage(colorize("&cThat player is not online."));
                    return true;
                }

                addDuelRequest(player.getUniqueId(), target.getUniqueId());
                TextComponent message = new TextComponent(colorize("&a\n&e" + player.getName() + " has sent you a duel invite "));
                TextComponent acceptButton = new TextComponent(colorize("&8[&a&lACCEPT&8] "));
                TextComponent denyButton = new TextComponent(colorize("&8[&c&lDENY&8]"));
                acceptButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(colorize("&7Click to accept")).create()));
                acceptButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel accept " + player.getName()));
                denyButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(colorize("&7Click to deny")).create()));
                denyButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel deny " + player.getName()));
                target.spigot().sendMessage(message, acceptButton, denyButton);
                player.sendMessage(colorize("&eYou have sent a duel invite to " + target.getName()));
                return true;
            }
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("edit")) {
            String arenaName = args[1];
            if (!ArenaManager.getArenas().containsKey(arenaName.toLowerCase())) {
                player.sendMessage(colorize("&cThere's no arena with that name."));
                return true;
            }

            Arena arena = ArenaManager.getArenas().get(arenaName.toLowerCase());
            if (args[2].equalsIgnoreCase("setpointa")) {
                arena.setPointA(player.getLocation());
                ArenaManager.saveArena(arena);
                player.sendMessage(colorize("&eYou have set the point a location for arena named " + arena.getName()));
                return true;
            }

            if (args[2].equalsIgnoreCase("setpointb")) {
                arena.setPointB(player.getLocation());
                ArenaManager.saveArena(arena);
                player.sendMessage(colorize("&eYou have set the point b location for arena named " + arena.getName()));
                return true;
            }

            if (args[2].equalsIgnoreCase("setspawn")) {
                arena.setSpawn(player.getLocation());
                ArenaManager.saveArena(arena);
                player.sendMessage(colorize("&eYou have set the spawn location for arena named " + arena.getName()));
                return true;
            }
        }

        player.sendMessage(colorize("&a\n&e&lDuel Help\n" +
                "&7/duel [Player]\n" +
                "&7/duel invite <Player>\n" +
                "&7/duel accept <Player>\n" +
                "&7/duel deny <Player>\n"));

        if (isAdmin)
            player.sendMessage(colorize("&7/duel create <Arena>\n" +
                    "&7/duel delete <Arena>\n" +
                    "&7/duel edit <Arena> <setpointA/setpointB/setSpawn>\n"));
        return true;
    }

    @EventHandler
    public void onMove(@NotNull PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!ArenaManager.getDuelCountdown().contains(player.getUniqueId()))
            return;

        player.teleport(event.getFrom());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (!ArenaManager.getDuelsInProgress().containsKey(player.getUniqueId()))
            return;

        Arena arena = ArenaManager.getDuelsInProgress().get(player.getUniqueId());
        player.teleport(arena.getSpawn());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (player.getKiller() == null)
            return;

        Player killer = player.getKiller();
        if (!(ArenaManager.getDuelsInProgress().containsKey(player.getUniqueId()) && ArenaManager.getDuelsInProgress().containsKey(killer.getUniqueId())))
            return;

        endDuel(killer, player);
    }

    private void endDuel(Player player, Player target) {
        Bukkit.broadcastMessage(colorize("&e&l" + player.getName() + " &ebeat " + target.getName() + " in a duel"));
        player.sendMessage(colorize("&7You're gonna get teleported back to spawn in 60 seconds."));
        ArenaManager.getDuelsInProgress().remove(target.getUniqueId());

        Arena arena = ArenaManager.getDuelsInProgress().get(player.getUniqueId());
        Bukkit.getScheduler().runTaskLater(RedstonePvp.getPlugin(RedstonePvp.class), () -> {
            if (player.isOnline())
                player.teleport(arena.getSpawn());
            ArenaManager.getDuelsInProgress().remove(player.getUniqueId());
            arena.setActive(false);
        }, 20*60);
    }

    private void startDuel(Player player, Player target, Arena arena) {
        arena.setActive(true);
        ArenaManager.getDuelsInProgress().put(player.getUniqueId(), arena);
        ArenaManager.getDuelsInProgress().put(target.getUniqueId(), arena);
        ArenaManager.getDuelCountdown().add(player.getUniqueId());
        ArenaManager.getDuelCountdown().add(target.getUniqueId());
        player.teleport(arena.getPointA());
        target.teleport(arena.getPointB());
        RedstonePvp mainPlugin = RedstonePvp.getPlugin(RedstonePvp.class);
        timerTick(player, target, 5);
        Bukkit.getScheduler().runTaskLater(mainPlugin, () -> timerTick(player, target, 4), 20);
        Bukkit.getScheduler().runTaskLater(mainPlugin, () -> timerTick(player, target, 3), 20*2);
        Bukkit.getScheduler().runTaskLater(mainPlugin, () -> timerTick(player, target, 2), 20*3);
        Bukkit.getScheduler().runTaskLater(mainPlugin, () -> timerTick(player, target, 1), 20*4);
        Bukkit.getScheduler().runTaskLater(mainPlugin, () -> timerTick(player, target, 0), 20*5);
        Bukkit.getScheduler().runTaskLater(mainPlugin, () -> ArenaManager.getDuelCountdown().remove(player.getUniqueId()), 20*6);
        Bukkit.getScheduler().runTaskLater(mainPlugin, () -> ArenaManager.getDuelCountdown().remove(target.getUniqueId()), 20*6);
    }

    private void timerTick(Player player, Player target, int countdown) {
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
        target.playSound(target.getLocation(), Sound.NOTE_PLING, 1, 1);
        player.sendMessage(colorize("&eDuel will start in " + countdown));
        target.sendMessage(colorize("&eDuel will start in " + countdown));
    }

    private void addDuelRequest(UUID player, UUID target) {
        if (!ArenaManager.getDuelRequests().containsKey(target))
            ArenaManager.getDuelRequests().put(target, new HashSet<>());

        Set<UUID> requestList = ArenaManager.getDuelRequests().get(target);
        requestList.add(player);
        ArenaManager.getDuelRequests().put(target, requestList);
    }

    private boolean containsDuelRequest(UUID player, UUID target) {
        return ArenaManager.getDuelRequests().containsKey(target) && ArenaManager.getDuelRequests().get(target).contains(player);
    }
}
