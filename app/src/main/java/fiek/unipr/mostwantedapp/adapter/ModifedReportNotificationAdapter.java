package fiek.unipr.mostwantedapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.maps.report.ReportInfoActivity;
import fiek.unipr.mostwantedapp.models.NotificationReportUser;

public class ModifedReportNotificationAdapter extends ArrayAdapter<NotificationReportUser> {

    private static String FAKE = "FAKE";
    private static String VERIFIED = "VERIFIED";
    private String urlOfProfile, user_report_time_elapsed, informer_person;
    private List<NotificationReportUser> modifiedReportList = null;
    private ArrayList<NotificationReportUser> arraylist;

    public ModifedReportNotificationAdapter(@NonNull Context context, ArrayList<NotificationReportUser> reportArrayList) {
        super(context, 0, reportArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // below line is use to inflate the
        // layout for our item of list view.
        View listitemView = convertView;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.modified_notification_item, parent, false);
        }

        // after inflating an item of listview item
        // we are getting data from array list inside
        // our modal class.
        NotificationReportUser report = getItem(position);

        // initializing our UI components of list view item.
        TextView user_report_modified_name = listitemView.findViewById(R.id.user_report_modified_name);
        TextView user_report_modified_description = listitemView.findViewById(R.id.user_report_modified_description);
        TextView user_report_modified_time = listitemView.findViewById(R.id.user_report_modified_time);

        try {
            // after initializing our items we are
            // setting data to our view.
            // below line is use to set data to our text view.
            user_report_modified_name.setText(getContext().getText(R.string.your_report_in_datetime)+" "+report.getNotificationReportDateTime()+" "+getContext().getText(R.string.has_new_status_right_now));

            String verified = getContext().getText(R.string.status_of_report_has_changed_to)+" "+
                    report.getNotificationReportStatusChangedTo()+ " \n"+ getContext().getText(R.string.you_earn)+20+" "+ "coins\n"+getContext().getText(R.string.thank_you_for_collaboration);

            String fake = getContext().getText(R.string.status_of_report_has_changed_to)+" "+
                    report.getNotificationReportStatusChangedTo()+ " \n"+ getContext().getText(R.string.please_be_real_in_giving_information_next_time)+", "+
                    getContext().getText(R.string.thank_you);

            if(report.getNotificationReportStatusChangedTo().equals(FAKE)){
                user_report_modified_description.setText(fake.substring(0,36)+"...");
            }else if(report.getNotificationReportStatusChangedTo().equals(VERIFIED)){
                user_report_modified_description.setText(verified.substring(0,36)+"...");
            }

            //Your report with date: and title: and decription has been changed to

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");

            Date start_date = simpleDateFormat.parse(report.getNotificationDateTimeChanged());
            Date end_date = simpleDateFormat.parse(getTimeDate());
            printDifference(start_date, end_date);

            if(user_report_time_elapsed != null){
                user_report_modified_time.setText(user_report_time_elapsed);
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }


        // below line is use to add item click listener
        // for our item of list view.
        listitemView.setClickable(true);
        listitemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on the item click on our list view.
                // we are displaying a toast message.
                Intent intent=new Intent(v.getContext(), ReportInfoActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle viewBundle = new Bundle();

                viewBundle.putString("notificationReportDateTime", report.getNotificationReportDateTime());
                viewBundle.putString("notificationReportType", report.getNotificationReportType());
                viewBundle.putString("notificationReportTitle", report.getNotificationReportTitle());
                viewBundle.putString("notificationReportStatusChangedTo", report.getNotificationReportStatusChangedTo());
                viewBundle.putString("notificationDateTimeChanged", report.getNotificationDateTimeChanged());
                viewBundle.putString("notificationReportBody", report.getNotificationReportBody());
                viewBundle.putString("notificationReportUID", report.getNotificationReportUID());


                intent.putExtras(viewBundle);
                v.getContext().startActivity(intent);
            }
        });
        return listitemView;
    }

    public void printDifference(Date startDate, Date endDate) {
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
            user_report_time_elapsed = weeks+"w ";
        }else if(elapsedDays != 0) {
            user_report_time_elapsed = elapsedDays+"d ";
        }else if(elapsedHours != 0){
            user_report_time_elapsed = elapsedHours+"h ";
        }else if(elapsedMinutes != 0){
            user_report_time_elapsed = elapsedMinutes+"m ";
        }else if(elapsedSeconds != 0){
            user_report_time_elapsed = elapsedSeconds+"s ";
        }

    }

    public static String getTimeDate() { // without parameter argument
        try{
            Date netDate = new Date(); // current time from here
            SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            return sfd.format(netDate);
        } catch(Exception e) {
            return "date";
        }
    }

}
