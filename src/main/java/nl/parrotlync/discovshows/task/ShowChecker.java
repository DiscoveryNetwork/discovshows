package nl.parrotlync.discovshows.task;

import nl.parrotlync.discovshows.DiscovShows;
import nl.parrotlync.discovshows.model.Show;
import nl.parrotlync.discovshows.util.StorageUtil;
import org.apache.commons.lang.time.DateUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ShowChecker extends BukkitRunnable {
    private SimpleDateFormat tFormatter = new SimpleDateFormat("HH:mm:ss");
    private SimpleDateFormat dFormatter = new SimpleDateFormat("dd-MM-yyyy");
    private SimpleDateFormat wFormatter = new SimpleDateFormat("EEEE");
    private CommandSender sender;

    public ShowChecker() {
        this.sender = Bukkit.getServer().getConsoleSender();
    }

    @Override
    public void run() {
        try {
            Date now = new Date();
            for (Show show : DiscovShows.getInstance().getShowManager().getShows()) {
                // Weekday schedules
                if (StorageUtil.getSchedules(show.getFilePath()) != null) {
                    for (Date date : StorageUtil.getSchedules(show.getFilePath())) {
                        if (wFormatter.parse(wFormatter.format(now)).compareTo(wFormatter.parse(wFormatter.format(date))) == 0) {
                            checkShow(show, date, now);
                        }
                    }
                }

                // Custom schedules
                if (StorageUtil.getCustomSchedules(show.getFilePath()) != null) {
                    for (Date date : StorageUtil.getCustomSchedules(show.getFilePath())) {
                        if (dFormatter.parse(dFormatter.format(now)).compareTo(dFormatter.parse(dFormatter.format(date))) == 0) {
                            checkShow(show, date, now);
                        }
                    }
                }
            }
        } catch (ParseException e) {
            // Don't do too much
        }
    }

    private void checkShow(Show show, Date date, Date now) throws ParseException {
        Date currentTime = tFormatter.parse(tFormatter.format(now));
        Date checkTime = tFormatter.parse(tFormatter.format(date));

        for (int seconds : show.getCommandKeys()) {
            if (seconds == 0) { continue; }
            if (DateUtils.addSeconds(currentTime, seconds).compareTo(checkTime) == 0) {
                if (show.getCommands(seconds) != null) {
                    executeCommands(show.getCommands(seconds));
                    return;
                }
            }
        }

        if (currentTime.compareTo(checkTime) == 0) {
            show.start(0);
        }
    }

    private void executeCommands(final List<String> commands) {
        Bukkit.getScheduler().runTask(DiscovShows.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (String cmd : commands) {
                    Bukkit.dispatchCommand(sender, cmd);
                }
            }
        });
    }
}
