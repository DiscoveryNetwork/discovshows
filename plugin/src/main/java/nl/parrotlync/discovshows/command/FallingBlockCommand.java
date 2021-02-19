package nl.parrotlync.discovshows.command;

import nl.parrotlync.discovshows.DiscovShows;
import nl.parrotlync.discovshows.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.FallingBlock;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

public class FallingBlockCommand extends ShowCommand {

    public FallingBlockCommand() {
        super("Spawn a series of falling blocks", "discovshows.command.fallingblock");
        arguments.put(0, new PositionalWorldArgument());
        arguments.put(1, new PositionalXArgument());
        arguments.put(2, new PositionalYArgument());
        arguments.put(3, new PositionalZArgument());
        arguments.put(4, new StringTabArgument("dx", new String[] {"0.5"}));
        arguments.put(5, new StringTabArgument("dy", new String[] {"0.5"}));
        arguments.put(6, new StringTabArgument("dz", new String[] {"0.5"}));
        arguments.put(7, new MaterialTabArgument());
        arguments.put(8, new StringTabArgument("runTime", new String[] {"1"}));
        arguments.put(9, new StringTabArgument("blockData", new String[] {"0"}));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        int x, y, z, runTime, blockData;
        float dx, dy, dz;

        Material material = Material.getMaterial(args[7].toUpperCase());
        if (material == null) {
            ChatUtil.sendConfigMessage(sender, "invalid-material");
            return;
        }

        try {
            x = Integer.parseInt(args[1]);
            y = Integer.parseInt(args[2]);
            z = Integer.parseInt(args[3]);
            runTime = Integer.parseInt(args[8]);
            blockData = Integer.parseInt(args[9]);

            dx = Float.parseFloat(args[4]);
            dy = Float.parseFloat(args[5]);
            dz = Float.parseFloat(args[6]);
        } catch (NumberFormatException e) {
            ChatUtil.sendConfigMessage(sender, "number-parse-error");
            return;
        }

        World world = Bukkit.getWorld(args[0]);
        if (world == null) {
            ChatUtil.sendConfigMessage(sender, "invalid-world");
            return;
        }

        Location location = new Location(world, x, y, z);
        Vector motion = new Vector(dx, dy, dz);
        int task = Bukkit.getScheduler().runTaskTimer(DiscovShows.getInstance(), () -> spawnFallingBlock(material, blockData, location, motion), 1L, 1L).getTaskId();
        Bukkit.getScheduler().runTaskLater(DiscovShows.getInstance(), () -> Bukkit.getScheduler().cancelTask(task), runTime);
    }

    private void spawnFallingBlock(Material material, int blockData, Location location, Vector motion) {
        MaterialData data = new ItemStack(material, 1, (byte) blockData).getData();
        FallingBlock block = location.getWorld().spawnFallingBlock(location, data);
        block.setVelocity(motion);
        block.setDropItem(false);
        block.setMetadata("Type", new FixedMetadataValue(DiscovShows.getInstance(), "FallingBlockEffect"));
    }
}
