package nl.parrotlync.discovshows;

import nl.parrotlync.discovshows.command.ShowCommandExecutor;
import nl.parrotlync.discovshows.listener.EventListener;
import nl.parrotlync.discovshows.manager.ShowManager;
import nl.parrotlync.discovshows.task.ShowScheduler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class DiscovShows extends JavaPlugin {
    private static DiscovShows instance;
    private final ShowManager showManager = new ShowManager();

    public DiscovShows() {
        instance = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        showManager.load();

        Objects.requireNonNull(getCommand("show")).setExecutor(new ShowCommandExecutor());
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        getLogger().info("DiscovShows is now enabled!");
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(DiscovShows.getInstance());
        getLogger().info("DiscovShows is now disabled!");
    }

    public void startShowScheduler() {
        new ShowScheduler().runTaskTimerAsynchronously(DiscovShows.getInstance(), 0L, 20L);
    }

    public ShowManager getShowManager() {
        return showManager;
    }

    public static DiscovShows getInstance() { return instance; }
}
