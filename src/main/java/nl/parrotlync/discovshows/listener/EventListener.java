package nl.parrotlync.discovshows.listener;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class EventListener implements Listener {

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTitle().equals("DiscovShows List")) {
            event.setCancelled(true);
        }
        if (event.getView().getTitle().contains("Schedules")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntityType() == EntityType.FALLING_BLOCK) {
            if (event.getEntity().getMetadata("Type").get(0) != null) {
                if (event.getEntity().getMetadata("Type").get(0).asString().equals("FallingBlockEffect")) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
