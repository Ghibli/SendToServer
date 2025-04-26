package it.alessiogta.send4Server;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;


import java.io.*;
import java.util.*;

public class SendToServer extends JavaPlugin {

    private static SendToServer instance;
    private final Map<UUID, String> playerServerMap = new HashMap<>();
    private final List<String> serverList = new ArrayList<>();
    private GraphStatsManager graphStatsManager;
    private static Economy economy = null;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        //Inizializzo la dipendenza Vault per l'economia del server
        if (!setupEconomy()) {
            getLogger().severe("Vault non trovato o nessun plugin di economia disponibile!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        File statsConfigFile = new File(getDataFolder(), "stats_config.yml");
        if (!statsConfigFile.exists()) {
            saveResource("stats_config.yml", false);
            getLogger().info("\u001B[32mFile stats_config.yml creato.\u001B[0m");
        }

        //Controlla se nel config il databse è attivato
        if (getConfig().getBoolean("database.enabled")) {
            DatabaseManager.connect(this);
        }
        //Richiamo Stats Manager
        StatsManager.init(this);

        getCommand("sendtoserver").setExecutor(new SendToServerCommand(this));
        getCommand("sendtoserver").setTabCompleter(new SendToServerTabCompleter(this));
        // Richiamo lo Stats Manager, che servirà per i grafici
        graphStatsManager = new GraphStatsManager(this);

        getServer().getPluginManager().registerEvents(new GuiListener(this), this);
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getServer().getPluginManager().registerEvents(new QuitListener(this), this);

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

                    // Aggiorna lo stats_config.yml solo se sync-enabled: true
                    File statsFile = new File(getDataFolder(), "stats_config.yml");
                    FileConfiguration statsConfig = YamlConfiguration.loadConfiguration(statsFile);

                    if (statsConfig.getBoolean("sync-enabled", true)) {
                        statsConfig.set("active-servers", serverList);
                        try {
                            statsConfig.save(statsFile);
                            if (statsConfig.getBoolean("show-sync-messages", true)) {
                                String color = "\u001B[33m";
                                String reset = "\u001B[0m";
                                String msg = statsConfig.getString("sync-message", "Lista server aggiornata in stats_config.yml.");
                                getLogger().info(color + msg + reset);
                            }
                        } catch (IOException e) {
                            getLogger().warning("Impossibile salvare stats_config.yml: " + e.getMessage());
                        }
                    }
                    try {
                        statsConfig.save(statsFile);
                        //getLogger().info("\u001B[36mLista server aggiornata in stats_config.yml.\u001B[0m");
                    } catch (IOException e) {
                        getLogger().warning("Impossibile salvare stats_config.yml: " + e.getMessage());
                    }
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

        //Ogni quanto salva le stats del giocatore periodicamente
        if (StatsManager.isEnabled()) {
            int interval = StatsManager.getStatsConfig().getInt("save-interval-seconds", 300);
            getServer().getScheduler().runTaskTimer(this, () -> {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    StatsManager.updateStats(p);
                }
            }, 20L * interval, 20L * interval);
        }
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

    public GraphStatsManager getGraphStatsManager() {
        return graphStatsManager;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public Economy getEconomy() {
        return economy;
    }

    private void printBanner(boolean enable) {
        String prefix = "\u001B[36m"; // Colore ciano
        String nome = "\u001B[33m"; //Il mio nome Giallo
        String url ="\u001B[94m"; // Link colore BLU
        String reset = "\u001B[0m";   // Reset
        String status = enable ? "\u001B[32mABILITATO" : "\u001B[31mDISABILITATO";
        String serverName = getConfig().getString("server-name", "default");

        getLogger().info(prefix + "============================================");
        getLogger().info("      Send To Server Plugin per MCLEGACY      ");
        getLogger().info("         Creato da:"+ nome +" AlessioGTA (owner)         "+ reset);
        getLogger().info(url + "               www.mclegacy.it                  "+reset);
        getLogger().info("               Stato: " + status + prefix);
        getLogger().info(prefix + "                 Server: " + "\u001B[35m" + serverName + reset);
        getLogger().info(prefix+"============================================" + reset);
    }
    //DATABASE IMPLEMENTATION
}