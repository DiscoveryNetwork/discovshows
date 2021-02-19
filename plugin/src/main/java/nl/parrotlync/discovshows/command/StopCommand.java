package nl.parrotlync.discovshows.command;

import nl.parrotlync.discovshows.DiscovShows;
import nl.parrotlync.discovshows.model.Show;
import nl.parrotlync.discovshows.util.ChatUtil;
import org.bukkit.command.CommandSender;

public class StopCommand extends ShowCommand {

    public StopCommand() {
        super("Stop a show", "discovshows.command.start");
        arguments.put(0, new ShowTabArgument());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Show show = DiscovShows.getInstance().getShowManager().getShow(args[0]);
        if (show == null) {
            ChatUtil.sendConfigMessage(sender, "show-not-found", args[0]);
            return;
        }

        if (!show.isRunning()) {
            ChatUtil.sendConfigMessage(sender, "show-not-running");
            return;
        }

        show.stop();
        ChatUtil.sendConfigMessage(sender, "show-stopped", show.getName());
    }
}
