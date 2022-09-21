package fiek.unipr.mostwantedapp.adapter.report.modified;

import static fiek.unipr.mostwantedapp.utils.Constants.DATE_TIME;
import static fiek.unipr.mostwantedapp.utils.Constants.FAKE;
import static fiek.unipr.mostwantedapp.utils.Constants.UNVERIFIED;
import static fiek.unipr.mostwantedapp.utils.Constants.VERIFIED;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.utils.RecyclerViewInterface;
import fiek.unipr.mostwantedapp.models.NotificationReportUser;

public class ModifiedReportNotificationAdapter extends RecyclerView.Adapter<ModifiedReportNotificationListViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;
    private Context context;
    private String user_report_time_elapsed;
    private List<NotificationReportUser> modifiedReportList;

    public ModifiedReportNotificationAdapter(Context context, List<NotificationReportUser> modifiedReportList, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.modifiedReportList = modifiedReportList;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public ModifiedReportNotificationListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.modified_notification_item, parent, false);
        return new ModifiedReportNotificationListViewHolder(itemView, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ModifiedReportNotificationListViewHolder holder, int position) {
        try {
            holder.user_report_modified_name.setText(context.getText(R.string.your_report_in_datetime)+" "+modifiedReportList.get(position).getNotificationReportDateTime()+" "+context.getText(R.string.has_new_status_right_now));

            String verified = context.getText(R.string.status_of_report_has_changed_to)+" "+
                    modifiedReportList.get(position).getNotificationReportNewStatus()+ " \n"+ context.getText(R.string.you_earn)+20+" "+ "coins\n"+context.getText(R.string.thank_you_for_collaboration);

            String fake = context.getText(R.string.status_of_report_has_changed_to)+" "+
                    modifiedReportList.get(position).getNotificationReportNewStatus()+ " \n"+ context.getText(R.string.please_be_real_in_giving_information_next_time)+", "+
                    context.getText(R.string.thank_you);

            String unverified = context.getText(R.string.status_of_report_has_changed_to)+" "+
                    modifiedReportList.get(position).getNotificationReportNewStatus()+ " \n"+ context.getText(R.string.still_under_review)+", "+
                    context.getText(R.string.thank_you);

            if(modifiedReportList.get(position).getNotificationReportNewStatus().equals(FAKE)){
                holder.user_report_modified_description.setText(fake.substring(0,36)+"...");
            }else if(modifiedReportList.get(position).getNotificationReportNewStatus().equals(VERIFIED)){
                holder.user_report_modified_description.setText(verified.substring(0,36)+"...");
            }else if(modifiedReportList.get(position).getNotificationReportNewStatus().equals(UNVERIFIED)){
                holder.user_report_modified_description.setText(unverified.substring(0,36)+"...");
            }

            //Your report with date: and title: and decription has been changed to

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME);

            Date start_date = simpleDateFormat.parse(modifiedReportList.get(position).getNotificationDateTime());
            Date end_date = simpleDateFormat.parse(getTimeDate());
            printDifference(start_date, end_date);

            if(user_report_time_elapsed != null){
                holder.user_report_modified_time.setText(user_report_time_elapsed);
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return modifiedReportList.size();
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
            SimpleDateFormat sfd = new SimpleDateFormat(DATE_TIME, Locale.getDefault());
            return sfd.format(netDate);
        } catch(Exception e) {
            return "date";
        }
    }

}
