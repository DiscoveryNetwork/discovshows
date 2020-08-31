package nl.parrotlync.discovshows.manager;

import nl.parrotlync.discovshows.DiscovShows;
import nl.parrotlync.discovshows.model.Show;
import nl.parrotlync.discovshows.util.StorageUtil;

import java.io.File;
import java.util.*;

public class ShowManager {
    private HashMap<String, Show> shows = new HashMap<String, Show>();
    private String path = "plugins/DiscovShows/";

    public List<Show> getShows() {
        List<Show> showList = new ArrayList<Show>();
        for (String name : shows.keySet()) {
            showList.add(shows.get(name));
        }
        return showList;
    }

    public void addShow(String name, Show show) {
        if (shows.get(name.toLowerCase()) == null) {
            shows.put(name.toLowerCase(), show);
        }
    }

    public Show getShow(String name) { return shows.get(name.toLowerCase()); }

    public void removeShow(String name) {
        if (shows.get(name.toLowerCase()) == null) {
            shows.remove(name.toLowerCase());
        }
    }

    public void load() {
        shows.clear();
        File directory = new File("plugins/DiscovShows/");
        for (String fileName : Objects.requireNonNull(directory.list())) {
            String filePath = path + fileName;
            Show show = new Show(StorageUtil.getName(filePath), StorageUtil.getSchedule(filePath), StorageUtil.getSteps(filePath), StorageUtil.getRepeat(filePath), StorageUtil.getCommands(filePath) ,filePath);
            shows.put(show.getName().toLowerCase(), show);
        }
        DiscovShows.getInstance().getLogger().info("Loaded " + shows.size() + " shows.");
    }

}
