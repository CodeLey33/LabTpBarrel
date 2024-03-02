package me.ley.leythings;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public final class LeyThings extends JavaPlugin implements Listener {

    private Location teleportLocation = new Location(Bukkit.getWorld("spawn"), -46, 23, -37);
    private boolean teleporting = false;
    private Set<Player> cooldownPlayers = new HashSet<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("The plugin has loaded correctly");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!cooldownPlayers.contains(player) && event.getAction() == Action.RIGHT_CLICK_BLOCK && !teleporting) {
            if (event.getClickedBlock().getType() == Material.BARREL) {
                Location barrelLocation = event.getClickedBlock().getLocation();
                World world = barrelLocation.getWorld();
                // Verificar si el barril está en las coordenadas específicas
                if (world != null && world.getName().equals("spawn")) {
                    if ((barrelLocation.getBlockX() == -49 && barrelLocation.getBlockY() == 9 && barrelLocation.getBlockZ() == -53)
                            || (barrelLocation.getBlockX() == -45 && barrelLocation.getBlockY() == 24 && barrelLocation.getBlockZ() == -37)) {
                        // Verificar si el jugador está en modo survival
                        if (player.getGameMode() == GameMode.SURVIVAL) {
                            activateTeleportSequence(player, barrelLocation);
                            event.setCancelled(true); // Cancelar el evento de interactuar con el barril
                            teleporting = true;
                            cooldownPlayers.add(player);
                            Bukkit.getScheduler().runTaskLater(this, () -> cooldownPlayers.remove(player), 156);
                        } else {
                            player.sendMessage("You must be in survival mode");
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
                player.performCommand("screeneffect fullscreen BLACK 5 40 5 freeze " + player.getName());
                // Esperar 0.2 segundos
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        // Sonido del barril abriendo
                        player.playSound(player.getLocation(), Sound.BLOCK_BARREL_OPEN, 1, 1);
                    }
                }.runTaskLater(LeyThings.this, 4);

                // Sonidos y teletransporte
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        // Sonido de las hojas de cerezo rompiéndose (primer sonido)
                        player.playSound(player.getLocation(), Sound.BLOCK_CHERRY_LEAVES_BREAK, 1, 0.1f);
                    }
                }.runTaskLater(LeyThings.this, 14);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        // Sonido de las hojas de cerezo rompiéndose (segundo sonido)
                        player.playSound(player.getLocation(), Sound.BLOCK_CHERRY_LEAVES_BREAK, 1, 0.1f);
                    }
                }.runTaskLater(LeyThings.this, 20);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        // Sonido de las hojas de cerezo rompiéndose (tercer sonido)
                        player.playSound(player.getLocation(), Sound.BLOCK_CHERRY_LEAVES_BREAK, 1, 0.1f);
                    }
                }.runTaskLater(LeyThings.this, 26);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        // Teletransportar al jugador
                        teleportPlayer(player, barrelLocation);
                    }
                }.runTaskLater(LeyThings.this, 46);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        // Sonido del barril cerrándose
                        player.playSound(player.getLocation(), Sound.BLOCK_BARREL_CLOSE, 1, 1);
                    }
                }.runTaskLater(LeyThings.this, 56);
            }
        }.runTask(this);
    }

    private void teleportPlayer(Player player, Location barrelLocation) {
        World world = player.getWorld();
        Location teleportLocation;
        // Definir las coordenadas de teletransporte dependiendo del barril interactuado
        if (barrelLocation.getBlockX() == -49 && barrelLocation.getBlockY() == 9 && barrelLocation.getBlockZ() == -53) {
            teleportLocation = new Location(world, -45.5, 23, -36.6, 80, 0);
        } else {
            teleportLocation = new Location(world, -47.5, 8, -52.5, -90, 0);
        }
        player.teleport(teleportLocation);
        teleporting = false;
    }



    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("The plugin has stopped successfully");
    }
}
