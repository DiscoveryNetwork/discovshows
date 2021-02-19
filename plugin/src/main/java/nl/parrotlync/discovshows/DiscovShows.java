package nl.parrotlync.discovshows;

import nl.parrotlync.discovshows.command.ShowCommandExecutor;
import nl.parrotlync.discovshows.listener.EventListener;
import nl.parrotlync.discovshows.manager.ShowManager;
import nl.parrotlync.discovshows.task.ShowScheduler;
import nl.parrotlync.discovshows.worldedit.WorldEditWrapper;
import nl.parrotlync.discovshows.worldedit.WorldEditWrapper_V6;
import nl.parrotlync.discovshows.worldedit.WorldEditWrapper_V7;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class DiscovShows extends JavaPlugin {
    private static DiscovShows instance;
    private final ShowManager showManager = new ShowManager();
    private WorldEditWrapper worldEditWrapper = null;

    public DiscovShows() {
        instance = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        showManager.load();

        if (getServer().getPluginManager().isPluginEnabled("WorldEdit")) {
            Plugin plugin = getServer().getPluginManager().getPlugin("WorldEdit");
            assert plugin != null;
            if (plugin.getDescription().getVersion().startsWith("6.")) {
                worldEditWrapper = new WorldEditWrapper_V6();
                getLogger().info("Found WorldEdit V6! Enabling support...");
            } else if (plugin.getDescription().getVersion().startsWith("7.")) {
                worldEditWrapper = new WorldEditWrapper_V7();
                getLogger().info("Found WorldEdit V7! Enabling support...");
            }
        }
        getCommand("show").setExecutor(new ShowCommandExecutor());
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

    public WorldEditWrapper getWorldEditWrapper() {
        return worldEditWrapper;
    }

    public static DiscovShows getInstance() { return instance; }
}
