package nl.parrotlync.discovshows.command;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.registry.WorldData;
import nl.parrotlync.discovshows.DiscovShows;
import nl.parrotlync.discovshows.model.Fountain;
import nl.parrotlync.discovshows.model.Show;
import nl.parrotlync.discovshows.util.ChatUtil;
import nl.parrotlync.discovshows.util.StorageUtil;
import org.apache.commons.lang.time.DateUtils;
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

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class ShowCommandExecutor implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender.hasPermission("discovshows.operate")) {
            if (args.length == 0) {
                ChatUtil.sendMessage(sender, "§6DiscovShows-1.12.2-v1.3.3 §7(§aParrotLync§7) - Use /show help", false);
                return true;
            }

            if (args[0].equalsIgnoreCase("help")) {
                return help(sender);
            }

            if (args[0].equalsIgnoreCase("forcestart")) {
                if (DiscovShows.getInstance().getShowManager().getShow(args[1]) != null) {
                    Integer ticks =  (args.length == 3) ? (args[2].equals("0")) ? 0 : Integer.parseInt(args[2]) : 0;
                    Show show = DiscovShows.getInstance().getShowManager().getShow(args[1]);
                    if (show.start(ticks)) {
                        ChatUtil.sendMessage(sender, "§7Started the show §a" + show.getName(), true);
                    } else {
                        ChatUtil.sendMessage(sender, "§cShow is already running!", true);
                    }
                    return true;
                }
            }

            if (args[0].equalsIgnoreCase("start") && args.length == 2) {
                if (DiscovShows.getInstance().getShowManager().getShow(args[1]) != null) {
                    Show show = DiscovShows.getInstance().getShowManager().getShow(args[1]);
                    if (canStart(show)) {
                        if (show.scheduleNow()) {
                            ChatUtil.sendMessage(sender, "§7The show §a" + show.getName() + " §7should be starting soon.", true);
                        } else {
                            ChatUtil.sendMessage(sender, "§cShow is already running!", true);
                        }
                    } else {
                        ChatUtil.sendMessage(sender, "§cThat show can't be started right now!", true);
                    }
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
                    StorageUtil.clearCustomSchedule();
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
                    inventory.addItem(createShowGuiItem(show));
                }
                player.openInventory(inventory);
                return true;
            }

            if (args[0].equalsIgnoreCase("schedules") && args.length == 2) {
                Player player = (Player) sender;
                SimpleDateFormat weekFormatter = new SimpleDateFormat("EEEE HH:mm z");
                Show show = DiscovShows.getInstance().getShowManager().getShow(args[1]);
                if (show != null) {
                    Inventory inventory = Bukkit.createInventory(null,  27, "Schedule list");
                    for (Date date : StorageUtil.getSchedules(show.getFilePath())) {
                        inventory.addItem(createScheduleGuiItem(weekFormatter.format(date), (byte) 11));
                    }
                    for (Date date : StorageUtil.getCustomSchedules(show.getFilePath())) {
                        inventory.addItem(createScheduleGuiItem(date.toString(), (byte) 4));
                    }
                    player.openInventory(inventory);
                    return true;
                }
            }
        }

        if (sender.hasPermission("discovshows.effects")) {
            if (args[0].equalsIgnoreCase("effect")) {
                if (args[1].equalsIgnoreCase("fountain")) {
                    new Fountain(args);
                    return true;
                }
            }

            if (args[0].equalsIgnoreCase("paste") && args.length == 6) {
                File file = new File("plugins/WorldEdit/schematics/" + args[1] + ".schematic");

                if (!file.exists()) {
                    ChatUtil.sendMessage(sender, "§cFile not found!", true);
                    return true;
                }

                ClipboardFormat format = ClipboardFormat.findByFile(file);
                if (format != null) {
                    try {
                        World world = new BukkitWorld(Bukkit.getWorld(args[2]));
                        WorldData data = world.getWorldData();
                        ClipboardReader reader = format.getReader(new FileInputStream(file));
                        Clipboard clipboard = reader.read(data);
                        EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1);
                        Operation operation = new ClipboardHolder(clipboard, data)
                                .createPaste(editSession, data)
                                .ignoreAirBlocks(true)
                                .to(BlockVector.toBlockPoint(Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5])))
                                .build();
                        Operations.complete(operation);
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return false;
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
            suggestions.add("paste");
            suggestions.add("schedules");
            return StringUtil.copyPartialMatches(args[0], suggestions, new ArrayList<String>());
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("start") || args[0].equalsIgnoreCase("stop") || args[0].equalsIgnoreCase("forcestart") || args[0].equalsIgnoreCase("schedules")) {
                suggestions.addAll(DiscovShows.getInstance().getShowManager().getIdentifiers());
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
            ChatUtil.sendMessage(sender, "§3/show schedules <show> §7Get an overview of all show schedules", false);
            ChatUtil.sendMessage(sender, "§3/show reload §7Reload all show & config files", false);
            if (sender.hasPermission("discovshows.effects")) {
                //ChatUtil.sendMessage(sender, "§3/show effect fountain <x> <y> <z> <dx> <dy> <dz> <blockId> <blockData> <runTime> <world> §7Spawn a fountain", false);
                ChatUtil.sendMessage(sender, "§3/show paste <schematic> <world> <x> <y> <z> §7Paste a WorldEdit schematic at a specific location", false);
            }
        } else {
            ChatUtil.sendMessage(sender, "§cYou do not have permission to do that!", true);
        }
        return true;
    }

    private boolean canStart(Show show) {
        if (show.isRunning()) { return false; }
        if (StorageUtil.getCustomSchedules(show.getFilePath()).isEmpty() && StorageUtil.getSchedules(show.getFilePath()).isEmpty()) { return true; }
        try {
            boolean start = true;
            SimpleDateFormat tFormatter = new SimpleDateFormat("HH:mm:ss");
            List<Date> dates = StorageUtil.getSchedules(show.getFilePath());
            dates.addAll(StorageUtil.getCustomSchedules(show.getFilePath()));
            Date now = tFormatter.parse(tFormatter.format(new Date()));
            if (show.getCommandKeys() != null) {
                int max = Collections.max(show.getCommandKeys());
                int showTime = show.getDuration() + 1;
                for (Date date : dates) {
                    if (DateUtils.addSeconds(now, max + showTime).compareTo(tFormatter.parse(tFormatter.format(date))) >= 0 && now.compareTo(tFormatter.parse(tFormatter.format(DateUtils.addSeconds(date, showTime)))) <= 0) {
                        start = false;
                    }
                }
                return start;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private ItemStack createShowGuiItem(Show show) {
        ItemStack item;
        ItemMeta meta;
        if (show.getTask() == null || show.getTask().isCancelled()) {
            if (!canStart(show)) {
                item = new ItemStack(Material.STAINED_CLAY, 1, (byte) 11);
                meta = item.getItemMeta();
                meta.setLore(Arrays.asList("§6Scheduled", show.getIdentifier()));
            } else {
                item = new ItemStack(Material.STAINED_CLAY, 1, (byte) 14);
                meta = item.getItemMeta();
                meta.setLore(Arrays.asList("§cIdle", show.getIdentifier()));
            }
        } else {
            item = new ItemStack(Material.STAINED_CLAY, 1, (byte) 5);
            meta = item.getItemMeta();
            meta.setLore(Arrays.asList("§aRunning", show.getIdentifier()));
        }
        meta.setDisplayName("§7" + show.getName());
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createScheduleGuiItem(String date, byte color) {
        ItemStack item = new ItemStack(Material.STAINED_CLAY, 1, color);
        ItemMeta meta = item.getItemMeta();
        meta.setLore(Collections.singletonList("§a" + date));
        if (color == (byte) 11) {
            meta.setDisplayName("§7Schedule");
        } else {
            meta.setDisplayName("§7Custom schedule");
        }
        item.setItemMeta(meta);
        return item;
    }
}
