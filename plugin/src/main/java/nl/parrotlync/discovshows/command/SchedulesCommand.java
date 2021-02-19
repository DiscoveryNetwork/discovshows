package nl.parrotlync.discovshows.command;

import nl.parrotlync.discovshows.DiscovShows;
import nl.parrotlync.discovshows.model.ScheduleType;
import nl.parrotlync.discovshows.model.Show;
import nl.parrotlync.discovshows.model.ShowDates;
import nl.parrotlync.discovshows.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.Date;

public class SchedulesCommand extends ShowCommand {

    public SchedulesCommand() {
        super("Get a list of schedules for a show", "discovshows.command.schedules");
        arguments.put(0, new ShowTabArgument());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            ChatUtil.sendConfigMessage(sender, "player-only");
            return;
        }

        Player player = (Player) sender;
        Show show = DiscovShows.getInstance().getShowManager().getShow(args[0]);
        if (show == null) {
            ChatUtil.sendConfigMessage(sender, "show-not-found", args[0]);
            return;
        }

        double size = (Math.floor((double) DiscovShows.getInstance().getShowManager().getShows().size() / 9) + 1) * 9;
        Inventory inventory = Bukkit.createInventory(null, (int) size, String.format("Schedules [%s]", show.getName()));

        for (Date date : show.getScheduleManager().getSchedules()) {
            inventory.addItem(getListItem(date, show.getScheduleManager().getScheduleType(date)));
        }
        player.openInventory(inventory);
    }

    private ItemStack getListItem(Date date, ScheduleType type) {
        ItemStack item;
        ItemMeta meta;

        if (type == ScheduleType.REGULAR) {
            item = new ItemStack(Material.STAINED_CLAY, 1, (byte) 11);
            meta = item.getItemMeta();
            meta.setLore(Collections.singletonList(ShowDates.getDayTimeFormat().format(date)));
            meta.setDisplayName("§7Weekly schedule");
        } else if (type == ScheduleType.CUSTOM) {
            item = new ItemStack(Material.STAINED_CLAY, 1, (byte) 4);
            meta = item.getItemMeta();
            meta.setLore(Collections.singletonList(date.toString()));
            meta.setDisplayName("§7Custom schedule");
        } else { return null; }

        item.setItemMeta(meta);
        return item;
    }
}
