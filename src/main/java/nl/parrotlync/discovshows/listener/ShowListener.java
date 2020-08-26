package nl.parrotlync.discovshows.listener;

import nl.parrotlync.discovshows.DiscovShows;
import nl.parrotlync.discovshows.model.Show;
import nl.parrotlync.discovshows.util.ChatUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

public class ShowListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        if (inventory.getTitle().equals("DiscovShows List")) {
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            if (item != null && item.getType() == Material.STAINED_CLAY) {
                ItemMeta meta = item.getItemMeta();
                Player player = (Player) event.getWhoClicked();
                if (item.getData().toString().equals("STAINED_CLAY(14)")) {
                    player.closeInventory();
                    Show show = DiscovShows.getInstance().getShowManager().getShow(meta.getDisplayName().replace("§7", ""));
                    show.start();
                    ChatUtil.sendMessage(player, "§7Started the show §a" + show.getName(), true);
                } else if (item.getData().toString().equals("STAINED_CLAY(13)")) {
                    player.closeInventory();
                    Show show = DiscovShows.getInstance().getShowManager().getShow(meta.getDisplayName().replace("§7", ""));
                    show.stop();
                    ChatUtil.sendMessage(player, "§7Stopped the show §c" + show.getName(), true);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryDragEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory.getTitle().equals("DiscovShows List")) {
            event.setCancelled(true);
        }
    }
}
