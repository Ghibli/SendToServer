package it.alessiogta.send4Server;

import org.bukkit.command.*;

import java.util.*;

public class SendToServerTabCompleter implements TabCompleter {

    private final SendToServer plugin;

    public SendToServerTabCompleter(SendToServer plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            completions.add("gui");
            completions.add("reload");
            completions.addAll(plugin.getDynamicServers()); // <- lista dinamica in RAM
            return completions;
        }
        return Collections.emptyList();
    }

}