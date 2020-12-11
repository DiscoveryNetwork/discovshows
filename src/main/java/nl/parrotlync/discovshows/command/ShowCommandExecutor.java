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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.StringUtil;
import org.bukkit.util.Vector;
import ru.beykerykt.lightapi.LightAPI;
import ru.beykerykt.lightapi.LightType;
import ru.beykerykt.lightapi.chunks.ChunkInfo;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class ShowCommandExecutor implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender.hasPermission("discovshows.operate")) {
            if (args.length == 0) {
                ChatUtil.sendMessage(sender, "§6DiscovShows-1.12.2-v" + DiscovShows.getInstance().getDescription().getVersion() +" §7(§aParrotLync§7) - Use /show help", false);
                return true;
            }

            if (args[0].equalsIgnoreCase("help")) {
                return help(sender);
            }

            if (args[0].equalsIgnoreCase("start") && args.length == 2) {
                Show show = DiscovShows.getInstance().getShowManager().getShow(args[1]);
                if (show != null) {
                    if (canStart(show)) {
                        if (show.scheduleNow()) {
                            ChatUtil.sendMessage(sender, "§7The show §a" + show.getName() + " §7should be starting soon.", true);
                        } else {
                            ChatUtil.sendMessage(sender, "§cShow is already running!", true);
                        }
                    } else {
                        ChatUtil.sendMessage(sender, "§cThat show can't be started right now!", true);
                    }
                }
                else {
                    ChatUtil.sendMessage(sender, "§cThat show doesn't exist!", true);
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("stop") && args.length == 2) {
                Show show = DiscovShows.getInstance().getShowManager().getShow(args[1]);
                if (show != null) {
                    show.stop();
                    ChatUtil.sendMessage(sender, "§7Stopped the show §c" + show.getName(), true);
                    return true;
                }
            }

            if (args[0].equalsIgnoreCase("list")) {
                Player player = (Player) sender;
                double size = (Math.floor((double) DiscovShows.getInstance().getShowManager().getShows().size() / 9) + 1) * 9;
                Inventory inventory = Bukkit.createInventory(null, (int) size, "DiscovShows List");
                for (Show show : DiscovShows.getInstance().getShowManager().getShows()) {
                    inventory.addItem(createShowGuiItem(show));
                }
                player.openInventory(inventory);
                return true;
            }
        }

        if (sender.hasPermission("discovshows.developer")) {
            if (args[0].equalsIgnoreCase("forcestart")) {
                Show show = DiscovShows.getInstance().getShowManager().getShow(args[1]);
                if (show != null) {
                    Integer ticks =  (args.length == 3) ? (args[2].equals("0")) ? 0 : Integer.parseInt(args[2]) : 0;
                    if (show.start(ticks)) {
                        ChatUtil.sendMessage(sender, "§7Started the show §a" + show.getName(), true);
                    } else {
                        ChatUtil.sendMessage(sender, "§cShow is already running!", true);
                    }
                } else {
                    ChatUtil.sendMessage(sender, "§cThat show doesn't exist!", true);
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("debug") && args.length == 2) {
                Player player = (Player) sender;
                Show show = DiscovShows.getInstance().getShowManager().getShow(args[1]);
                if (show != null) {
                    show.togglePlayerBossBar(player);
                    return true;
                }
                return false;
            }

            if (args[0].equalsIgnoreCase("schedules") && args.length == 2) {
                Player player = (Player) sender;
                SimpleDateFormat weekFormatter = new SimpleDateFormat("EEEE HH:mm z");
                Show show = DiscovShows.getInstance().getShowManager().getShow(args[1]);
                if (show != null) {
                    double size = StorageUtil.getSchedules(show.getFilePath()).size() + StorageUtil.getCustomSchedules(show.getFilePath()).size();
                    size = (Math.floor(size / 9) + 1) * 9;
                    Inventory inventory = Bukkit.createInventory(null,  (int) size, "Schedule list");
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
        }

        if (sender.hasPermission("discovshows.effects")) {
            if (args[0].equalsIgnoreCase("fountain") && args.length == 9) {
                Location location = new Location(Bukkit.getWorld(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
                Vector motion = new Vector(Float.parseFloat(args[5]), Float.parseFloat(args[6]), Float.parseFloat(args[7]));
                Integer runTime = Integer.parseInt(args[8]);
                Fountain fountain = new Fountain(location, motion, runTime);
                fountain.run();
                return true;
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

            if (args[0].equalsIgnoreCase("light")) {
                if (args[1].equalsIgnoreCase("create") && args.length == 7) {
                    Location location = new Location(Bukkit.getWorld(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]));
                    LightAPI.createLight(location, LightType.BLOCK, Integer.parseInt(args[6]), false);
                    updateLightChunks(location, Integer.parseInt(args[6]));
                    ChatUtil.sendMessage(sender, "§7Created a new light source in §6" + args[2] + " §7at §b" + args[3] + " " + args[4] + " " + args[5], true);
                    return true;
                }

                if (args[1].equalsIgnoreCase("remove") && args.length == 6) {
                    Location location = new Location(Bukkit.getWorld(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]));
                    LightAPI.deleteLight(location, LightType.BLOCK, false);
                    updateLightChunks(location, 15);
                    ChatUtil.sendMessage(sender, "§7Removed a light source in §6" + args[2] + " §7at §b" + args[3] + " " + args[4] + " " + args[5], true);
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
            suggestions.add("paste");
            suggestions.add("schedules");
            return StringUtil.copyPartialMatches(args[0], suggestions, new ArrayList<String>());
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("start") || args[0].equalsIgnoreCase("stop") || args[0].equalsIgnoreCase("forcestart") || args[0].equalsIgnoreCase("schedules") || args[0].equalsIgnoreCase("debug")) {
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
            if (sender.hasPermission("discovshows.developer")) {
                ChatUtil.sendMessage(sender, "§3/show forcestart <show> [ticks] §7Toggle debug mode for a show", false);
                ChatUtil.sendMessage(sender, "§3/show debug <show> §7Toggle debug mode for a show", false);
                ChatUtil.sendMessage(sender, "§3/show schedules <show> §7Get an overview of all show schedules", false);
                ChatUtil.sendMessage(sender, "§3/show reload §7Reload all show & config files", false);
            }
            if (sender.hasPermission("discovshows.effects")) {
                ChatUtil.sendMessage(sender, "§3/show fountain <world> <x> <y> <z> <dx> <dy> <dz> <runTime> §7Spawn a falling block fountain", false);
                ChatUtil.sendMessage(sender, "§3/show paste <schematic> <world> <x> <y> <z> §7Paste a WorldEdit schematic at a specific location", false);
                ChatUtil.sendMessage(sender, "§3/show light create <world> <x> <y> <z> <level> §7Create an invisible light source", false);
                ChatUtil.sendMessage(sender, "§3/show light remove <world> <x> <y> <z> §7Create an invisible light source", false);
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
            SimpleDateFormat weekFormatter = new SimpleDateFormat("EEEE HH:mm:ss");
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            int showTime = show.getDuration() + Collections.max(show.getCommandKeys());
            Date weekNow = weekFormatter.parse(weekFormatter.format(new Date()));
            for (Date date : StorageUtil.getSchedules(show.getFilePath())) {
                if (DateUtils.addSeconds(weekNow, showTime).compareTo(date) >= 0 && weekNow.compareTo(DateUtils.addSeconds(date, showTime)) <= 0) {
                    start = false;
                }
            }
            Date dateNow = dateFormatter.parse(dateFormatter.format(new Date()));
            for (Date date : StorageUtil.getCustomSchedules(show.getFilePath())) {
                if (DateUtils.addSeconds(dateNow, showTime).compareTo(date) >= 0 && dateNow.compareTo(DateUtils.addSeconds(date, showTime)) <= 0) {
                    start = false;
                }
            }
            return start;
        } catch (Exception e) {
            return false;
        }
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

    public void updateLightChunks(Location location, Integer lightLevel) {
        for (ChunkInfo chunkInfo : LightAPI.collectChunks(location, LightType.BLOCK, lightLevel)) {
            LightAPI.updateChunk(chunkInfo, LightType.BLOCK);
        }
    }
}
