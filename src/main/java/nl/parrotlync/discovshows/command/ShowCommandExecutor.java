package nl.parrotlync.discovshows.command;

import nl.parrotlync.discovshows.DiscovShows;
import nl.parrotlync.discovshows.model.Fountain;
import nl.parrotlync.discovshows.model.Show;
import nl.parrotlync.discovshows.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShowCommandExecutor implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender.hasPermission("discovshows.operate")) {
            if (args.length == 0) {
                ChatUtil.sendMessage(sender, "§6DiscovShows-1.12.2-v1.2.1 §7- Use /show help", true);
                return true;
            }

            if (args[0].equalsIgnoreCase("help")) {
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

        if (sender.hasPermission("discovshows.effects")) {
            if (args[0].equalsIgnoreCase("effect")) {
                if (args[1].equalsIgnoreCase("fountain")) {
                    new Fountain(args);
                    return true;
                }
            }
        }

        return help(sender);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            suggestions.add("help");
            suggestions.add("start");
            suggestions.add("stop");
            suggestions.add("list");
            suggestions.add("reload");
            return StringUtil.copyPartialMatches(args[0], suggestions, new ArrayList<String>());
        }

        if (args.length == 2) {
            if (args[1].equalsIgnoreCase("start") || args[1].equalsIgnoreCase("stop")) {
                for (Show show : DiscovShows.getInstance().getShowManager().getShows()) {
                    suggestions.add(show.getName());
                }
                return StringUtil.copyPartialMatches(args[1], suggestions, new ArrayList<String>());
            }
        }

        return suggestions;
    }

    private boolean help(CommandSender sender) {
        if (sender.hasPermission("discovshows.operate")) {
            ChatUtil.sendMessage(sender, "§f+---+ §9DiscovShows §f+---+", false);
            ChatUtil.sendMessage(sender, "§3/show start <name> §7Start a show", false);
            ChatUtil.sendMessage(sender, "§3/show stop <name> §7Stop a show", false);
            ChatUtil.sendMessage(sender, "§3/show list §7List all shows", false);
            ChatUtil.sendMessage(sender, "§3/show reload §7Reload all show & config files", false);
            if (sender.hasPermission("discovshows.effects")) {
                //ChatUtil.sendMessage(sender, "§3/show effect fountain <x> <y> <z> <dx> <dy> <dz> <blockId> <blockData> <runTime> <world> §7Spawn a fountain", false);
            }
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
