package nl.parrotlync.discovshows.task;

import nl.parrotlync.discovshows.model.Fountain;
import org.bukkit.scheduler.BukkitRunnable;

public class FountainTask extends BukkitRunnable {
    private final Fountain fountain;

    public FountainTask(final Fountain fountain) {
        this.fountain = fountain;
    }

    @Override
    public void run() {
        fountain.spawn();
    }
}
