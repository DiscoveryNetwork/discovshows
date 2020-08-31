package nl.parrotlync.discovshows.model;

import nl.parrotlync.discovshows.DiscovShows;
import nl.parrotlync.discovshows.task.FountainTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.FallingBlock;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class Fountain {
    private Location location;
    private Vector motion;
    private World world;
    private BukkitTask task;
    private int runTime;
    private String blockId;
    private int blockData;

    public Fountain(String[] args) {
        world = Bukkit.getWorld(args[10]);
        location = new Location(world, (double) Float.parseFloat(args[2]), (double) Float.parseFloat(args[3]), (double) Float.parseFloat(args[4]));
        motion = new Vector(Float.parseFloat(args[5]), Float.parseFloat(args[6]), Float.parseFloat(args[7]));
        runTime = Integer.parseInt(args[10]);
        blockId = args[8];
        blockData = Integer.parseInt(args[9]);
        task = new FountainTask(this).runTaskTimer(DiscovShows.getInstance(), 1L, 1L);
        Bukkit.getScheduler().runTaskLater(DiscovShows.getInstance(), new Runnable() {
            @Override
            public void run() {
                Bukkit.getScheduler().cancelTask(task.getTaskId());
            }
        }, (long) runTime);
    }

    public int getRunTime() {
        return runTime;
    }

    public FallingBlock spawnFountain() {
        MaterialData data = new ItemStack(Material.STAINED_CLAY, 1, (byte) blockData).getData();
        FallingBlock block = world.spawnFallingBlock(location, data);
        block.setDropItem(false);
        block.setVelocity(motion);
        return block;
    }
}
