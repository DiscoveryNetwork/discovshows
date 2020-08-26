package nl.parrotlync.discovshows.model;

import nl.parrotlync.discovshows.DiscovShows;
import nl.parrotlync.discovshows.task.ShowRunner;
import nl.parrotlync.discovshows.util.StorageUtil;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Show {
    private String name;
    private Date schedule;
    private BukkitTask task;
    private String filePath;
    private HashMap<Integer, List<String>> steps;

    public Show(String name, Date schedule, HashMap<Integer, List<String>> steps, String filePath) {
        this.name = name;
        this.schedule = schedule;
        this.steps = steps;
        this.filePath = filePath;
    }

    public Show() {}

    public List<String> getStepCommands(Integer offset) {
        return steps.get(offset);
    }

    public List<Integer> getSteps() {
        return new ArrayList<>(steps.keySet());
    }

    public String getName() {
        return name;
    }

    public Date getSchedule() {
        return schedule;
    }

    public BukkitTask getTask() {
        return task;
    }

    public void start() {
        reload();
        task = new ShowRunner(this).runTaskTimer(DiscovShows.getInstance(), 0L, 1L);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
        }
    }

    public void reload() {
        name = StorageUtil.getName(filePath);
        schedule = StorageUtil.getSchedule(filePath);
        steps = StorageUtil.getSteps(filePath);
    }
}
