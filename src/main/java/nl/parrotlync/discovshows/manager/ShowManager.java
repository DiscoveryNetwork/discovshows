package nl.parrotlync.discovshows.manager;

import nl.parrotlync.discovshows.DiscovShows;
import nl.parrotlync.discovshows.model.Show;
import nl.parrotlync.discovshows.task.ShowChecker;
import nl.parrotlync.discovshows.util.StorageUtil;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.*;

public class ShowManager {
    private HashMap<String, Show> shows = new HashMap<>();
    private String path = "plugins/DiscovShows/";
    private BukkitTask showChecker;

    public List<Show> getShows() {
        List<Show> showList = new ArrayList<>();
        for (String name : shows.keySet()) {
            showList.add(shows.get(name));
        }
        return showList;
    }

    public List<String> getIdentifiers() {
        return new ArrayList<>(shows.keySet());
    }

    private void addShow(String name, Show show) {
        if (shows.get(name.toLowerCase()) == null) {
            shows.put(name.toLowerCase(), show);
        }
    }

    public Show getShow(String name) { return shows.get(name.toLowerCase()); }

    public void load() {
        for (Show show : shows.values()) {
            show.stop();
        }
        shows.clear();
        stopChecker();
        File directory = new File(path);
        for (String fileName : Objects.requireNonNull(directory.list())) {
                String filePath = path + fileName;
                Show show = new Show(StorageUtil.getName(filePath), StorageUtil.getSteps(filePath), StorageUtil.getRepeat(filePath), StorageUtil.getCommands(filePath) ,filePath, fileName.replace(".yml", ""));
                addShow(show.getIdentifier(), show);
        }
        DiscovShows.getInstance().getLogger().info("Loaded " + shows.size() + " shows.");
        startChecker();
    }

    public void stopChecker() {
        if (showChecker != null) {
            showChecker.cancel();
        }
    }

    public void startChecker() {
        showChecker = new ShowChecker().runTaskTimerAsynchronously(DiscovShows.getInstance(), 0L, 20L);
    }
}
