package nl.parrotlync.discovshows.command;

import nl.parrotlync.discovshows.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import ru.beykerykt.lightapi.LightAPI;
import ru.beykerykt.lightapi.LightType;
import ru.beykerykt.lightapi.chunks.ChunkInfo;

public class LightCommand extends ShowCommand {

    public LightCommand() {
        super("Create or remove an invisible light source (LightAPI)", "discovshows.command.light");
        arguments.put(0, new StringTabArgument("create/remove", new String[] {"create", "remove"}));
        arguments.put(1, new PositionalWorldArgument());
        arguments.put(2, new PositionalXArgument());
        arguments.put(3, new PositionalYArgument());
        arguments.put(4, new PositionalZArgument());
        arguments.put(5, new StringTabArgument("lightLevel", new String[] {"15"}));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        int x, y, z, level;

        try {
            x = Integer.parseInt(args[2]);
            y = Integer.parseInt(args[3]);
            z = Integer.parseInt(args[4]);
            level = Integer.parseInt(args[5]);
        } catch (NumberFormatException e) {
            ChatUtil.sendConfigMessage(sender, "number-parse-error");
            return;
        }

        World world = Bukkit.getWorld(args[1]);
        if (world == null) {
            ChatUtil.sendConfigMessage(sender, "invalid-world");
            return;
        }

        Location location = new Location(world, x, y, z);
        if (args[0].equalsIgnoreCase("create")) {
            LightAPI.createLight(location, LightType.BLOCK, level, false);
            ChatUtil.sendConfigMessage(sender, "light-created", new String[] {args[2], args[3], args[4], world.getName()});
        } else if (args[0].equalsIgnoreCase("remove")) {
            LightAPI.deleteLight(location, LightType.BLOCK, false);
            ChatUtil.sendConfigMessage(sender, "light-removed", new String[] {args[2], args[3], args[4], world.getName()});
        } else {
            ChatUtil.sendConfigMessage(sender, "subcommand-not-found");
            return;
        }

        for (ChunkInfo chunkInfo : LightAPI.collectChunks(location, LightType.BLOCK, level)) {
            LightAPI.updateChunk(chunkInfo, LightType.BLOCK);
        }
    }
}
