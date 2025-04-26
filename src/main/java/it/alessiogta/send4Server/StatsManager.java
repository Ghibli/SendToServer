package it.alessiogta.send4Server;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import net.milkbowl.vault.economy.Economy;

public class StatsManager {

    private static Connection connection;
    private static FileConfiguration statsConfig;
    private static JavaPlugin plugin;

    public static void init(JavaPlugin p) {
        plugin = p;
        File file = new File(p.getDataFolder(), "stats_config.yml");
        if (!file.exists()) {
            p.saveResource("stats_config.yml", false);
        }
        statsConfig = YamlConfiguration.loadConfiguration(file);

        if (!statsConfig.getBoolean("enabled", false)) return;

        String currentServer = statsConfig.getString("this-server", "unknown");
        if (!statsConfig.getStringList("active-servers").contains(currentServer)) return;

        connect();
    }

    private static void connect() {
        try {
            String host = statsConfig.getString("database.host");
            int port = statsConfig.getInt("database.port");
            String db = statsConfig.getString("database.name");
            String user = statsConfig.getString("database.user");
            String pass = statsConfig.getString("database.password");
            int timeout = statsConfig.getInt("database.connectionTimeout", 3000);
            String timezone = statsConfig.getString("database.timezone", "UTC");

            // Connessione con autoReconnect=true per evitare errori di timeout
            String url = "jdbc:mysql://" + host + ":" + port + "/" + db
                    + "?useSSL=false"
                    + "&serverTimezone=" + timezone
                    + "&connectTimeout=" + timeout
                    + "&autoReconnect=true";
            connection = DriverManager.getConnection(url, user, pass);

            try (Statement stmt = connection.createStatement()) {
                ResultSet rs = connection.getMetaData().getTables(null, null, "sts_player_stats", null);
                if (!rs.next()) {
                    stmt.executeUpdate("CREATE TABLE sts_player_stats ("
                        + "uuid VARCHAR(36),"
                        + "username VARCHAR(16),"
                        + "server_name VARCHAR(64),"
                        + "stat_key VARCHAR(64),"
                        + "value DOUBLE DEFAULT 0,"
                        + "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,"
                        + "PRIMARY KEY (uuid, server_name, stat_key)"
                        + ");");
                    plugin.getLogger().info("\u001B[36mTabella\u001B[33mPlayer Stats\u001B[36m creata con successo.\u001B[0m");
                } else {
                    plugin.getLogger().info("\u001B[36mLa tabella\u001B[33m Player Stats\u001B[36m esiste già.\u001B[0m");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateStats(Player player) {
        if (connection == null) return;
        ensureConnection(SendToServer.getInstance());
        try {
            String serverName = statsConfig.getString("this-server", "unknown");
            UUID uuid = player.getUniqueId();
            String username = player.getName();

            ZoneId zoneId = ZoneId.of(statsConfig.getString("database.timezone", "UTC"));
            Timestamp timestamp = Timestamp.from(ZonedDateTime.now(zoneId).toInstant());

            for (Statistic stat : Statistic.values()) {
                if (stat.isSubstatistic() || stat.getType() != Statistic.Type.UNTYPED) continue;

                int value = player.getStatistic(stat);

                PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO sts_player_stats (uuid, username, server_name, stat_key, value, timestamp) VALUES (?, ?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE value = VALUES(value), timestamp = VALUES(timestamp)"
                );
                ps.setString(1, uuid.toString());
                ps.setString(2, username);
                ps.setString(3, serverName);
                ps.setString(4, stat.name());
                ps.setInt(5, value);
                ps.setTimestamp(6, timestamp);
                ps.executeUpdate();
            }
            //Salva il bilancio dopo il ciclo
            Economy econ = SendToServer.getInstance().getEconomy();
            if (econ != null) {
                double balance = econ.getBalance(player);

                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO sts_player_stats (uuid, username, server_name, stat_key, value, timestamp) VALUES (?, ?, ?, ?, ?, ?)"
                                + " ON DUPLICATE KEY UPDATE value = VALUES(value), timestamp = VALUES(timestamp)"
                );
                ps.setString(1, uuid.toString());
                ps.setString(2, username);
                ps.setString(3, serverName);
                ps.setString(4, "BALANCE");
                ps.setDouble(5, balance);
                ps.setTimestamp(6, timestamp);
                ps.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void ensureConnection(JavaPlugin plugin) {
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(2)) {
                plugin.getLogger().warning("[SendToServer] Connessione persa, riconnessione in corso...");
                connect();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("[SendToServer] Errore durante il controllo della connessione: " + e.getMessage());
        }
    }

    public static boolean isEnabled() {
        return statsConfig != null && statsConfig.getBoolean("enabled", false);
    }

    public static FileConfiguration getStatsConfig() {
        return statsConfig;
    }
}