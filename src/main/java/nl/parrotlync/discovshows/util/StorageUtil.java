package nl.parrotlync.discovshows.util;

import nl.parrotlync.discovshows.DiscovShows;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class StorageUtil {
    private static final SimpleDateFormat weekFormatter = new SimpleDateFormat("EEEE HH:mm");
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    private static final HashMap<String, List<Date>> customScheduled = new HashMap<>();

    private static YamlConfiguration getConfig(String path) {
        File file = new File(path);
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return config;
    }

    public static boolean checkConfig(String path) {
        File file = new File(path);
        if (!file.isFile()) { return false; }
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
            return true;
        } catch (Exception e) {
            DiscovShows.getInstance().getLogger().info("Error while loading show file at " + path);
            DiscovShows.getInstance().getLogger().info("Please check this file for YAML errors");
            e.printStackTrace();
        }
        return false;
    }

    public static String getName(String path) {
        YamlConfiguration config = getConfig(path);
        return config.getString("name");
    }

    public static Boolean getRepeat(String path) {
        YamlConfiguration config = getConfig(path);
        return config.getBoolean("repeat");
    }

    public static Boolean getDiscordEnabled(String path) {
        YamlConfiguration config = getConfig(path);
        return config.getBoolean("discord.enabled");
    }

    public static String getDiscordMessage(String path) {
        YamlConfiguration config = getConfig(path);
        return config.getString("discord.message");
    }

    public static HashMap<Integer, List<String>> getCommands(String path) {
        YamlConfiguration config = getConfig(path);
        HashMap<Integer, List<String>> commandMap = new HashMap<>();
        ConfigurationSection commands = config.getConfigurationSection("commands");
        if (commands != null) {
            for (String preCmd : commands.getKeys(false)) {
                if (preCmd.equalsIgnoreCase("after")) {
                    commandMap.put(0, commands.getStringList("after"));
                    continue;
                }
                commandMap.put(Integer.parseInt(preCmd.split("-")[1]), commands.getStringList(preCmd));
            }
        }
        return commandMap;
    }

    public static HashMap<Integer, List<String>> getSteps(String path) {
        YamlConfiguration config = getConfig(path);
        ConfigurationSection stepSection = config.getConfigurationSection("steps");
        HashMap<Integer, List<String>> steps = new HashMap<>();
        for (String offset : stepSection.getKeys(false)) {
            steps.put(Integer.parseInt(offset), stepSection.getStringList(offset));
        }
        return steps;
    }

    public static List<Date> getSchedules(String path) {
        YamlConfiguration config = getConfig(path);
        List<Date> showDates = new ArrayList<>();
        ConfigurationSection schedules = config.getConfigurationSection("schedule");
        if (schedules != null) {
            for (String schedule : schedules.getKeys(false)) {
                if (schedule.equals("once")) { continue; }
                try {
                    for (String time : schedules.getStringList(schedule)) {
                        Date date = weekFormatter.parse(schedule + " " + time);
                        showDates.add(date);
                    }
                } catch (ParseException e) {
                    DiscovShows.getInstance().getLogger().info("Something went wrong while parsing schedules for file '" + path + "'");
                }
            }
        }
        return showDates;
    }

    public static List<Date> getCustomSchedules(String path) {
        YamlConfiguration config = getConfig(path);
        List<Date> showDates = new ArrayList<>();
        if (customScheduled.get(path) != null) {
            showDates.addAll(customScheduled.get(path));
        }
        ConfigurationSection schedules = config.getConfigurationSection("schedule");
        if (schedules != null) {
            if (schedules.getStringList("once") != null) {
                for (String schedule : schedules.getStringList("once")) {
                    try {
                        Date date = dateFormatter.parse(schedule);
                        showDates.add(date);
                    } catch (ParseException e) {
                        DiscovShows.getInstance().getLogger().info("Something went wrong while parsing custom schedules for file '" + path + "'");
                    }
                }
            }
        }
        return showDates;
    }

    public static void addCustomSchedule(Date date, String path) {
        if (customScheduled.get(path) == null) {
            customScheduled.put(path, new ArrayList<Date>());
        }
        customScheduled.get(path).add(date);
    }

    public static void clearCustomSchedule() {
        customScheduled.clear();
    }

}
