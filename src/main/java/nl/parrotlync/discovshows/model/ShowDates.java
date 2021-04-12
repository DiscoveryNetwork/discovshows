package nl.parrotlync.discovshows.model;

import java.text.SimpleDateFormat;

public class ShowDates {

    public static SimpleDateFormat getDayFormat() {
        return new SimpleDateFormat("EEEE");
    }

    public static SimpleDateFormat getDayTimeFormat() {
        return new SimpleDateFormat("EEEE HH:mm");
    }

    public static SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat("dd-MM-yyyy");
    }

    public static SimpleDateFormat getDateTimeFormat() {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    }

    public static SimpleDateFormat getTimeFormat() {
        return new SimpleDateFormat("HH:mm:ss");
    }
}
