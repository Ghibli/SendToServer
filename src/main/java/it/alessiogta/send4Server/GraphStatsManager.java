package it.alessiogta.send4Server;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GraphStatsManager {

    private final Plugin plugin;
    private Connection connection;
    private final Map<String, Boolean> trackedStats = new HashMap<>();
    private FileConfiguration graphsConfig;
    private FileConfiguration statsFollowConfig;
    private int intervalSeconds;

    public GraphStatsManager(Plugin plugin) {
        this.plugin = plugin;
        loadConfigs();
        if (graphsConfig.getBoolean("enabled")) {
            connectDatabase();
            createTable();
            startScheduler();

            if (graphsConfig.getBoolean("show-startup-message", true)) {
                String rawMessage = graphsConfig.getString("startup-message", "&a[SendToServer] Grafici attivi: salvataggio ogni {seconds} secondi.");
                String finalMessage = ChatColor.translateAlternateColorCodes('&', rawMessage.replace("{seconds}", String.valueOf(intervalSeconds)));
                plugin.getLogger().info(finalMessage);
            }
        }
    }

    private void loadConfigs() {
        File graphFolder = new File(plugin.getDataFolder(), "graph");
        if (!graphFolder.exists()) {
            graphFolder.mkdirs();
        }

        File graphsConfigFile = new File(graphFolder, "graphs_config.yml");
        File statsFollowFile = new File(graphFolder, "stats_to_follow.yml");

        if (!graphsConfigFile.exists()) plugin.saveResource("graph/graphs_config.yml", false);
        if (!statsFollowFile.exists()) plugin.saveResource("graph/stats_to_follow.yml", false);

        graphsConfig = YamlConfiguration.loadConfiguration(graphsConfigFile);
        statsFollowConfig = YamlConfiguration.loadConfiguration(statsFollowFile);

        intervalSeconds = graphsConfig.getInt("save-interval-seconds", 1800);

        for (String stat : statsFollowConfig.getConfigurationSection("tracked-stats").getKeys(false)) {
            boolean enabled = statsFollowConfig.getBoolean("tracked-stats." + stat, false);
            trackedStats.put(stat, enabled);
        }
    }

    private void connectDatabase() {
        try {
            String host = graphsConfig.getString("host");
            int port = graphsConfig.getInt("port");
            String db = graphsConfig.getString("name");
            String user = graphsConfig.getString("user");
            String pass = graphsConfig.getString("password");
            int timeout = graphsConfig.getInt("connectionTimeout", 3000);
            String timezone = graphsConfig.getString("timezone", "UTC");

            String url = "jdbc:mysql://" + host + ":" + port + "/" + db +
                    "?useSSL=false&serverTimezone=" + timezone +
                    "&connectTimeout=" + timeout +
                    "&autoReconnect=true";

            connection = DriverManager.getConnection(url, user, pass);
            plugin.getLogger().info("\u001B[32mConnessione al database dei grafici stabilita.\u001B[0m");
        } catch (SQLException e) {
            plugin.getLogger().warning("Errore nella connessione al database grafici: " + e.getMessage());
        }
    }

    private void createTable() {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS sts_graphs_stats (" +
                    "uuid VARCHAR(36)," +
                    "server_name VARCHAR(64)," +
                    "stat_key VARCHAR(64)," +
                    "value DOUBLE," +
                    "date DATE," +
                    "PRIMARY KEY (uuid, server_name, stat_key, date)" +
                    ");");
            plugin.getLogger().info("\u001B[32mTabella sts_graphs_stats verificata/creata.\u001B[0m");
        } catch (SQLException e) {
            plugin.getLogger().warning("Errore nella creazione della tabella sts_graphs_stats: " + e.getMessage());
        }
    }

    private void startScheduler() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::saveAllPlayerStats, intervalSeconds * 20L, intervalSeconds * 20L);
    }

    private void saveAllPlayerStats() {
        String serverName = SendToServer.getInstance().getConfig().getString("server-name", "default");
        LocalDate today = LocalDate.now();

        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();

            for (String statKey : trackedStats.keySet()) {
                if (trackedStats.get(statKey)) {
                    double value = getStatValue(player, statKey);
                    saveStat(uuid, serverName, statKey, value, today);
                }
            }
        }
    }

    private double getStatValue(Player player, String statKey) {
        if (statKey.equalsIgnoreCase("balance")) {
            Economy econ = SendToServer.getInstance().getEconomy();
            if (econ != null) {
                return econ.getBalance(player);
            } else {
                return 0.0;
            }
        }

        try {
            return player.getStatistic(Statistic.valueOf(statKey));
        } catch (IllegalArgumentException ex) {
            return 0;
        }
    }

    private void saveStat(UUID uuid, String serverName, String statKey, double value, LocalDate date) {
        try (PreparedStatement ps = connection.prepareStatement(
                "REPLACE INTO sts_graphs_stats (uuid, server_name, stat_key, value, date) VALUES (?, ?, ?, ?, ?)")) {
            ps.setString(1, uuid.toString());
            ps.setString(2, serverName);
            ps.setString(3, statKey);
            ps.setDouble(4, value);
            ps.setDate(5, Date.valueOf(date));
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().warning("Errore nel salvataggio della statistica " + statKey + " per " + uuid + ": " + e.getMessage());
        }
    }

    public void reloadConfigs() {
        loadConfigs();
        //plugin.getLogger().info("[SendToServer] Configurazioni dei grafici ricaricate con successo.");
    }

}