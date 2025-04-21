package it.alessiogta.send4Server;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class SendCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration config = SendToServer.getInstance().getConfig();

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("sendtoserver.reload")) {
                sender.sendMessage(config.getString("messages.no-permission").replace("&", "§"));
                return true;
            }
            SendToServer.getInstance().reloadConfig();
            sender.sendMessage(config.getString("messages.reloaded").replace("&", "§"));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(config.getString("messages.only-player").replace("&", "§"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("sendtoserver.use")) {
            player.sendMessage(config.getString("messages.no-permission").replace("&", "§"));
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(config.getString("messages.usage").replace("&", "§"));
            return true;
        }

        String server = args[0];
        player.sendMessage(config.getString("messages.sending").replace("&", "§").replace("%server%", server));
        SendToServer.sendPlayerToServer(player, server);
        return true;
    }
}
