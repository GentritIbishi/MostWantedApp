package fiek.unipr.mostwantedapp.adapter.maps;

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
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import fiek.unipr.mostwantedapp.fragment.admin.update.user.UpdateUserListViewHolder;
import fiek.unipr.mostwantedapp.helpers.RecyclerViewInterface;
import fiek.unipr.mostwantedapp.maps.admin.MapsActivity;
import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.helpers.CircleTransform;
import fiek.unipr.mostwantedapp.models.Person;
import fiek.unipr.mostwantedapp.models.User;

public class MapsLocationListAdapter extends RecyclerView.Adapter<MapsLocationListViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;
    private Context context;
    private List<Person> personLocationList;
    private String time_elapsed;

    public MapsLocationListAdapter(Context context, List<Person> personLocationList, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.personLocationList = personLocationList;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public MapsLocationListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.location_item_single, parent, false);
        return new MapsLocationListViewHolder(itemView, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MapsLocationListViewHolder holder, int position) {
        try {
            // after initializing our items we are
            // setting data to our view.
            // below line is use to set data to our text view.
            holder.tv_location_person_fullName.setText(personLocationList.get(position).getFullName());

            // in below line we are using Picasso to
            // load image from URL in our Image VIew.
            if (personLocationList.get(position).getUrlOfProfile() != null && !personLocationList.get(position).getUrlOfProfile().isEmpty()) {
                Picasso.get().load(personLocationList.get(position).getUrlOfProfile()).transform(new CircleTransform()).into(holder.profile_location_person);
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");

            Date start_date = simpleDateFormat.parse(personLocationList.get(position).getRegistration_date());
            Date end_date = simpleDateFormat.parse(getTimeDate());
            printDifference(start_date, end_date);

            if (time_elapsed != null) {
                holder.tv_location_time_elapsed.setText(time_elapsed);
            }
        }catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return personLocationList.size();
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
