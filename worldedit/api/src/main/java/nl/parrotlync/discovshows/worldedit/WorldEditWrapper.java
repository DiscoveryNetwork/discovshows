package nl.parrotlync.discovshows.worldedit;

import com.sk89q.worldedit.WorldEditException;
import org.bukkit.Location;

import java.io.File;
import java.io.IOException;

public interface WorldEditWrapper {

    public void pasteSchematic(Location location, File file) throws IOException, WorldEditException;
}
