package nl.parrotlync.discovshows.command;

import nl.parrotlync.discovshows.DiscovShows;
import nl.parrotlync.discovshows.model.Show;
import nl.parrotlync.discovshows.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class ShowCommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender.hasPermission("discovshows.operate")) {
            if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
                return help(sender);
            }

            if (args[0].equalsIgnoreCase("start") && args.length == 2) {
                if (DiscovShows.getInstance().getShowManager().getShow(args[1]) != null) {
                    Show show = DiscovShows.getInstance().getShowManager().getShow(args[1]);
                    show.start();
                    ChatUtil.sendMessage(sender, "§7Started the show §a" + show.getName(), true);
                    return true;
                }
            }

            if (args[0].equalsIgnoreCase("stop") && args.length == 2) {
                if (DiscovShows.getInstance().getShowManager().getShow(args[1]) != null) {
                    Show show = DiscovShows.getInstance().getShowManager().getShow(args[1]);
                    show.stop();
                    ChatUtil.sendMessage(sender, "§7Stopped the show §c" + show.getName(), true);
                    return true;
                }
            }

            if (args[0].equalsIgnoreCase("reload")) {
                try {
                    DiscovShows.getInstance().getShowManager().load();
                    ChatUtil.sendMessage(sender, "§7Show files have been reloaded", true);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            if (args[0].equalsIgnoreCase("list")) {
                Player player = (Player) sender;
                Inventory inventory = Bukkit.createInventory(null, 27, "DiscovShows List");
                for (Show show : DiscovShows.getInstance().getShowManager().getShows()) {
                    inventory.addItem(createGuiItem(show));
                }
                player.openInventory(inventory);
                return true;
            }
        }
        return help(sender);
    }

    private boolean help(CommandSender sender) {
        if (sender.hasPermission("discovshows.operate")) {
            ChatUtil.sendMessage(sender, "§f+---+ §9DiscovShows §f+---+", false);
            ChatUtil.sendMessage(sender, "§3/show start <name> §7Start a show", false);
            ChatUtil.sendMessage(sender, "§3/show stop <name> §7Stop a show", false);
            ChatUtil.sendMessage(sender, "§3/show list §7List all shows", false);
            ChatUtil.sendMessage(sender, "§3/show reload §7Reload all show & config files", false);
        } else {
            ChatUtil.sendMessage(sender, "§cYou do not have permission to do that!", true);
        }
        return true;
    }

    private ItemStack createGuiItem(Show show) {
        ItemStack item;
        ItemMeta meta;
        if (show.getTask() == null || show.getTask().isCancelled()) {
            item = new ItemStack(Material.STAINED_CLAY, 1, (byte) 14);
            meta = item.getItemMeta();
            meta.setLore(Collections.singletonList("§cIdle"));
        } else {
            item = new ItemStack(Material.STAINED_CLAY, 1, (byte) 13);
            meta = item.getItemMeta();
            meta.setLore(Collections.singletonList("§aRunning"));
        }
        meta.setDisplayName("§7" + show.getName());
        item.setItemMeta(meta);
        return item;
    }
}
