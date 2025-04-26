package it.alessiogta.send4Server;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class JoinListener implements Listener {

    private final SendToServer plugin;

    public JoinListener(SendToServer plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.requestServer(player);
        plugin.requestServerList(player);

        //Verifica di Online se per lo stato nel DB
        UUID uuid = player.getUniqueId();
        if (plugin.getConfig().getBoolean("database.enabled") && DatabaseManager.isConnected()) {
            String server = plugin.getPlayerServer(player.getUniqueId());
            DatabaseManager.updatePlayerStatus(player, server, true);
            DatabaseManager.logMovement(player, server, "login");

        }
        StatsManager.updateStats(event.getPlayer());

    }
}