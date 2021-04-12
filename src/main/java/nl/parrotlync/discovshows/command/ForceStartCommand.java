package nl.parrotlync.discovshows.command;

import nl.parrotlync.discovshows.DiscovShows;
import nl.parrotlync.discovshows.model.Show;
import nl.parrotlync.discovshows.util.ChatUtil;
import org.bukkit.command.CommandSender;

public class ForceStartCommand extends ShowCommand {

    public ForceStartCommand() {
        super("Force start a show", "discovshows.command.forcestart");
        arguments.put(0, new ShowTabArgument());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Show show = DiscovShows.getInstance().getShowManager().getShow(args[0]);
        if (show == null) {
            ChatUtil.sendConfigMessage(sender, "show-not-found", args[0]);
            return;
        }

        if (show.isRunning()) {
            ChatUtil.sendConfigMessage(sender, "show-already-running");
            return;
        }

        show.start();
        ChatUtil.sendConfigMessage(sender, "show-scheduled", show.getName());
    }
}
