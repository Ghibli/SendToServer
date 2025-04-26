package it.alessiogta.send4Server;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

public class DatabaseManager {

    private static Connection connection;

    public static void connect(JavaPlugin plugin) {
        plugin = SendToServer.getInstance();

        String host = plugin.getConfig().getString("database.host");
        int port = plugin.getConfig().getInt("database.port");
        String db = plugin.getConfig().getString("database.name");
        String user = plugin.getConfig().getString("database.user");
        String pass = plugin.getConfig().getString("database.password");
        int timeout = plugin.getConfig().getInt("database.connectionTimeout", 3000);
        String timezone = plugin.getConfig().getString("database.timezone", "UTC");
        String url = "jdbc:mysql://" + host + ":" + port + "/" + db
                + "?useSSL=false"
                + "&serverTimezone=" + timezone
                + "&connectTimeout=" + timeout
                + "&autoReconnect=true";
        try {
            connection = DriverManager.getConnection(url, user, pass);
            plugin.getLogger().info("\u001B[36mConnessione MySQL: \u001B[32mOK !.\u001B[0m");
            ensureConnection(SendToServer.getInstance());
            try (Statement stmt = connection.createStatement()) {
                ResultSet rs = connection.getMetaData().getTables(null, null, "sts_player_status", null);
                if (!rs.next()) {
                    stmt.executeUpdate("CREATE TABLE sts_player_status ("
                            + "uuid VARCHAR(36) PRIMARY KEY,"
                            + "username VARCHAR(16),"
                            + "last_login DATETIME,"
                            + "last_logout DATETIME,"
                            + "last_server_in VARCHAR(64),"
                            + "last_server_out VARCHAR(64),"
                            + "x INT, y INT, z INT,"
                            + "world VARCHAR(64),"
                            + "online BOOLEAN DEFAULT 0"
                            + ");");
                    plugin.getLogger().info("\u001B[36mTabella\u001B[33m Player Status \\u001B[36mcreata con successo\u001B[0m\"");
                } else {
                    plugin.getLogger().info("\u001B[36mLa tabella\u001B[33m Player Status \u001B[36mesiste già.\u001B[0m");
                }
            }
            try (Statement stmt = connection.createStatement()) {
                ResultSet rs = connection.getMetaData().getTables(null, null, "sts_player_movements", null);
                if (rs.next()) {
                    plugin.getLogger().info("\u001B[36mLa tabella\u001B[33m Player Movements\u001B[36m esiste già.\u001B[0m"); // Giallo
                } else {
                    stmt.executeUpdate("CREATE TABLE sts_player_movements ("
                            + "id INT AUTO_INCREMENT PRIMARY KEY,"
                            + "uuid VARCHAR(36),"
                            + "username VARCHAR(16),"
                            + "action ENUM('login','logout','switch'),"
                            + "server VARCHAR(64),"
                            + "x INT, y INT, z INT,"
                            + "world VARCHAR(64),"
                            + "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP"
                            + ");");
                    plugin.getLogger().info("\u001B[36mTabella\u001B[33m Player Movements\u001B[36m creata con successo.\u001B[0m"); // Verde
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updatePlayerStatus(Player player, String serverName, boolean online) {
        if (connection == null) return;
        ensureConnection(SendToServer.getInstance());
        try {
            ZoneId zoneId = ZoneId.of(SendToServer.getInstance().getConfig().getString("database.timezone", "UTC"));
            ZonedDateTime now = ZonedDateTime.now(zoneId);
            Timestamp timestamp = Timestamp.from(now.toInstant());

            PreparedStatement ps = connection.prepareStatement(
                    "REPLACE INTO sts_player_status (uuid, username, last_login, last_server_in, x, y, z, world, online) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
            );
            ps.setString(1, player.getUniqueId().toString());
            ps.setString(2, player.getName());
            ps.setTimestamp(3, timestamp);
            ps.setString(4, serverName);
            ps.setInt(5, player.getLocation().getBlockX());
            ps.setInt(6, player.getLocation().getBlockY());
            ps.setInt(7, player.getLocation().getBlockZ());
            ps.setString(8, player.getWorld().getName());
            ps.setBoolean(9, online);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateLogoutInfo(Player player, String serverName) {
        if (connection == null) return;
        ensureConnection(SendToServer.getInstance());
        try {
            ZoneId zoneId = ZoneId.of(SendToServer.getInstance().getConfig().getString("database.timezone", "UTC"));
            ZonedDateTime now = ZonedDateTime.now(zoneId);
            Timestamp timestamp = Timestamp.from(now.toInstant());

            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE sts_player_status SET last_logout = ?, last_server_out = ? WHERE uuid = ?"
            );
            ps.setTimestamp(1, timestamp);
            ps.setString(2, serverName);
            ps.setString(3, player.getUniqueId().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void logMovement(Player player, String server, String action) {
        if (connection == null) return;
        ensureConnection(SendToServer.getInstance());
        try {
            ZoneId zoneId = ZoneId.of(SendToServer.getInstance().getConfig().getString("database.timezone", "UTC"));
            ZonedDateTime now = ZonedDateTime.now(zoneId);
            Timestamp timestamp = Timestamp.from(now.toInstant());

            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO sts_player_movements (uuid, username, action, server, x, y, z, world, timestamp) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
            );
            ps.setString(1, player.getUniqueId().toString());
            ps.setString(2, player.getName());
            ps.setString(3, action);
            ps.setString(4, server);
            ps.setInt(5, player.getLocation().getBlockX());
            ps.setInt(6, player.getLocation().getBlockY());
            ps.setInt(7, player.getLocation().getBlockZ());
            ps.setString(8, player.getWorld().getName());
            ps.setTimestamp(9, timestamp);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void ensureConnection(JavaPlugin plugin) {
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(2)) {
                plugin.getLogger().warning("[SendToServer] Connecting to DB in progress...");
                connect(plugin);
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("[SendToServer] Database Connection Error: " + e.getMessage());
        }
    }

    public static void logSwitch(Player player, String targetServer) {
        logMovement(player, targetServer, "switch");
    }

    public static boolean isConnected() {
        return connection != null;
    }
}