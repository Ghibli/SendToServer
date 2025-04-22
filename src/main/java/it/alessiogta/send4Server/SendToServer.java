package it.alessiogta.send4Server;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;

public class SendToServer extends JavaPlugin {

    private static SendToServer instance;
    private final Map<UUID, String> playerServerMap = new HashMap<>();
    private final List<String> serverList = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        getCommand("sendtoserver").setExecutor(new SendToServerCommand(this));
        getCommand("sendtoserver").setTabCompleter(new SendToServerTabCompleter(this));
        getServer().getPluginManager().registerEvents(new GuiListener(this), this);
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", (channel, player, message) -> {
            try {
                DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
                String sub = in.readUTF();

                if (sub.equals("GetServer")) {
                    String currentServer = in.readUTF();
                    playerServerMap.put(player.getUniqueId(), currentServer);
                }

                if (sub.equals("GetServers")) {
                    String serversString = in.readUTF();
                    serverList.clear();
                    serverList.addAll(Arrays.asList(serversString.split(", ")));
                    getConfig().set("servers", serverList);

                    for (String server : serverList) {
                        String path = "server-icons." + server;
                        if (!getConfig().contains(path)) {
                            getConfig().set(path + ".material", "ENDER_PEARL");
                            getConfig().set(path + ".name", "&a" + server);
                            getConfig().set(path + ".slot", -1);
                        }
                    }

                    saveConfig();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Refresh periodico
        getServer().getScheduler().runTaskTimer(this, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                requestServer(p);
                requestServerList(p);
            }
        }, 20L, 20L * 30);

        printBanner(true);

    }

    public static SendToServer getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        printBanner(false);
    }

    public void requestServer(Player player) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("GetServer");
            player.sendPluginMessage(this, "BungeeCord", b.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void requestServerList(Player player) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("GetServers");
            player.sendPluginMessage(this, "BungeeCord", b.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getMessage(String path) {
        return ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages." + path, path));
    }

    public String format(String text, Map<String, String> values) {
        for (Map.Entry<String, String> entry : values.entrySet()) {
            text = text.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public String getPlayerServer(UUID uuid) {
        return playerServerMap.getOrDefault(uuid, "Unknown");
    }

    public List<String> getDynamicServers() {
        return serverList.isEmpty() ? getConfig().getStringList("servers") : serverList;
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