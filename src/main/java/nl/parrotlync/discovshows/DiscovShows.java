package nl.parrotlync.discovshows;

import nl.parrotlync.discovshows.command.ShowCommandExecutor;
import nl.parrotlync.discovshows.listener.ShowListener;
import nl.parrotlync.discovshows.manager.ShowManager;
import nl.parrotlync.discovshows.task.ShowChecker;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class DiscovShows extends JavaPlugin {
    private ShowManager showManager;
    private static DiscovShows instance;

    public DiscovShows() {
        showManager = new ShowManager();
        instance = this;
        File dir = new File("plugins/DiscovShows/");
        if (!dir.exists()) { dir.mkdir(); }
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        try {
            showManager.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
        getServer().getPluginManager().registerEvents(new ShowListener(), this);
        this.getCommand("show").setExecutor(new ShowCommandExecutor());
        getLogger().info("DiscovShows is now enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("DiscovShows is now disabled!");
    }

    public static DiscovShows getInstance() { return instance; }

    public ShowManager getShowManager() { return showManager; }
}
