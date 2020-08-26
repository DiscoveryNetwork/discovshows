package nl.parrotlync.discovshows.task;

import nl.parrotlync.discovshows.DiscovShows;
import nl.parrotlync.discovshows.model.Show;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ShowChecker extends BukkitRunnable {
    private SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");

    @Override
    public void run() {
        List<Show> shows = DiscovShows.getInstance().getShowManager().getShows();
        Date currentTime = new Date();
        for (Show show : shows) {
            if (show.getSchedule() != null) {
                if (formatter.format(currentTime.getTime()).equals(formatter.format(show.getSchedule().getTime()))) {
                    show.start();
                }
            }
        }
    }
}
