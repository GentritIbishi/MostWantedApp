package fiek.unipr.mostwantedapp.utils;

import java.util.Calendar;
import java.util.Timer;

public class ScheduleTaskTimer {

    public static void setPayoutPaypalTask(int hour, int minute, int second, int millisecond) {
        Timer timer = new Timer();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, millisecond);
        timer.schedule(new PayoutsPaypalTask(), cal.getTime());
    }

}
