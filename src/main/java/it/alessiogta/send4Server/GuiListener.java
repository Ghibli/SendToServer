package it.alessiogta.send4Server;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.UUID;

public class GuiListener implements Listener {

    private final SendToServer plugin;

    public GuiListener(SendToServer plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        String title = event.getView().getTitle();
        if (title.contains("Seleziona")) {
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            if (item == null || item.getType() != Material.PLAYER_HEAD) return;

            SkullMeta meta = (SkullMeta) item.getItemMeta();
            OfflinePlayer selected = meta.getOwningPlayer();
            if (selected != null) {
                GuiManager.openServerSelector(player, selected, plugin);
            }
        } else if (title.contains("Invia")) {
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            if (item == null || item.getType() != Material.ENDER_PEARL) return;

            String server = ChatColor.stripColor(item.getItemMeta().getDisplayName());
            UUID targetId = GuiManager.getSelectedTarget(player.getUniqueId());
            Player target = Bukkit.getPlayer(targetId);
            if (target != null) {
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b);
                try {
                    out.writeUTF("Connect");
                    out.writeUTF(server);
                    target.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
                    player.sendMessage("§aHai inviato §e" + target.getName() + " §aal server §e" + server);
                } catch (Exception e) {
                    player.sendMessage("§cErrore durante il trasferimento: " + e.getMessage());
                }
            }
        }
    }
}