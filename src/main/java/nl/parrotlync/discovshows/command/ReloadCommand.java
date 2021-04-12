package nl.parrotlync.discovshows.command;

import nl.parrotlync.discovshows.DiscovShows;
import nl.parrotlync.discovshows.util.ChatUtil;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends ShowCommand {

    public ReloadCommand() {
        super("Reload all shows & files", "discovshows.command.reload");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        DiscovShows.getInstance().getShowManager().load();
        ChatUtil.sendConfigMessage(sender, "shows-reloaded");
    }
}
