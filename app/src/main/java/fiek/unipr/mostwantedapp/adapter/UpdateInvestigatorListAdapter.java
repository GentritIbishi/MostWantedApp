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

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.fragment.admin.update.investigator.UpdateInvestigatorFragment;
import fiek.unipr.mostwantedapp.fragment.admin.update.person.UpdatePersonFragment;
import fiek.unipr.mostwantedapp.helpers.CircleTransform;
import fiek.unipr.mostwantedapp.models.Investigator;
import fiek.unipr.mostwantedapp.models.Person;

public class UpdateInvestigatorListAdapter extends ArrayAdapter<Investigator> {

    private TextView update_investigator_time_joined, update_investigator_name;
    private ImageView update_investigator_image;
    private String investigator_time_elapsed;

    // constructor for our list view adapter.
    public UpdateInvestigatorListAdapter(@NonNull Context context, ArrayList<Investigator> investigatorArrayList) {
        super(context, 0, investigatorArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // below line is use to inflate the
        // layout for our item of list view.
        View listitemView = convertView;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.update_investigator_item_single, parent, false);
        }

        // after inflating an item of listview item
        // we are getting data from array list inside
        // our modal class.
        Investigator investigator = getItem(position);

        // initializing our UI components of list view item.
        update_investigator_name = listitemView.findViewById(R.id.update_investigator_name);
        update_investigator_time_joined = listitemView.findViewById(R.id.update_investigator_time_joined);
        update_investigator_image = listitemView.findViewById(R.id.update_investigator_image);

        try {
            // after initializing our items we are
            // setting data to our view.
            // below line is use to set data to our text view.
            update_investigator_name.setText(investigator.getFullName());

            // in below line we are using Picasso to
            // load image from URL in our Image VIew.
            if (investigator.getUrlOfProfile() != null && !investigator.getUrlOfProfile().isEmpty()) {
                Picasso.get().load(investigator.getUrlOfProfile()).transform(new CircleTransform()).into(update_investigator_image);
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");

            Date start_date = simpleDateFormat.parse(investigator.getRegistration_date());
            Date end_date = simpleDateFormat.parse(getTimeDate());
            printDifference(start_date, end_date);

            if (investigator_time_elapsed != null) {
                update_investigator_time_joined.setText(investigator_time_elapsed);
            }
        }catch (ParseException e) {
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
                Bundle viewBundle = new Bundle();
                viewBundle.putString("investigator_id", investigator.getInvestigator_id());
                viewBundle.putString("firstName", investigator.getFirstName());
                viewBundle.putString("lastName", investigator.getLastName());
                viewBundle.putString("parentName", investigator.getParentName());
                viewBundle.putString("fullName", investigator.getFullName());
                viewBundle.putString("birthday", investigator.getBirthday());
                viewBundle.putString("address", investigator.getAddress());
                viewBundle.putString("eyeColor", investigator.getEyeColor());
                viewBundle.putString("hairColor", investigator.getHairColor());
                viewBundle.putString("phy_appearance", investigator.getPhy_appearance());
                viewBundle.putString("urlOfProfile", investigator.getUrlOfProfile());
                viewBundle.putString("registration_date", investigator.getRegistration_date());
                viewBundle.putString("age", investigator.getAge());
                viewBundle.putString("gender", investigator.getGender());
                viewBundle.putString("height", investigator.getHeight());
                viewBundle.putString("weight", investigator.getWeight());
                UpdateInvestigatorFragment updateInvestigatorFragment = new UpdateInvestigatorFragment();
                updateInvestigatorFragment.setArguments(viewBundle);
                loadFragment(updateInvestigatorFragment);
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

    private void loadFragment(Fragment fragment) {
        ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.admin_fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }
}
