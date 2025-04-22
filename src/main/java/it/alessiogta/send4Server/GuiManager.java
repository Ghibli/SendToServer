package it.alessiogta.send4Server;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class GuiManager {

    private static final Map<UUID, UUID> selectedTargets = new HashMap<>();

    public static void openPlayerSelector(Player opener, SendToServer plugin) {
        Inventory gui = Bukkit.createInventory(null, 54,
                ChatColor.translateAlternateColorCodes('&',
                        plugin.getConfig().getString("gui.player-selector-title")));

        for (Player target : Bukkit.getOnlinePlayers()) {
            if (target.getUniqueId().equals(opener.getUniqueId())) continue;

            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(target.getUniqueId()));
            meta.setDisplayName("§a" + target.getName());

            List<String> lore = new ArrayList<>();
            String serverName = plugin.getPlayerServer(target.getUniqueId());
            lore.add(plugin.format(plugin.getConfig().getString("gui.lore.server"),
                    Map.of("server", serverName != null ? serverName : "Unknown")));
            lore.add(plugin.format(plugin.getConfig().getString("gui.lore.coordinates"),
                    Map.of("x", String.valueOf(target.getLocation().getBlockX()),
                           "y", String.valueOf(target.getLocation().getBlockY()),
                           "z", String.valueOf(target.getLocation().getBlockZ()))));
            meta.setLore(lore);
            skull.setItemMeta(meta);
            gui.addItem(skull);
        }

        opener.openInventory(gui);
    }

    public static void openServerSelector(Player executor, OfflinePlayer target, SendToServer plugin) {

        Inventory gui = Bukkit.createInventory(null, 27,
                ChatColor.translateAlternateColorCodes('&',
                        plugin.format(plugin.getConfig().getString("gui.server-selector-title"),
                                Map.of("player", target.getName()))));

        selectedTargets.put(executor.getUniqueId(), target.getUniqueId());

        for (String serverName : plugin.getDynamicServers()) {
            Material material = Material.ENDER_PEARL;
            String displayName = "§a" + serverName;
            int slot = -1;

            if (plugin.getConfig().contains("server-icons." + serverName)) {
                String matName = plugin.getConfig().getString("server-icons." + serverName + ".material");
                material = Material.getMaterial(matName.toUpperCase());
                displayName = ChatColor.translateAlternateColorCodes('&',
                        plugin.getConfig().getString("server-icons." + serverName + ".name"));
                slot = plugin.getConfig().getInt("server-icons." + serverName + ".slot", -1);
            }

            ItemStack item = new ItemStack(material != null ? material : Material.BARRIER);
            var meta = item.getItemMeta();
            meta.setDisplayName(displayName);
            meta.setLore(List.of(plugin.format(
                    plugin.getConfig().getString("gui.lore.click-to-send"),
                    Map.of("player", target.getName(), "server", serverName)
            )));
            item.setItemMeta(meta);

            if (slot >= 0 && slot < gui.getSize()) {
                gui.setItem(slot, item);
            } else {
                gui.addItem(item);
            }
        }

        executor.openInventory(gui);
    }

    public static UUID getSelectedTarget(UUID executorId) {
        return selectedTargets.get(executorId);
    }
}