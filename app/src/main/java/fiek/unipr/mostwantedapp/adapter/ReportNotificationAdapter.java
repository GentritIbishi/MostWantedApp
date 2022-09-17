package fiek.unipr.mostwantedapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.fragment.admin.notification.NotificationListViewHolder;
import fiek.unipr.mostwantedapp.fragment.admin.update.user.UpdateUserListViewHolder;
import fiek.unipr.mostwantedapp.helpers.RecyclerViewInterface;
import fiek.unipr.mostwantedapp.models.Report;
import fiek.unipr.mostwantedapp.maps.report.SingleReportActivity;
import fiek.unipr.mostwantedapp.models.User;

public class ReportNotificationAdapter extends RecyclerView.Adapter<NotificationListViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;
    private Context context;
    private List<Report> reportList;
    private static String ANONYMOUS = "ANONYMOUS";
    private String urlOfProfile, user_report_time_elapsed, informer_person;

    public ReportNotificationAdapter(Context context, List<Report> reportList, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.reportList = reportList;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public NotificationListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View notificationItemView = LayoutInflater.from(context)
                .inflate(R.layout.report_notification_item, parent, false);
        return new NotificationListViewHolder(notificationItemView, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationListViewHolder holder, int position) {
        try {
            holder.user_report_name.setText(reportList.get(position).getInformer_person());

            informer_person = holder.user_report_name.getText().toString();

            if (informer_person != null && informer_person.equals(ANONYMOUS)){

                holder.user_reported_image.setImageResource(R.drawable.ic_anonymous);

            }else if(informer_person != null && informer_person.startsWith("+")){

                holder.user_reported_image.setImageResource(R.drawable.ic_phone_login);

            }else if(!informer_person.equals(ANONYMOUS) && !informer_person.startsWith("+")){
                Glide.with(context)
                        .load(reportList.get(position).getInformer_person_urlOfProfile())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                holder.user_reported_image.setImageDrawable(resource);
                                return true;
                            }
                        })
                        .circleCrop()
                        .preload();
            }


            holder.user_report_description.setText(reportList.get(position).getDescription());

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");

            Date start_date = simpleDateFormat.parse(reportList.get(position).getDate_time());
            Date end_date = simpleDateFormat.parse(getTimeDate());
            printDifference(start_date, end_date);

            if(user_report_time_elapsed != null){
                holder.user_report_time.setText(user_report_time_elapsed);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return reportList.size();
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
