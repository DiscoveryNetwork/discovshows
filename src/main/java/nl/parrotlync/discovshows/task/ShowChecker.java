package nl.parrotlync.discovshows.task;

import nl.parrotlync.discordapi.DiscordAPI;
import nl.parrotlync.discovshows.DiscovShows;
import nl.parrotlync.discovshows.model.ScheduleType;
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
    private final SimpleDateFormat tFormatter = new SimpleDateFormat("HH:mm:ss");
    private final SimpleDateFormat dFormatter = new SimpleDateFormat("dd-MM-yyyy");
    private final SimpleDateFormat wFormatter = new SimpleDateFormat("EEEE");
    private final CommandSender sender;

    public ShowChecker() {
        this.sender = Bukkit.getServer().getConsoleSender();
    }

    @Override
    public void run() {
        try {
            Date now = new Date();
            for (Show show : DiscovShows.getInstance().getShowManager().getShows()) {
                // Weekday schedules
                StorageUtil.getSchedules(show.getFilePath());
                for (Date date : StorageUtil.getSchedules(show.getFilePath())) {
                    if (wFormatter.parse(wFormatter.format(now)).compareTo(wFormatter.parse(wFormatter.format(date))) == 0) {
                        checkShow(show, date, now, ScheduleType.SCHEDULED);
                    }
                }

                // Custom schedules
                StorageUtil.getCustomSchedules(show.getFilePath());
                for (Date date : StorageUtil.getCustomSchedules(show.getFilePath())) {
                    if (dFormatter.parse(dFormatter.format(now)).compareTo(dFormatter.parse(dFormatter.format(date))) == 0) {
                        checkShow(show, date, now, ScheduleType.CUSTOM);
                    }
                }
            }
        } catch (ParseException e) {
            // Don't do too much
        }
    }

    private void checkShow(Show show, Date date, Date now, ScheduleType type) throws ParseException {
        Date currentTime = tFormatter.parse(tFormatter.format(now));
        Date checkTime = tFormatter.parse(tFormatter.format(date));

        if (show.hasDiscordBroadcastEnabled() && type == ScheduleType.SCHEDULED) {
            if (DateUtils.addSeconds(currentTime, 1200).compareTo(checkTime) == 0) {
                String channel = DiscovShows.getInstance().getConfig().getString("staff-channel");
                DiscordAPI.sendEmbed("Discovery Network - Shows", "The show **" + show.getName() + "** is starting in 20 minutes! \nAnnouncements can be made in <#" + DiscovShows.getInstance().getConfig().getString("event-channel") + ">.", 0x61C244, channel);
            }

            if (DateUtils.addSeconds(currentTime, 600).compareTo(checkTime) == 0) {
                if (show.getDiscordMessage() != null) {
                    String channel = DiscovShows.getInstance().getConfig().getString("event-channel");
                    DiscordAPI.sendMessageWithCheck(show.getDiscordMessage(), channel, 600);
                }
            }
        }

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
