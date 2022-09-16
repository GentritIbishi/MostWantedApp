package fiek.unipr.mostwantedapp.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.fragment.admin.update.investigator.UpdateInvestigatorFragment;
import fiek.unipr.mostwantedapp.fragment.admin.update.investigator.UpdateInvestigatorListViewHolder;
import fiek.unipr.mostwantedapp.fragment.admin.update.person.UpdatePersonFragment;
import fiek.unipr.mostwantedapp.fragment.admin.update.user.UpdateUserListViewHolder;
import fiek.unipr.mostwantedapp.helpers.CircleTransform;
import fiek.unipr.mostwantedapp.helpers.RecyclerViewInterface;
import fiek.unipr.mostwantedapp.models.Investigator;
import fiek.unipr.mostwantedapp.models.Person;
import fiek.unipr.mostwantedapp.models.User;

public class UpdateInvestigatorListAdapter extends RecyclerView.Adapter<UpdateInvestigatorListViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;
    private Context context;
    private List<Investigator> investigatorList;
    private String investigator_time_elapsed;

    public UpdateInvestigatorListAdapter(Context context, List<Investigator> investigatorList, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.investigatorList = investigatorList;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public UpdateInvestigatorListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View userView = LayoutInflater.from(context)
                .inflate(R.layout.update_investigator_item_single, parent, false);
        return new UpdateInvestigatorListViewHolder(userView, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull UpdateInvestigatorListViewHolder holder, int position) {
        try {
            // after initializing our items we are
            // setting data to our view.
            // below line is use to set data to our text view.
            holder.update_investigator_name.setText(investigatorList.get(position).getFullName());

            // in below line we are using Picasso to
            // load image from URL in our Image VIew.
            if (investigatorList.get(position).getUrlOfProfile() != null && !investigatorList.get(position).getUrlOfProfile().isEmpty()) {
                Picasso.get().load(investigatorList.get(position).getUrlOfProfile()).transform(new CircleTransform()).into(holder.update_investigator_image);
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");

            Date start_date = simpleDateFormat.parse(investigatorList.get(position).getRegistration_date());
            Date end_date = simpleDateFormat.parse(getTimeDate());
            printDifference(start_date, end_date);

            if (investigator_time_elapsed != null) {
                holder.update_investigator_time_joined.setText(investigator_time_elapsed);
            }
        }catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return investigatorList.size();
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
            investigator_time_elapsed = weeks+"w ";
        }else if(elapsedDays != 0) {
            investigator_time_elapsed = elapsedDays+"d ";
        }else if(elapsedHours != 0){
            investigator_time_elapsed = elapsedHours+"h ";
        }else if(elapsedMinutes != 0){
            investigator_time_elapsed = elapsedMinutes+"m ";
        }else if(elapsedSeconds != 0){
            investigator_time_elapsed = elapsedSeconds+"s ";
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
