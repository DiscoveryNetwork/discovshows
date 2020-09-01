package nl.parrotlync.discovshows.manager;

import nl.parrotlync.discovshows.DiscovShows;
import nl.parrotlync.discovshows.model.Show;
import nl.parrotlync.discovshows.util.StorageUtil;

import java.io.File;
import java.util.*;

public class ShowManager {
    private HashMap<String, Show> shows = new HashMap<>();
    private String path = "plugins/DiscovShows/";

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
        shows.clear();
        File directory = new File(path);
        for (String fileName : Objects.requireNonNull(directory.list())) {
            String filePath = path + fileName;
            Show show = new Show(StorageUtil.getName(filePath), StorageUtil.getSchedule(filePath), StorageUtil.getSteps(filePath), StorageUtil.getRepeat(filePath), StorageUtil.getCommands(filePath) ,filePath);
            addShow(fileName.replace(".yml", ""), show);
        }
        DiscovShows.getInstance().getLogger().info("Loaded " + shows.size() + " shows.");
    }

}
