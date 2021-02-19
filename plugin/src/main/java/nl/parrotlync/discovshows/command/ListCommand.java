package nl.parrotlync.discovshows.command;

import nl.parrotlync.discovshows.DiscovShows;
import nl.parrotlync.discovshows.model.Show;
import nl.parrotlync.discovshows.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class ListCommand extends ShowCommand {

    public ListCommand() {
        super("Get a list of all shows", "discovshows.command.list");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            ChatUtil.sendConfigMessage(sender, "player-only");
            return;
        }

        Player player = (Player) sender;
        double size = (Math.floor((double) DiscovShows.getInstance().getShowManager().getShows().size() / 9) + 1) * 9;
        Inventory inventory = Bukkit.createInventory(null, (int) size, "DiscovShows List");

        for (Show show : DiscovShows.getInstance().getShowManager().getShows()) {
            inventory.addItem(getListItem(show));
        }
        player.openInventory(inventory);
    }

    private ItemStack getListItem(Show show) {
        ItemStack item;
        ItemMeta meta;
        if (show.isRunning()) {
            item = new ItemStack(Material.STAINED_CLAY, 1, (byte) 5);
            meta = item.getItemMeta();
            meta.setLore(Arrays.asList("§aRunning", show.getIdentifier()));
        } else if (show.isScheduled()) {
            item = new ItemStack(Material.STAINED_CLAY, 1, (byte) 11);
            meta = item.getItemMeta();
            meta.setLore(Arrays.asList("§6Scheduled", show.getIdentifier()));
        } else {
            item = new ItemStack(Material.STAINED_CLAY, 1, (byte) 14);
            meta = item.getItemMeta();
            meta.setLore(Arrays.asList("§cIdle", show.getIdentifier()));
        }
        meta.setDisplayName(String.format("§7%s", show.getName()));
        item.setItemMeta(meta);
        return item;
    }
}
