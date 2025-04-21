package it.alessiogta.send4Server;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class SendToServer extends JavaPlugin {

    private static SendToServer instance;

    @Override
    public void onEnable() {
        instance = this;
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getCommand("sendtoserver").setExecutor(new SendCommand());
        saveDefaultConfig();
        printBanner(true);
    }

    public static SendToServer getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        printBanner(false);
    }

    public static void sendPlayerToServer(Player player, String serverName) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(serverName);
        } catch (Exception e) {
            player.sendMessage("§cErrore durante il trasferimento: " + e.getMessage());
        }
        player.sendPluginMessage(instance, "BungeeCord", b.toByteArray());
    }

    private void printBanner(boolean enable) {
        String prefix = "\u001B[36m"; // Colore ciano
        String nome = "\u001B[33m"; //Il mio nome Giallo
        String url ="\u001B[94m"; // Link colore BLU
        String reset = "\u001B[0m";   // Reset
        String status = enable ? "\u001B[32mABILITATO" : "\u001B[31mDISABILITATO";

        getLogger().info(prefix + "============================================");
        getLogger().info("        SendToServer Plugin per MCLEGACY      ");
        getLogger().info("         Creato da:"+ nome +" AlessioGTA (owner)         "+ reset);
        getLogger().info(url + "            www.mclegacy.it                  "+reset);
        getLogger().info("           Stato: " + status + prefix);
        getLogger().info(prefix+"============================================" + reset);
    }
}