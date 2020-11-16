package nl.parrotlync.discovshows.listener;

import nl.parrotlync.discovshows.DiscovShows;
import nl.parrotlync.discovshows.model.Show;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
                    Show show = DiscovShows.getInstance().getShowManager().getShow(meta.getLore().get(1));
                    player.performCommand("show start " + show.getIdentifier());
                } else if (item.getData().toString().equals("STAINED_CLAY(13)")) {
                    player.closeInventory();
                    Show show = DiscovShows.getInstance().getShowManager().getShow(meta.getLore().get(1));
                    player.performCommand("show stop " + show.getIdentifier());
                }
            }
        }
        if (inventory.getTitle().equals("Schedule list")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory.getTitle().equals("DiscovShows List")) {
            event.setCancelled(true);
        }
        if (inventory.getTitle().equals("Schedule list")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntityType() == EntityType.FALLING_BLOCK) {
            if (event.getEntity().getMetadata("Type").get(0) != null && event.getEntity().getMetadata("Type").get(0).asString().equals("Fountain")) {
                event.setCancelled(true);
            }
        }
    }
}
