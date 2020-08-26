package nl.parrotlync.discovshows.util;

import nl.parrotlync.discovshows.model.Show;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class StorageUtil {

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

    public static String getName(String path) {
        YamlConfiguration config = getConfig(path);
        return config.getString("name");
    }

    public static Date getSchedule(String path) {
        YamlConfiguration config = getConfig(path);
        String schedule = config.getString("schedule");
        if (schedule != null && !schedule.equals("0")) {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
            try {
                return formatter.parse(schedule);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
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
}
