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
import fiek.unipr.mostwantedapp.models.Report;
import fiek.unipr.mostwantedapp.maps.report.SingleReportActivity;

public class ReportNotificationAdapter extends ArrayAdapter<Report> {
    // constructor for our list view adapter.

    private CircleImageView user_reported_image;
    private static String ANONYMOUS = "ANONYMOUS";
    private String urlOfProfile, user_report_time_elapsed, informer_person;
    private List<Report> reportList = null;
    private ArrayList<Report> arraylist;

    public ReportNotificationAdapter(@NonNull Context context, ArrayList<Report> reportArrayList) {
        super(context, 0, reportArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // below line is use to inflate the
        // layout for our item of list view.
        View listitemView = convertView;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.report_notification_item, parent, false);
        }

        // after inflating an item of listview item
        // we are getting data from array list inside
        // our modal class.
        Report report = getItem(position);

        // initializing our UI components of list view item.
        TextView user_report_name = listitemView.findViewById(R.id.user_report_name);
        TextView user_report_description = listitemView.findViewById(R.id.user_report_description);
        TextView user_report_time = listitemView.findViewById(R.id.user_report_time);
        user_reported_image = listitemView.findViewById(R.id.user_reported_image);

        try {
            // after initializing our items we are
            // setting data to our view.
            // below line is use to set data to our text view.
            user_report_name.setText(report.getInformer_person());

            informer_person = user_report_name.getText().toString();

            if (informer_person != null && informer_person.equals(ANONYMOUS)){

                user_reported_image.setImageResource(R.drawable.ic_anonymous);

            }else if(informer_person != null && informer_person.startsWith("+")){

                user_reported_image.setImageResource(R.drawable.ic_phone_login);

            }else if(!informer_person.equals(ANONYMOUS) && !informer_person.startsWith("+")){
                // in below line we are using Picasso to
                // load image from URL in our Image VIew.
                Glide.with(getContext())
                        .load(report.getInformer_person_urlOfProfile())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                user_reported_image.setImageDrawable(resource);
                                return true;
                            }
                        })
                        .circleCrop()
                        .preload();
            }


            user_report_description.setText(report.getDescription());

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");

                Date start_date = simpleDateFormat.parse(report.getDate_time());
                Date end_date = simpleDateFormat.parse(getTimeDate());
                printDifference(start_date, end_date);

                if(user_report_time_elapsed != null){
                    user_report_time.setText(user_report_time_elapsed);
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
                Intent intent=new Intent(v.getContext(), SingleReportActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle viewBundle = new Bundle();
                viewBundle.putString("date_time", report.getDate_time());
                viewBundle.putString("title", report.getTitle());
                viewBundle.putString("description", report.getDescription());
                viewBundle.putString("informer_person", report.getInformer_person());
                viewBundle.putString("status", report.getStatus().toString());
                viewBundle.putString("informer_person_urlOfProfile", report.getInformer_person_urlOfProfile());
                viewBundle.putDouble("latitude", report.getLatitude());
                viewBundle.putDouble("longitude", report.getLongitude());
                viewBundle.putString("uID", report.getuID());
                viewBundle.putString("wanted_person", report.getWanted_person());

                try {
                    if(report.getImages().containsValue(null)) {
                        viewBundle.putInt("totalImages", 0);
                        viewBundle.putStringArray("images", null);
                    }else {
                        viewBundle.putInt("totalImages", report.getImages().size());

                        int totalImages = report.getImages().size();

                        Map<String, Object> images;
                        images = (Map<String, Object>) report.getImages();

                        String[] arrImages = new String[totalImages];

                        for(int i = 0; i<totalImages; i++) {
                            arrImages[i] = images.get("image"+i).toString();
                        }

                        viewBundle.putStringArray("images", arrImages);
                    }
                }catch (Exception e) {
                    System.out.println("ERROR"+e.getMessage());
                }

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
