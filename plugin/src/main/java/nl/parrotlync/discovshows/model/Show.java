package nl.parrotlync.discovshows.model;

import nl.parrotlync.discovshows.DiscovShows;
import nl.parrotlync.discovshows.manager.ScheduleManager;
import nl.parrotlync.discovshows.task.ShowRunner;
import nl.parrotlync.discovshows.util.StorageUtil;
import org.apache.commons.lang.time.DateUtils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.text.ParseException;
import java.util.*;

public class Show {
    private final String identifier;
    private final String filePath;
    private final ScheduleManager scheduleManager = new ScheduleManager();
    private final HashMap<Integer, List<String>> commands = new HashMap<>();
    private final HashMap<Integer, List<String>> steps = new HashMap<>();
    private String name;
    private BukkitTask task;
    private boolean repeat = false;

    public Show(String identifier, String filePath) {
        this.identifier = identifier;
        this.filePath = filePath;
        load();
    }

    public String getName() {
        return name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public boolean getRepeat() {
        return repeat;
    }

    public ScheduleManager getScheduleManager() {
        return scheduleManager;
    }

    public Integer getLastStepKey() {
        return Collections.max(steps.keySet());
    }

    public boolean hasStepsAt(Integer ticks) {
        return steps.get(ticks) != null;
    }

    public List<String> getSteps(Integer ticks) {
        return steps.get(ticks);
    }

    public List<String> getCommands(Integer seconds) {
        return commands.get(seconds);
    }

    public List<Integer> getCommandKeys() {
        return new ArrayList<>(commands.keySet());
    }

    public boolean isRunning() {
        return task != null && !task.isCancelled();
    }

    public boolean isScheduled() {
        if (scheduleManager.getSchedules().size() == 0) { return false; }
        int totalShowTime = (Collections.max(steps.keySet()) / 20) + Collections.max(commands.keySet()) + 15;

        try {
            Date regularNow = ShowDates.getDayTimeFormat().parse(ShowDates.getDayTimeFormat().format(new Date()));
            Date customNow = ShowDates.getDateTimeFormat().parse(ShowDates.getDateTimeFormat().format(new Date()));
            for (Date date : scheduleManager.getSchedules()) {
                if (scheduleManager.getScheduleType(date) == ScheduleType.REGULAR) {
                    if (DateUtils.addSeconds(regularNow, totalShowTime).compareTo(date) >= 0 && regularNow.compareTo(DateUtils.addSeconds(date, totalShowTime)) <= 0) {
                        return true;
                    }
                }

                if (scheduleManager.getScheduleType(date) == ScheduleType.CUSTOM) {
                    if (DateUtils.addSeconds(customNow, totalShowTime).compareTo(date) >= 0 && customNow.compareTo(DateUtils.addSeconds(date, totalShowTime)) <= 0) {
                        return true;
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void schedule() {
        if (task == null || task.isCancelled()) {
            try {
                Date now = ShowDates.getDateTimeFormat().parse(ShowDates.getDateTimeFormat().format(new Date()));
                if (!commands.isEmpty()) {
                    scheduleManager.addSchedule(DateUtils.addSeconds(now, Collections.max(commands.keySet()) + 5), ScheduleType.CUSTOM);
                    return;
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return;
            }
            start();
        }
    }

    public void start() {
        if (task == null || task.isCancelled()) {
            task = new ShowRunner(this).runTaskTimer(DiscovShows.getInstance(), 0L, 1L);
        }
    }

    public void stop() {
        if (task != null && !task.isCancelled()) {
            task.cancel();
            executeCommandsAfter();
        }
    }

    public void executeCommandsAfter() {
        if (commands.get(0) != null) {
            for (String command : commands.get(0)) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }
    }

    private void load() {
        name = StorageUtil.getName(filePath);
        repeat = StorageUtil.getRepeat(filePath);
        scheduleManager.load(filePath);

        steps.clear();
        HashMap<Integer, List<String>> steps = StorageUtil.getSteps(filePath);
        for (Integer key : steps.keySet()) {
            this.steps.put(key, steps.get(key));
        }

        commands.clear();
        HashMap<Integer, List<String>> commands = StorageUtil.getCommands(filePath);
        for (Integer key : commands.keySet()) {
            this.commands.put(key, commands.get(key));
        }
    }
}
