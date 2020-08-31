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
    private Boolean repeat;
    private String filePath;
    private HashMap<Integer, String> preCommands;
    private HashMap<Integer, List<String>> steps;

    public Show(String name, Date schedule, HashMap<Integer, List<String>> steps, Boolean repeat, HashMap<Integer, String> preCommands, String filePath) {
        this.name = name;
        this.schedule = schedule;
        this.steps = steps;
        this.repeat = repeat;
        this.preCommands = preCommands;
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

    public Boolean getRepeat() {
        return repeat;
    }

    public BukkitTask getTask() {
        return task;
    }

    public String getPreCommand(Integer minutes) {
        return preCommands.get(minutes);
    }

    public void start() {
        reload();
        stop();
        task = new ShowRunner(this).runTaskTimer(DiscovShows.getInstance(), 0L, 1L);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
        }
    }

    private void reload() {
        name = StorageUtil.getName(filePath);
        schedule = StorageUtil.getSchedule(filePath);
        steps = StorageUtil.getSteps(filePath);
        repeat = StorageUtil.getRepeat(filePath);
        preCommands = StorageUtil.getCommands(filePath);
    }
}
