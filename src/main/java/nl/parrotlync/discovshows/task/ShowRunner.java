package nl.parrotlync.discovshows.task;

import nl.parrotlync.discovshows.DiscovShows;
import nl.parrotlync.discovshows.model.Show;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.List;

public class ShowRunner extends BukkitRunnable {
    private final Show show;
    private final ConsoleCommandSender sender;
    private Integer ticks;
    private final Integer lastTick;

    public ShowRunner(Show show, Integer ticks) {
        this.show = show;
        this.ticks = ticks;
        this.sender = Bukkit.getServer().getConsoleSender();
        List<Integer> steps = show.getSteps();
        Collections.sort(steps);
        this.lastTick = steps.get(steps.size() - 1);
        DiscovShows.getInstance().getLogger().info("Show has started (" + show.getName() + ")");
    }

    @Override
    public void run() {
        show.updateBossBar(ticks);
        if (show.getStepCommands(ticks) != null) {
            for (String cmd : show.getStepCommands(ticks)) {
                Bukkit.dispatchCommand(sender, cmd);
            }
        }
        if (ticks.equals(lastTick)) {
            if (show.getRepeat() != null && show.getRepeat()) {
                ticks = -1;
            } else {
                this.cancel();
                DiscovShows.getInstance().getLogger().info("Show has finished (" + show.getName() + ")");
                if (show.getCommands(0) != null) {
                    for (String cmd : show.getCommands(0)) {
                        Bukkit.dispatchCommand(sender, cmd);
                    }
                }
                show.updateBossBar(0);
            }
        }
        ticks += 1;
    }
}
