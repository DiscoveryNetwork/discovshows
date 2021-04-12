package nl.parrotlync.discovshows.command;

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
import nl.parrotlync.discovshows.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class PasteCommand extends ShowCommand {

    public PasteCommand() {
        super("Paste a schematic at a given location (WorldEdit)", "discovshows.command.paste");
        arguments.put(0, new PositionalWorldArgument());
        arguments.put(1, new PositionalXArgument());
        arguments.put(2, new PositionalYArgument());
        arguments.put(3, new PositionalZArgument());
        arguments.put(4, new SchematicTabArgument());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        int x, y, z;

        try {
            x = Integer.parseInt(args[1]);
            y = Integer.parseInt(args[2]);
            z = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            ChatUtil.sendConfigMessage(sender, "number-parse-error");
            return;
        }

        World world = Bukkit.getWorld(args[0]);
        if (world == null) {
            ChatUtil.sendConfigMessage(sender, "invalid-world");
            return;
        }

        File file = new File(String.format("plugins/WorldEdit/schematics/%s", args[4]));
        if (!file.exists()) {
            ChatUtil.sendConfigMessage(sender, "file-not-found");
            return;
        }

        Location location = new Location(world, x, y, z);
        try {
            pasteSchematic(location, file);
            ChatUtil.sendConfigMessage(sender, "schematic-paste-success");
        } catch (Exception e) {
            ChatUtil.sendConfigMessage(sender, "schematic-paste-error");
            e.printStackTrace();
        }
    }

    private void pasteSchematic(Location location, File file) throws IOException, WorldEditException {
        com.sk89q.worldedit.world.World world = new BukkitWorld(location.getWorld());
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
