package fiek.unipr.mostwantedapp.helpers;

import static fiek.unipr.mostwantedapp.helpers.Constants.DATE;
import static fiek.unipr.mostwantedapp.helpers.Constants.DATE_TIME;

import java.text.SimpleDateFormat;
import java.util.Date;
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

}
