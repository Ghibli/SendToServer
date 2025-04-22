package it.alessiogta.send4Server;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class SendToServerCommand implements CommandExecutor {

    private final SendToServer plugin;

    public SendToServerCommand(SendToServer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(plugin.getMessage("usage"));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("sendtoserver.reload")) {
                sender.sendMessage(plugin.getMessage("no-permission"));
                return true;
            }
            plugin.reloadConfig();
            sender.sendMessage(plugin.getMessage("reload-success"));
            return true;
        }

        if (args[0].equalsIgnoreCase("gui")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getMessage("only-players"));
                return true;
            }
            Player player = (Player) sender;
            GuiManager.openPlayerSelector(player, plugin);
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage("only-players"));
            return true;
        }

        Player player = (Player) sender;
        String serverName = args[0];
        if (!plugin.getConfig().getStringList("servers").contains(serverName)) {
            player.sendMessage(plugin.format(plugin.getMessage("server-not-found"), Map.of("server", serverName)));
            return true;
        }

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(serverName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
        player.sendMessage(plugin.format(plugin.getMessage("player-sent"), Map.of("server", serverName)));
        return true;
    }
}