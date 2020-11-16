package nl.parrotlync.discovshows.model;

import nl.parrotlync.discovshows.DiscovShows;
import nl.parrotlync.discovshows.task.FountainTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class Fountain {
    private final Location location;
    private final Vector motion;
    private final int runTime;
    private BukkitTask task;

    public Fountain(Location location, Vector motion, Integer runTime) {
        this.location = location;
        this.motion = motion;
        this.runTime = runTime;
    }

    public void run() {
        this.task = new FountainTask(this).runTaskTimer(DiscovShows.getInstance(), 1L, 1L);
        Bukkit.getScheduler().runTaskLater(DiscovShows.getInstance(), new Runnable() {
            @Override
            public void run() {
                Bukkit.getScheduler().cancelTask(task.getTaskId());
            }
        }, runTime);
    }

    public void spawn() {
        MaterialData data = new ItemStack(Material.STAINED_GLASS, 1, (byte) 9).getData();
        FallingBlock block = location.getWorld().spawnFallingBlock(location, data);
        block.setMetadata("Type", new FixedMetadataValue(DiscovShows.getInstance(), "Fountain"));
        block.setDropItem(false);
        block.setVelocity(motion);
    }
}