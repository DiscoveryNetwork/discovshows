package nl.parrotlync.discovshows.manager;

import nl.parrotlync.discovshows.model.ScheduleType;
import nl.parrotlync.discovshows.util.StorageUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ScheduleManager {
    private final HashMap<Date, ScheduleType> schedules = new HashMap<>();

    public void addSchedule(Date date, ScheduleType type) {
        schedules.put(date, type);
    }

    public void removeSchedule(Date date) { schedules.remove(date); }

    public List<Date> getSchedules() {
        return new ArrayList<>(this.schedules.keySet());
    }

    public ScheduleType getScheduleType(Date date) {
        return schedules.get(date);
    }

    public void load(String filePath) {
        schedules.clear();
        for (Date date : StorageUtil.getSchedules(filePath)) {
            schedules.put(date, ScheduleType.REGULAR);
        }

        for (Date date : StorageUtil.getCustomSchedules(filePath)) {
            schedules.put(date, ScheduleType.CUSTOM);
        }
    }
}
