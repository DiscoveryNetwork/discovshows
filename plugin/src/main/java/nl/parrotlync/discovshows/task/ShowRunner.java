package nl.parrotlync.discovshows.task;

import nl.parrotlync.discovshows.DiscovShows;
import nl.parrotlync.discovshows.model.Show;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class ShowRunner extends BukkitRunnable {
    private final Show show;
    private Integer ticks = 0;


    public ShowRunner(Show show) {
        this.show = show;
        DiscovShows.getInstance().getLogger().info(String.format("Show has started (%s)", show.getName()));
    }

    @Override
    public void run() {
        if (show.hasStepsAt(ticks)) {
            for (String command : show.getSteps(ticks)) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }

        if (ticks.equals(show.getLastStepKey())) {
            if (!show.getRepeat()) {
                this.cancel();
                show.executeCommandsAfter();
                DiscovShows.getInstance().getLogger().info("Show has finished (" + show.getName() + ")");
            } else {
                ticks = -1;
            }
        }

        ticks += 1;
    }
}
