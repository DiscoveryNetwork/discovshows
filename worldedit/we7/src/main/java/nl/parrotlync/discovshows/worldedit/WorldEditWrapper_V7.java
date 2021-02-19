package nl.parrotlync.discovshows.worldedit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import org.bukkit.Location;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class WorldEditWrapper_V7 implements WorldEditWrapper {

    @Override
    public void pasteSchematic(Location location, File file) throws IOException, WorldEditException {
        World world = new BukkitWorld(location.getWorld());
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        assert format != null;

        ClipboardReader reader = format.getReader(new FileInputStream(file));
        EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1);
        Operation operation = new ClipboardHolder(reader.read())
                .createPaste(editSession)
                .to(BlockVector3.at(location.getX(), location.getY(), location.getZ()))
                .ignoreAirBlocks(true)
                .build();
        Operations.complete(operation);
    }
}
