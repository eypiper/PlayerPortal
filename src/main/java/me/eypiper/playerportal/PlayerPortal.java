package me.eypiper.playerportal;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.ArrayList;
import java.util.UUID;

public class PlayerPortal extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("PlayerPortal");
        getServer().getConsoleSender().sendMessage("Plugin created by eyPiper");
        getServer().getConsoleSender().sendMessage("Spigot: https://www.spigotmc.org/members/eypiper.746034/");
        getServer().getPluginManager().registerEvents(this, this);
        saveDefaultConfig();
    }

    public static final ArrayList<UUID> inPortal = new ArrayList<UUID>();
    public int waitTime = getConfig().getInt("wait-time");

    @EventHandler
    public void PlayerMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (event.getTo().getBlock().getType().equals(Material.NETHER_PORTAL)) {
            if (player.getGameMode() == GameMode.CREATIVE) {
                if (!inPortal.contains(player.getUniqueId())) {
                    inPortal.add(player.getUniqueId());
                }
            }
        }
        if (!event.getTo().getBlock().getType().equals(Material.NETHER_PORTAL)) {
            if (player.getGameMode() == GameMode.CREATIVE) {
                if (inPortal.contains(player.getUniqueId())) {
                    inPortal.remove(player.getUniqueId());
                }
            }
        }
    }

    @EventHandler
    public void PlayerPortalEvent(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        final Location portalFinder;
        if (player.getGameMode() == GameMode.CREATIVE) {
            try {
                portalFinder = NetherPortalFinder.locate(event.getTo());
            } catch (IllegalArgumentException e) {
                event.setCancelled(false);
                return;
            }
            if (portalFinder != null) {
                event.setCancelled(true);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (inPortal.contains(player.getUniqueId())) {
                            player.teleport(portalFinder);
                        }
                    }
                }.runTaskLater(this, waitTime*20);
            }
        }
    }
}