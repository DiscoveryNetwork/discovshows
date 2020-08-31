package nl.parrotlync.discovshows.task;

import nl.parrotlync.discovshows.DiscovShows;
import nl.parrotlync.discovshows.model.Show;
import nl.parrotlync.discovshows.util.ChatUtil;
import org.apache.commons.lang.time.DateUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ShowChecker extends BukkitRunnable {
    private SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
    private CommandSender sender;

    public ShowChecker() {
        this.sender = Bukkit.getServer().getConsoleSender();
    }

    @Override
    public void run() {
        List<Show> shows = DiscovShows.getInstance().getShowManager().getShows();
        Date currentTime = new Date();
        for (Show show : shows) {
            if (show.getSchedule() != null) {
                if (formatter.format(DateUtils.addMinutes(currentTime, 10)).equals(formatter.format(show.getSchedule().getTime()))) {
                    if (show.getPreCommand(10) != null) {
                        Bukkit.dispatchCommand(sender, show.getPreCommand(10));
                    }
                }

                else if (formatter.format(DateUtils.addMinutes(currentTime, 5)).equals(formatter.format(show.getSchedule().getTime()))) {
                    if (show.getPreCommand(5) != null) {
                        Bukkit.dispatchCommand(sender, show.getPreCommand(5));
                    }
                }

                else if (formatter.format(DateUtils.addMinutes(currentTime, 1)).equals(formatter.format(show.getSchedule().getTime()))) {
                    if (show.getPreCommand(1) != null) {
                        Bukkit.dispatchCommand(sender, show.getPreCommand(1));
                    }
                }

                if (formatter.format(currentTime.getTime()).equals(formatter.format(show.getSchedule().getTime()))) {
                    show.start();
                }
            }
        }
    }
}
