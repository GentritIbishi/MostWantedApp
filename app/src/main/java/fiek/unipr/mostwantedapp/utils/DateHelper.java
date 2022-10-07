package fiek.unipr.mostwantedapp.utils;

import static java.util.Calendar.getInstance;
import static fiek.unipr.mostwantedapp.utils.Constants.DATE;
import static fiek.unipr.mostwantedapp.utils.Constants.DATE_TIME;
import static fiek.unipr.mostwantedapp.utils.Constants.DATE_TIME_STYLE;
import static fiek.unipr.mostwantedapp.utils.Constants.DATE_TIME_STYLE_2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateHelper {

    public static String getDateTime() {
        try {
            Date netDate = new Date(); // current time from here
            SimpleDateFormat sfd = new SimpleDateFormat(DATE_TIME, Locale.getDefault());
            return sfd.format(netDate);
        } catch (Exception e) {
            return "datetime";
        }
    }

    public static String getDate() {
        try {
            Date netDate = new Date(); // current time from here
            SimpleDateFormat sfd = new SimpleDateFormat(DATE, Locale.getDefault());
            return sfd.format(netDate);
        } catch (Exception e) {
            return "date";
        }
    }

    public static String getDateTimeStyle() {
        try {
            Date netDate = new Date(); // current time from here
            SimpleDateFormat sfd = new SimpleDateFormat(DATE_TIME_STYLE, Locale.getDefault());
            return sfd.format(netDate);
        } catch (Exception e) {
            return "date";
        }
    }

    public static Boolean isAccountOlder(Integer days, Integer yyyy, Integer mm, Integer dd) {
        LocalDate now = LocalDate.now();
        LocalDate date1 = LocalDate.of(yyyy, mm, dd);

        long day1 = ChronoUnit.DAYS.between(now, date1);

        if (day1 <= -days) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isDateInCurrentMonth(String date) throws ParseException {
        //Create 2 instances of Calendar
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        //set the given date in one of the instance and current date in the other
        Date givenDate = new SimpleDateFormat(DATE).parse(date);
        cal1.setTime(givenDate);
        cal2.setTime(new Date());

        //now compare the dates using methods on Calendar
        if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR))
        {
            if (cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH))
            {
                return true;
            }else
            {
                return false;
            }
        }else {
            return false;
        }
    }

    public static String printDifference(Date startDate, Date endDate) {
        //milliseconds
        String time_elapsed = "";
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

        long weeks = elapsedDays / 7;

        if (weeks != 0) {
            return time_elapsed = weeks + "w ";
        } else if (elapsedDays != 0) {
            return time_elapsed = elapsedDays + "d ";
        } else if (elapsedHours != 0) {
            return time_elapsed = elapsedHours + "h ";
        } else if (elapsedMinutes != 0) {
            return time_elapsed = elapsedMinutes + "m ";
        } else if (elapsedSeconds != 0) {
            return time_elapsed = elapsedSeconds + "s ";
        } else {
            return time_elapsed;
        }
    }

    public static Date yesterday() {
        final Calendar cal = getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    public static String getYesterday() { // without parameter argument
        try {
            SimpleDateFormat sfd = new SimpleDateFormat(DATE, Locale.getDefault());
            return sfd.format(yesterday());
        } catch (Exception e) {
            return "date";
        }
    }

    public static String getFirstDayOfThisWeek() {
        try {
            Calendar calendar = new GregorianCalendar();
            Date date = new Date();
            calendar.clear();
            calendar.setTime(firstDayOfWeek(date));
            int year = calendar.get(Calendar.YEAR);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH) + 1;
            SimpleDateFormat sfd = new SimpleDateFormat(DATE);
            sfd.setTimeZone(calendar.getTimeZone());
            return sfd.format(calendar.getTime());
        } catch (Exception e) {
            return "date" + e.getMessage();
        }
    }

    public static Date firstDayOfWeek(Date date) {
        Calendar calendar = getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK, 1);
        return calendar.getTime();
    }

    public static String getFirstDayOfLastWeek() {
        try {
            Calendar calendar = getInstance();
            calendar = firstDayOfLastWeek(calendar);
            SimpleDateFormat sfd = new SimpleDateFormat(DATE);
            sfd.setTimeZone(calendar.getTimeZone());
            return sfd.format(calendar.getTime());
        } catch (Exception e) {
            return "date";
        }
    }

    public static Calendar firstDayOfLastWeek(Calendar c) {
        c = (Calendar) c.clone();
        // last week
        c.add(Calendar.WEEK_OF_YEAR, -1);
        // first day
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        return c;
    }

    public static String getNextMonthFirstDay() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_STYLE_2);
        return LocalDate.now().with(TemporalAdjusters.firstDayOfNextMonth())
                .format(dateTimeFormatter);
    }

}
