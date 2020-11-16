package nl.parrotlync.discovshows.model;

import nl.parrotlync.discovshows.DiscovShows;
import nl.parrotlync.discovshows.task.ShowRunner;
import nl.parrotlync.discovshows.util.StorageUtil;
import org.apache.commons.lang.time.DateUtils;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Show {
    private String name;
    private BukkitTask task;
    private Boolean repeat;
    private Boolean discordEnabled;
    private String discordMessage;
    private final String filePath;
    private final String identifier;
    private final BossBar bossBar;
    private HashMap<Integer, List<String>> commands;
    private HashMap<Integer, List<String>> steps;

    public Show(String name, HashMap<Integer, List<String>> steps, Boolean repeat, HashMap<Integer, List<String>> commands, String filePath, String identifier, Boolean discordEnabled, String discordMessage) {
        this.name = name;
        this.steps = steps;
        this.repeat = repeat;
        this.commands = commands;
        this.filePath = filePath;
        this.identifier = identifier;
        this.discordEnabled = discordEnabled;
        this.discordMessage = discordMessage;
        this.bossBar = Bukkit.createBossBar("§3§l" + name + "§7: §cIdle", BarColor.RED, BarStyle.SOLID);
    }

    public List<String> getStepCommands(Integer offset) {
        return steps.get(offset);
    }

    public List<Integer> getSteps() {
        return new ArrayList<>(steps.keySet());
    }

    public Integer getDuration() {
        Integer max = Collections.max(steps.keySet());
        return max / 20;
    }

    public String getName() {
        return name;
    }

    public Boolean getRepeat() {
        return repeat;
    }

    public BukkitTask getTask() {
        return task;
    }

    public List<String> getCommands(Integer seconds) {
        return commands.get(seconds);
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getFilePath() { return filePath; }

    public List<Integer> getCommandKeys() {
        return new ArrayList<>(commands.keySet());
    }

    public boolean isRunning() {
        return task != null && !task.isCancelled();
    }

    public boolean hasDiscordBroadcastEnabled() {
        return discordEnabled;
    }

    public String getDiscordMessage() { return discordMessage; }

    public void updateBossBar(Integer ticks) {
        if (bossBar.getPlayers().size() != 0) {
            if (isRunning()) {
                bossBar.setColor(BarColor.GREEN);
                if (Collections.max(steps.keySet()) != 0) {
                    bossBar.setProgress(Double.valueOf(ticks) / Collections.max(steps.keySet()));
                }
                bossBar.setTitle("§3§l" + name + "§7: §aRunning §7- §6" + ticks + " §7ticks");
            } else {
                bossBar.setColor(BarColor.RED);
                bossBar.setTitle("§3§l" + name + "§7: §cIdle");
                bossBar.setProgress(1);
            }
        }
    }

    public void togglePlayerBossBar(Player player) {
        if (bossBar.getPlayers().contains(player)) {
            bossBar.removePlayer(player);
        } else {
            bossBar.addPlayer(player);
        }
    }

    public boolean start(Integer ticks) {
        if (!isRunning()) {
            reload();
            task = new ShowRunner(this, ticks).runTaskTimer(DiscovShows.getInstance(), 0L, 1L);
            return true;
        }
        return false;
    }

    public boolean scheduleNow() {
        if (!isRunning()) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                Date now = formatter.parse(formatter.format(new Date()));
                if (!getCommandKeys().isEmpty()) {
                    int max = Collections.max(getCommandKeys());
                    Date date = DateUtils.addSeconds(now, max + 10);
                    StorageUtil.addCustomSchedule(date, filePath);
                } else {
                    start(0);
                }
                return true;
            } catch (ParseException e) {
                DiscovShows.getInstance().getLogger().info("Something went wrong while scheduling a show.");
            }
        }
        return false;
    }

    public void stop() {
        if (isRunning()) {
            task.cancel();
            if (commands.get(0) != null) {
                CommandSender sender = Bukkit.getServer().getConsoleSender();
                for (String cmd : commands.get(0)) {
                    Bukkit.dispatchCommand(sender, cmd);
                }
            }
        }
        updateBossBar(0);
    }

    private void reload() {
        name = StorageUtil.getName(filePath);
        steps = StorageUtil.getSteps(filePath);
        repeat = StorageUtil.getRepeat(filePath);
        discordEnabled = StorageUtil.getDiscordEnabled(filePath);
        discordMessage = StorageUtil.getDiscordMessage(filePath);
        commands = StorageUtil.getCommands(filePath);
    }
}
