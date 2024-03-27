package me.ley.leythings;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public final class LeyThings extends JavaPlugin implements Listener {

    private Set<Player> cooldownPlayers = new HashSet<>();
    private Set<Player> teleportingPlayers = new HashSet<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("The plugin has loaded correctly");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!cooldownPlayers.contains(player) && event.getAction() == Action.RIGHT_CLICK_BLOCK && !teleportingPlayers.contains(player)) {
            if (event.getClickedBlock().getType() == Material.BARREL) {
                Location barrelLocation = event.getClickedBlock().getLocation();
                World world = barrelLocation.getWorld();
                if (world != null && world.getName().equals("spawn")) {
                    if ((barrelLocation.getBlockX() == -49 && barrelLocation.getBlockY() == 9 && barrelLocation.getBlockZ() == -53)
                            || (barrelLocation.getBlockX() == -45 && barrelLocation.getBlockY() == 24 && barrelLocation.getBlockZ() == -37)) {
                        if (player.hasPermission("lab_barrel_tp")) {
                            if (player.getGameMode() == GameMode.SURVIVAL) {
                                teleportingPlayers.add(player);
                                activateTeleportSequence(player, barrelLocation);
                                event.setCancelled(true);
                                cooldownPlayers.add(player);
                                Bukkit.getScheduler().runTaskLater(this, () -> cooldownPlayers.remove(player), 156);
                            } else {
                                player.sendMessage(ChatColor.RED + "You must be in survival mode to use this.");
                                return;
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "You do not have permission to use this.");
                            return;
                        }
                    }
                }
            }
        }
    }

    private void activateTeleportSequence(Player player, Location barrelLocation) {
        new BukkitRunnable() {
            @Override
            public void run() {
                getServer().dispatchCommand(getServer().getConsoleSender(), "screeneffect fullscreen BLACK 5 40 5 freeze " + player.getName());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.playSound(player.getLocation(), Sound.BLOCK_BARREL_OPEN, 1, 1);
                    }
                }.runTaskLater(LeyThings.this, 4);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.playSound(player.getLocation(), Sound.BLOCK_CHERRY_LEAVES_BREAK, 1, 0.1f);
                    }
                }.runTaskLater(LeyThings.this, 14);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.playSound(player.getLocation(), Sound.BLOCK_CHERRY_LEAVES_BREAK, 1, 0.1f);
                    }
                }.runTaskLater(LeyThings.this, 20);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.playSound(player.getLocation(), Sound.BLOCK_CHERRY_LEAVES_BREAK, 1, 0.1f);
                    }
                }.runTaskLater(LeyThings.this, 26);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        teleportPlayer(player, barrelLocation);
                    }
                }.runTaskLater(LeyThings.this, 46);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.playSound(player.getLocation(), Sound.BLOCK_BARREL_CLOSE, 1, 1);
                        teleportingPlayers.remove(player);
                    }
                }.runTaskLater(LeyThings.this, 56);
            }
        }.runTask(this);
    }

    private void teleportPlayer(Player player, Location barrelLocation) {
        World world = player.getWorld();
        Location teleportLocation;
        if (barrelLocation.getBlockX() == -49 && barrelLocation.getBlockY() == 9 && barrelLocation.getBlockZ() == -53) {
            teleportLocation = new Location(world, -45.5, 23, -36.6, 80, 0);
        } else {
            teleportLocation = new Location(world, -47.5, 8, -52.5, -90, 0);
        }
        player.teleport(teleportLocation);
    }

    @Override
    public void onDisable() {
        getLogger().info("The plugin has stopped successfully");
    }
}
