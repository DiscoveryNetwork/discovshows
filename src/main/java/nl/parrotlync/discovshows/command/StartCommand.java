package nl.parrotlync.discovshows.command;

import nl.parrotlync.discovshows.DiscovShows;
import nl.parrotlync.discovshows.model.Show;
import nl.parrotlync.discovshows.util.ChatUtil;
import org.bukkit.command.CommandSender;

public class StartCommand extends ShowCommand {

    public StartCommand() {
        super("Start a show", "discovshows.command.start");
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

        if (show.isScheduled()) {
            ChatUtil.sendConfigMessage(sender, "show-already-scheduled");
            return;
        }

        show.schedule();
        ChatUtil.sendConfigMessage(sender, "show-scheduled", show.getName());
    }
}
