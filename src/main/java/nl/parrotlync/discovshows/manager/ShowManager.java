package nl.parrotlync.discovshows.manager;

import nl.parrotlync.discovshows.DiscovShows;
import nl.parrotlync.discovshows.model.Show;
import nl.parrotlync.discovshows.util.StorageUtil;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ShowManager {
    private final HashMap<String, Show> shows = new HashMap<>();

    public Show getShow(String identifier) {
        return shows.get(identifier);
    }

    public List<Show> getShows() {
        return new ArrayList<>(shows.values());
    }

    public List<String> getIdentifiers() {
        return new ArrayList<>(shows.keySet());
    }

    public void load() {
        Bukkit.getScheduler().cancelTasks(DiscovShows.getInstance());
        shows.clear();
        File showDirectory = new File(DiscovShows.getInstance().getDataFolder(), "shows/");
        showDirectory.mkdirs();
        for (String fileName : Objects.requireNonNull(showDirectory.list())) {
            String filePath = new File(showDirectory, fileName).getPath();
            if (!StorageUtil.checkConfig(filePath)) { continue; }
            Show show = new Show(fileName.replace(".yml", ""), filePath);
            shows.put(show.getIdentifier(), show);
        }
        DiscovShows.getInstance().startShowScheduler();
        DiscovShows.getInstance().getLogger().info("Loaded " + shows.size() + " shows!");
    }
}
