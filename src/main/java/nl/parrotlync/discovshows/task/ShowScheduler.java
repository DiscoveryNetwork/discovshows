package nl.parrotlync.discovshows.task;

import nl.parrotlync.discovshows.DiscovShows;
import nl.parrotlync.discovshows.model.ScheduleType;
import nl.parrotlync.discovshows.model.Show;
import nl.parrotlync.discovshows.model.ShowDates;
import org.apache.commons.lang.time.DateUtils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.ParseException;
import java.util.Date;

public class ShowScheduler extends BukkitRunnable {

    @Override
    public void run() {
        Date now = new Date();
        for (Show show : DiscovShows.getInstance().getShowManager().getShows()) {
            for (Date date : show.getScheduleManager().getSchedules()) {
                try {
                    if (show.getScheduleManager().getScheduleType(date) == ScheduleType.REGULAR) {
                        if (ShowDates.getDayFormat().parse(ShowDates.getDayFormat().format(now)).compareTo(ShowDates.getDayFormat().parse(ShowDates.getDayFormat().format(date))) == 0) {
                            checkSchedule(show, date, now);
                        }
                    }

                    if (show.getScheduleManager().getScheduleType(date) == ScheduleType.CUSTOM) {
                        if (ShowDates.getDateFormat().parse(ShowDates.getDateFormat().format(now)).compareTo(ShowDates.getDateFormat().parse(ShowDates.getDateFormat().format(date))) == 0) {
                            checkSchedule(show, date, now);
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void checkSchedule(Show show, Date date, Date now) throws ParseException {
        Date currentTime = ShowDates.getTimeFormat().parse(ShowDates.getTimeFormat().format(now));
        Date checkTime = ShowDates.getTimeFormat().parse(ShowDates.getTimeFormat().format(date));

        if (currentTime.compareTo(checkTime) == 0) {
            show.start();
            show.getScheduleManager().removeSchedule(date);
            return;
        }

        for (int seconds : show.getCommandKeys()) {
            if (seconds == 0) { continue; }
            if (DateUtils.addSeconds(currentTime, seconds).compareTo(checkTime) == 0) {
                Bukkit.getScheduler().runTask(DiscovShows.getInstance(), () -> {
                    for (String command : show.getCommands(seconds)) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                    }
                });
            }
        }
    }
}
