package nl.parrotlync.discovshows.task;

import nl.parrotlync.discovshows.model.Fountain;
import org.bukkit.scheduler.BukkitRunnable;

public class FountainTask extends BukkitRunnable {
    private Fountain fountain;
    private int count = 0;
    private double interval = 0.04D;

    public FountainTask(final Fountain fountain) {
        this.fountain = fountain;
    }

    @Override
    public void run() {
        if (count == fountain.getRunTime() / 2) {
            interval -= 2.0E-4D;
        }

        fountain.spawnFountain();
        ++count;
    }
}
