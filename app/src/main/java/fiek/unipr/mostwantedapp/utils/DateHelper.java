package fiek.unipr.mostwantedapp.utils;

import static fiek.unipr.mostwantedapp.utils.Constants.DATE;
import static fiek.unipr.mostwantedapp.utils.Constants.DATE_TIME;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateHelper {

    public static String getDateTime() {
        try{
            Date netDate = new Date(); // current time from here
            SimpleDateFormat sfd = new SimpleDateFormat(DATE_TIME, Locale.getDefault());
            return sfd.format(netDate);
        } catch(Exception e) {
            return "datetime";
        }
    }

    public static String getDate() {
        try{
            Date netDate = new Date(); // current time from here
            SimpleDateFormat sfd = new SimpleDateFormat(DATE, Locale.getDefault());
            return sfd.format(netDate);
        } catch(Exception e) {
            return "date";
        }
    }

    public static void printDifference(Date startDate, Date endDate, String time_elapsed) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        long weeks = elapsedDays/7;

        if(weeks != 0){
            time_elapsed = weeks+"w ";
        }else if(elapsedDays != 0) {
            time_elapsed = elapsedDays+"d ";
        }else if(elapsedHours != 0){
            time_elapsed = elapsedHours+"h ";
        }else if(elapsedMinutes != 0){
            time_elapsed = elapsedMinutes+"m ";
        }else if(elapsedSeconds != 0){
            time_elapsed = elapsedSeconds+"s ";
        }

    }

    public static Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    public static String getYesterday() { // without parameter argument
        try{
            SimpleDateFormat sfd = new SimpleDateFormat(DATE, Locale.getDefault());
            return sfd.format(yesterday());
        } catch(Exception e) {
            return "date";
        }
    }

    public static String getFirstDayOfThisWeek() {
        try{
            Calendar calendar = new GregorianCalendar();
            Date date = new Date();
            calendar.clear();
            calendar.setTime(firstDayOfWeek(date));
            int year = calendar.get(Calendar.YEAR);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH)+1;
            SimpleDateFormat sfd = new SimpleDateFormat(DATE);
            sfd.setTimeZone(calendar.getTimeZone());
            return sfd.format(calendar.getTime());
        } catch(Exception e) {
            return "date"+e.getMessage();
        }
    }

    public static Date firstDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK, 1);
        return calendar.getTime();
    }

    public static String getFirstDayOfLastWeek() {
        try{
            Calendar calendar = Calendar.getInstance();
            calendar = firstDayOfLastWeek(calendar);
            SimpleDateFormat sfd = new SimpleDateFormat(DATE);
            sfd.setTimeZone(calendar.getTimeZone());
            return sfd.format(calendar.getTime());
        } catch(Exception e) {
            return "date";
        }
    }

    public static Calendar firstDayOfLastWeek(Calendar c)
    {
        c = (Calendar) c.clone();
        // last week
        c.add(Calendar.WEEK_OF_YEAR, -1);
        // first day
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        return c;
    }

}
