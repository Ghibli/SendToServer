package it.alessiogta.send4Server;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.entity.Player;

public class QuitListener implements Listener {

    private final SendToServer plugin;

    public QuitListener(SendToServer plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (plugin.getConfig().getBoolean("database.enabled") && DatabaseManager.isConnected()) {
            String server = plugin.getPlayerServer(player.getUniqueId());
            DatabaseManager.updatePlayerStatus(player, server, false);
            DatabaseManager.updateLogoutInfo(player, server);
            DatabaseManager.logMovement(player, server, "logout");
        }
    }
}